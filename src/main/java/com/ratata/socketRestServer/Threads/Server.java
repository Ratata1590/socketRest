package com.ratata.socketRestServer.Threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

public class Server extends Thread {

	private int openPort;
	private String proxyUrl;
	private String destHost;
	private String destPort;
	private String sessionId;

	private ServerSocket serverSocket;

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Socket sock = null;
			try {
				sock = serverSocket.accept();
				String sockRestId = connectRemoteSocket(destHost, destPort, sessionId, sock);
				if (sockRestId == null) {
					sock.close();
					continue;
				}
				SEND send = new SEND();
				send.startSend(proxyUrl, sock, sockRestId);
				RECV recv = new RECV();
				recv.startSend(proxyUrl, sock, sockRestId);
				System.out.println(sock.getInetAddress() + ":" + sock.getPort() + "=>" + openPort + "=>" + proxyUrl
						+ "=>" + destHost + ":" + destPort);
			} catch (Exception e) {
				try {
					sock.close();
				} catch (Exception e1) {
				}
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}

	public void startServer(int openPort, String proxyUrl, String destHost, String destPort, String sessionId)
			throws Exception {
		this.openPort = openPort;
		this.proxyUrl = proxyUrl;
		this.destHost = destHost;
		this.destPort = destPort;
		this.serverSocket = new ServerSocket(openPort);
		this.sessionId = sessionId;
		start();
	}

	private String connectRemoteSocket(String destHost, String destPort, String sessionId, Socket sock) {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(proxyUrl.concat("/socketControl"));
		String sockRestId = null;
		post.addHeader("action", "connect");
		post.addHeader("host", destHost);
		post.addHeader("port", destPort);
		try {
			HttpResponse response;
			int ctry = 5;
			do {
				sockRestId = sessionId.toString() + ":" + getSaltString();
				post.setHeader("sessionId", sockRestId);
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
		return sockRestId;
	}

	public static String getSaltString() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 18) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}
		String saltStr = salt.toString();
		return saltStr;

	}
}
