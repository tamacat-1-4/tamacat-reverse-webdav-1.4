package org.tamacat.httpd.webdav;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.handler.ReverseHttpRequestFactory;

public class WebDavReverseHttpRequestFactory extends ReverseHttpRequestFactory {

	public static WebDavReverseHttpRequest getInstance(HttpRequest request, HttpResponse response,
			HttpContext context, ReverseUrl reverseUrl) {
		if (request instanceof HttpEntityEnclosingRequest) {
			return new WebDavReverseHttpEntityEnclosingRequest(request, context, reverseUrl);
		} else {
			return new WebDavReverseHttpRequest(request, context, reverseUrl);
		}
	}
}
