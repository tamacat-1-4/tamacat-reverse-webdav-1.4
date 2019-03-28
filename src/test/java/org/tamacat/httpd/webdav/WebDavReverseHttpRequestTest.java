package org.tamacat.httpd.webdav;

import static org.junit.Assert.*;

import java.net.URL;

import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServerConfig;
import org.tamacat.httpd.config.ServiceConfig;
import org.tamacat.httpd.config.ServiceType;
import org.tamacat.httpd.config.ServiceUrl;

public class WebDavReverseHttpRequestTest {

	ReverseUrl reverseUrl;
	ServiceUrl url;
	ServerConfig config;

	@Before
	public void setUp() throws Exception {
		config = new ServerConfig();
		ServiceConfig serviceConfig	= new ServiceConfig();

		ServiceUrl serviceUrl = new ServiceUrl(config);
		serviceUrl.setHandlerName("ReverseHandler");
		serviceUrl.setPath("/test2/");
		serviceUrl.setType(ServiceType.REVERSE);

		reverseUrl = new DefaultReverseUrl(serviceUrl);
		reverseUrl.setReverse(new URL("http://localhost:8080/test/"));
		serviceUrl.setReverseUrl(reverseUrl);
		serviceConfig.addServiceUrl(serviceUrl);

		url = serviceConfig.getServiceUrl("/test2/");
		reverseUrl = url.getReverseUrl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRewriteDestinationHeader() {
		WebDavReverseHttpRequest request = new WebDavReverseHttpRequest(
				new BasicHttpRequest("GET","/test2/test.txt"),
				new BasicHttpContext(),
				reverseUrl);
		
		request.setHeader(HTTP.TARGET_HOST, "www.example.com:8080");
		request.setHeader("Destination", "localhost");
		
		request.rewriteDestinationHeader(request);
		assertEquals("localhost", request.getFirstHeader("Destination").getValue());
	}

}
