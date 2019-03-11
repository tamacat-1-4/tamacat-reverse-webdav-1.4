package org.tamacat.httpd.webdav;

import java.net.Socket;
import java.net.SocketException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.core.ClientHttpConnection;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.handler.ReverseProxyHandler;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebDavReverseProxyHandler extends ReverseProxyHandler {

	static final Log LOG = LogFactory.getLog(WebDavReverseProxyHandler.class);

	@Override
	public void handle(HttpRequest request, HttpResponse response, HttpContext context) {
		try {
			for (RequestFilter filter : requestFilters) {
				filter.doFilter(request, response, context);
				if (skipRequestFilter(context)) break;
			}
			if (skipDoRequest(context) == false) {
				doRequest(request, response, context);
			}
		} catch (Exception e) {
			e.printStackTrace();
			handleException(request, response, e);
		} finally {
			for (ResponseFilter filter : responseFilters) {
				if (skipResponseFilter(context)) break;
				filter.afterResponse(request, response, context);
			}
		}
	}
	

	/**
	 * Request forwarding to backend server.
	 */
	protected HttpResponse forwardRequest(HttpRequest request, HttpResponse response, HttpContext context, ReverseUrl reverseUrl) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(">> " + RequestUtils.getRequestLine(request));
		}

		if (reverseUrl == null) {
			throw new ServiceUnavailableException("reverseUrl is null.");
		}
		try {
			context.setAttribute("reverseUrl", reverseUrl);
			Socket outsocket = createSocket(reverseUrl);
			if (outsocket == null) throw new SocketException("Can not create socket.");
			ClientHttpConnection conn = (ClientHttpConnection) context.getAttribute(HTTP_OUT_CONN);
			if (conn == null || !conn.isOpen()) {
				conn = new ClientHttpConnection(serviceUrl.getServerConfig());
				conn.bind(outsocket);
			}

			if (LOG.isTraceEnabled()) {
				LOG.trace("Outgoing connection to "	+ outsocket.getInetAddress());
				LOG.trace("request: " + request);
			}
			HttpContext reverseContext = new BasicHttpContext();
			reverseContext.setAttribute("reverseUrl", reverseUrl);
			
			WebDavReverseHttpRequest targetRequest = WebDavReverseHttpRequestFactory
					.getInstance(request, response, reverseContext, reverseUrl);
			
			targetRequest.setHeader(proxyOrignPathHeader, serviceUrl.getPath()); // v1.1
			
			//forward remote user.
			ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
			try {
				countUp(reverseUrl, context);
				
				httpexecutor.preProcess(targetRequest, httpproc, reverseContext);
				HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, reverseContext);
				httpexecutor.postProcess(targetResponse, httpproc, reverseContext);
				//Keep-Alive client connection.
				boolean keepAlive = connStrategy.keepAlive(targetResponse, reverseContext);
				if (keepAlive) {
					context.setAttribute(HTTP_CONN_KEEPALIVE, true);
					context.setAttribute(HTTP_OUT_CONN, conn); //WokerThread close the client connection.
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("client conn keep-alive count:" + conn.getMetrics().getResponseCount() + " - " + conn);
				}
				return targetResponse;
			} finally {
				countDown(reverseUrl, context);
			}
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
					BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase()
							+ " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		}
	}
}
