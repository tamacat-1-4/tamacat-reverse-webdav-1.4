/*
 * Copyright (c) 2015 tamacat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class DebugLoggingEntity extends HttpEntityWrapper {

	static final Log LOG = LogFactory.getLog(DebugLoggingEntity.class);

	protected int bufferSize = 8192; //8KB

	public DebugLoggingEntity(HttpEntity entity) {
		super(entity);
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		if (outstream == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}

		InputStream is = wrappedEntity.getContent();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte buff[] = new byte[bufferSize];
			int read;
			while ((read = is.read(buff)) > 0) {
				baos.write(buff, 0, read);
			}

			byte[] body = baos.toByteArray();
			LOG.debug(new String(body));
			outstream.write(body);
		} finally {

		}
	}
}
