package org.tamacat.httpd.webdav;

import java.util.List;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class Client_test {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		Sardine sardine = SardineFactory.begin("guest","password");
		List<DavResource> resources = sardine.getResources("http://localhost:1080/svn/test/");
		for (DavResource res : resources) {
			System.out.println(res);
		}
	}

}
