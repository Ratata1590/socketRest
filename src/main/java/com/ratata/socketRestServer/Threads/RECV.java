package com.ratata.socketRestServer.Threads;

import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class RECV extends Thread {
	private String proxyUrl;
	private Socket sock;
	private String sessionId;

	private int retry = 5;

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(proxyUrl.concat("/socketHandler"));
			get.addHeader("sockRestId", sessionId.toString());
			try {
				HttpResponse response;
				int ctry = retry;
				do {
					response = client.execute(get);
					ctry--;
					if (ctry == 0) {
						throw new Exception("out of retry");
					}
				} while (response.getStatusLine().getStatusCode() != 200);
				response.getEntity().writeTo(sock.getOutputStream());
				sock.getOutputStream().flush();
			} catch (Exception e) {
				disconnectRemoteSocket();
				try {

					sock.close();
				} catch (Exception e2) {
				}
				break;
			}
		}
	}

	public void startSend(String proxyUrl, Socket sock, String sessionId) {
		this.proxyUrl = proxyUrl;
		this.sock = sock;
		this.sessionId = sessionId;
		start();
	}

	private void disconnectRemoteSocket() {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(proxyUrl.concat("/socketControl"));
		post.addHeader("action", "diconnect");
		try {
			HttpResponse response;
			int ctry = 5;
			do {
				post.setHeader("sessionId", sessionId);
				response = client.execute(post);
				ctry--;
				if (ctry == 0) {
					throw new Exception("out of retry");
				}
			} while (response.getStatusLine().getStatusCode() != 200);
		} catch (Exception e) {
			try {
				sock.close();
			} catch (Exception e2) {
			}
		}
	}
}
