package com.ratata.socketRestServer.Threads;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class ServerMirror extends Thread {

	private int retryNumber = 5;

	private int openPort;
	private String proxyUrl;
	private String botName;
	private String destHost;
	private String destPort;

	private ServerSocket serverSocket;

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Socket sock = null;
			try {
				sock = serverSocket.accept();
				String sockRestIds = connectRemoteMirrorSocket(destHost, destPort, sock);
				if (sockRestIds == null) {
					sock.close();
					continue;
				}
				String[] sockRestId = sockRestIds.split(":");
				SEND send = new SEND();
				send.startSend(proxyUrl.concat("/mirror"), sock, sockRestId[0]);
				RECV recv = new RECV();
				recv.startSend(proxyUrl.concat("/mirror"), sock, sockRestId[1]);
				System.out.println(sock.getInetAddress() + ":" + sock.getPort() + "=>" + openPort + "=>" + proxyUrl
						+ "=>" + botName + "=>" + destHost + ":" + destPort);
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

	public void startServer(int openPort, String proxyUrl, String botName, String destHost, String destPort)
			throws Exception {
		this.openPort = openPort;
		this.proxyUrl = proxyUrl;
		this.botName = botName;
		this.destHost = destHost;
		this.destPort = destPort;
		this.serverSocket = new ServerSocket(openPort);
		start();
	}

	private String connectRemoteMirrorSocket(String destHost, String destPort, Socket sock) {
		String sockRestId = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(proxyUrl.concat("/botGetConnection"));
		post.addHeader("botName", botName);
		try {
			HttpResponse response = null;
			int ctry = retryNumber;
			while (!Thread.currentThread().isInterrupted()) {
			  System.out.println("connectRemoteMirrorSocket");
				response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200) {
					break;
				}
				post.releaseConnection();
				ctry--;
				if (ctry == 0) {
					throw new Exception("out of retry");
				}
			}
			sockRestId = EntityUtils.toString(response.getEntity());
			System.out.println("connectRemoteMirrorSocket:"+sockRestId);
			post.releaseConnection();
			sendConfigToBot(sockRestId);
		} catch (Exception e) {
			try {
				sock.close();
			} catch (Exception e2) {
			}
		}
		return sockRestId;
	}

	private void sendConfigToBot(String sockRestIds) throws Exception {
		String[] sockRestId = sockRestIds.split(":");
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(proxyUrl.concat("/mirror").concat("/socketHandler"));
		post.addHeader("sockRestId", sockRestId[0]);
		post.setEntity(new StringEntity(this.destHost + ":" + this.destPort + ":" + sockRestId[1]));
		HttpResponse response;
		int ctry = retryNumber;
		while (!Thread.currentThread().isInterrupted()) {
			response = client.execute(post);
			System.out.println("sendConfigToBot"+sockRestIds);
			if (response.getStatusLine().getStatusCode() == 200) {
				break;
			}
			post.releaseConnection();
			ctry--;
			if (ctry == 0) {
				throw new Exception("out of retry");
			}
		}
	}
}
