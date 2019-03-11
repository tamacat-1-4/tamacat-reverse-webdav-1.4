package org.tamacat.httpd.webdav;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.tamacat.httpd.core.StandardHttpRequestFactory;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class WebDavHttpRequestFactory extends StandardHttpRequestFactory {
	static final Log LOG = LogFactory.getLog(WebDavHttpRequestFactory.class);

	static final String[] RFC2616_COMMON_METHODS = {
		"GET"
	};

	static final String[] RFC2616_ENTITY_ENC_METHODS = {
		"POST", "PUT"
	};

	static final String[] RFC2616_SPECIAL_METHODS = {
		"HEAD", "TRACE", "CONNECT" //, "OPTIONS", "DELETE"
	};

	static final String[] WEBDAV_SUPPORT_METHODS = {
		"PROPFIND", "PROPPATCH", "MKCOL", "COPY", "MOVE", "LOCK", "UNLOCK"
	};

	static final String[] SVN_SUPPORT_METHODS = {
		"DELETE", "OPTIONS","REPORT", "MKWORKSPACE", "MKACTIVITY", "CHECKIN", "CHECKOUT", "MERGE",
		"ORDERPATCH","SUBSCRIBE","UNSUBSCRIBE","POLL","SEARCH","UNCHECKOUT","LABEL","UPDATE",
		"VERSION-CONTROL","WORKING-RESOURCE","BASELINE","BASELINE-CONTROL",
		"ACTIVITY","VERSION-CONTROLLED-COLLECTION","ACL",
		"DEPTH","MARGEINFO","LOG-REVPROPS","REBIND","UNBIND","BIND"
	};

	static boolean isOneOf(final String[] methods, final String method) {
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].equalsIgnoreCase(method)) {
				return true;
			}
		}
		return false;
	}

	public HttpRequest newHttpRequest(RequestLine requestline)
			throws MethodNotSupportedException {
		LOG.trace(requestline);
		if (requestline == null) {
			throw new IllegalArgumentException("Request line may not be null");
		}
		String method = requestline.getMethod();
		requestline = RequestUtils.getRequestLine(requestline);
		if (isOneOf(RFC2616_COMMON_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else if (isOneOf(WEBDAV_SUPPORT_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else if (isOneOf(SVN_SUPPORT_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(requestline);
		} else {
			System.out.println("??? : "+requestline);
			return new BasicHttpEntityEnclosingRequest(requestline);
			//throw new MethodNotSupportedException(method +  " method not supported");
		}
	}

	public HttpRequest newHttpRequest(final String method, String uri)
			throws MethodNotSupportedException {
		LOG.trace(method + " " + uri);
		if (isOneOf(RFC2616_COMMON_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else if (isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else if (isOneOf(RFC2616_SPECIAL_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else if (isOneOf(WEBDAV_SUPPORT_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else if (isOneOf(SVN_SUPPORT_METHODS, method)) {
			return new BasicHttpEntityEnclosingRequest(method, uri);
		} else {
			System.out.println("??? : "+method+" "+uri);
			return new BasicHttpEntityEnclosingRequest(method, uri);
			//throw new MethodNotSupportedException(method +  " method not supported");
		}
	}
}
