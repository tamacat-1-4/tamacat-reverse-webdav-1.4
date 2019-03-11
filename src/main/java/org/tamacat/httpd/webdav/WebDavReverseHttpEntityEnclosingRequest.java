package org.tamacat.httpd.webdav;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.util.RequestUtils;

public class WebDavReverseHttpEntityEnclosingRequest
		extends WebDavReverseHttpRequest implements HttpEntityEnclosingRequest {

	private HttpEntity entity;
	
	/**
	 * <p>Constructs with the original request of {@link HttpRequest}.
	 * @param request
	 * @param reverseUrl
	 */
	public WebDavReverseHttpEntityEnclosingRequest(HttpRequest request, HttpContext context, ReverseUrl reverseUrl) {
		super(request, context, reverseUrl);
		entity = RequestUtils.getEntity(request);
	}

	@Override
    public HttpEntity getEntity() {
        return this.entity;
    }
	
	@Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public boolean expectContinue() {
        Header expect = getFirstHeader(HTTP.EXPECT_DIRECTIVE);
        return expect != null && HTTP.EXPECT_CONTINUE.equalsIgnoreCase(expect.getValue());
    }
}
