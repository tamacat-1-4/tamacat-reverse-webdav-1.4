/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

public class DebugRequestInterceptor implements HttpRequestInterceptor {
	
	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
			if (entity != null) {
				((HttpEntityEnclosingRequest)request).setEntity(new DebugLoggingEntity(entity));
			}
		}
	}
}
