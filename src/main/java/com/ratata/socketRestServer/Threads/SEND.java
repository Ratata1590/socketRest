package com.ratata.socketRestServer.Threads;

import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class SEND extends Thread {
	private String proxyUrl;
	private Socket sock;
	private String sessionId;

	private int retry = 5;

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(proxyUrl.concat("/socketHandler"));
			post.addHeader("sockRestId", sessionId.toString());
			byte[] tmpbuff;
			byte[] resultBuff;
			try {
				resultBuff = new byte[sock.getReceiveBufferSize()];
				tmpbuff = new byte[sock.getInputStream().read(resultBuff, 0, resultBuff.length)];
				System.arraycopy(resultBuff, 0, tmpbuff, 0, tmpbuff.length);
				post.setEntity(new ByteArrayEntity(tmpbuff));

				HttpResponse response;
				int ctry = retry;
				do {
					response = client.execute(post);
					ctry--;
					if (ctry == 0) {
						throw new Exception("out of retry");
					}
				} while (response.getStatusLine().getStatusCode() != 200);
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
