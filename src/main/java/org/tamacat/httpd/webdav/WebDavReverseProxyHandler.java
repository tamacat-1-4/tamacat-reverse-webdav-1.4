package org.tamacat.httpd.webdav;

import java.net.SocketException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.core.BasicHttpStatus;
import org.tamacat.httpd.core.ClientHttpConnection;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.handler.ReverseProxyHandler;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebDavReverseProxyHandler extends ReverseProxyHandler {

	static final Log LOG = LogFactory.getLog(WebDavReverseProxyHandler.class);

	public WebDavReverseProxyHandler() {
		setAllowMethods(null);
	}

	@Override
	protected HttpResponse forwardRequest(HttpRequest request, HttpResponse response, HttpContext context, ReverseUrl reverseUrl) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(">> " + RequestUtils.getRequestLine(request));
		}
		
		if (reverseUrl == null) {
			throw new ServiceUnavailableException("reverseUrl is null.");
		}
		try {
			context.setAttribute("reverseUrl", reverseUrl);
			HttpContext reverseContext = new BasicHttpContext();
			reverseContext.setAttribute("reverseUrl", reverseUrl);
			//ReverseHttpRequest targetRequest = ReverseHttpRequestFactory.getInstance(request, response, reverseContext, reverseUrl);
			WebDavReverseHttpRequest targetRequest = WebDavReverseHttpRequestFactory.getInstance(request, response, reverseContext, reverseUrl);
			
			targetRequest.setHeader(proxyOrignPathHeader, serviceUrl.getPath()); // v1.1
			
			//forward remote user.
			ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
			try {
				countUp(reverseUrl, context);
				
				httpexecutor.preProcess(targetRequest, httpproc, reverseContext);
				ClientHttpConnection conn = getClientHttpConnection(context, reverseUrl);
				HttpResponse targetResponse = httpexecutor.execute(targetRequest, conn, reverseContext);
				httpexecutor.postProcess(targetResponse, httpproc, reverseContext);
				return targetResponse;
			} finally {
				countDown(reverseUrl, context);
			}
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
				BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase() + " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		}
	}
}
