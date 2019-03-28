package org.tamacat.httpd.webdav;

import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.handler.ReverseHttpRequest;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.StringUtils;

public class WebDavReverseHttpRequest extends ReverseHttpRequest {

	static final Log LOG = LogFactory.getLog(WebDavReverseHttpRequest.class);
	
	public WebDavReverseHttpRequest(HttpRequest request, HttpContext context, ReverseUrl reverseUrl) {
		super(request, context, reverseUrl);
	}

	public void setRequest(HttpRequest request, HttpContext context) {
		rewriteHostHeader(request, context);

		rewriteDestinationHeader(request);
		setHeaders(request.getAllHeaders());
		//setParams(request.getParams());
		ReverseUtils.removeRequestHeaders(this);
	}
	
	/**
	 * <p>Rewrite the Location request headers.
	 * @param response
	 * @param reverseUrl
	 */
	public void rewriteDestinationHeader(HttpRequest request) {
		Header[] destinationHeaders = request.getHeaders("Destination");
		request.removeHeaders("Destination");
		for (Header destination : destinationHeaders) {
			String value = destination.getValue();
			if (StringUtils.isNotEmpty(value) && reverseUrl.getHost() != null) {
				LOG.trace(destination);
				String host = reverseUrl.getHost().toString();
				URL convertUrl = reverseUrl.getReverseUrl(value.replace(host, ""));
				if (convertUrl != null) {
					request.addHeader("Destination", convertUrl.toString());
				} else {
					request.addHeader("Destination", value);
				}
			} else {
				request.addHeader("Destination", value);
			}
		}
	}
}
