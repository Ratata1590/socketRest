package com.ratata.socketRestServer.Threads;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class RECV extends LinkAbstract {
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(this.proxyUrl.concat("/socketHandler"));
			get.addHeader("sockRestId", sessionId);
			try {
				HttpResponse response = null;
				int ctry = retry;
				while (!Thread.currentThread().isInterrupted()) {
					response = client.execute(get);
					if (response.getStatusLine().getStatusCode() == 200) {
						break;
					}
					get.releaseConnection();
					ctry--;
					if (ctry == 0) {
						throw new Exception("out of retry");
					}
				}
				if (response.getEntity() != null) {
					response.getEntity().writeTo(sock.getOutputStream());
					sock.getOutputStream().flush();
					response.getEntity().getContent().close();
				} else {
					Thread.sleep(1000);
				}
				get.releaseConnection();
			} catch (Exception e) {
				disconnectRemoteSocket();
				break;
			}
		}
	}
}
