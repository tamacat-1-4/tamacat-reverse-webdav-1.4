/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;

public class DebugResponseInterceptor implements HttpResponseInterceptor {
	
	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			response.setEntity(new DebugLoggingEntity(entity));
		}
	}
}
