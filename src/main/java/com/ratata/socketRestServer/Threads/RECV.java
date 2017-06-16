package com.ratata.socketRestServer.Threads;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class RECV extends LinkAbstract {
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      CloseableHttpClient client = HttpClientBuilder.create().build();
      HttpGet get = new HttpGet(this.proxyUrl.concat("/socketHandler"));
      get.addHeader("sockRestId", sessionId);
      try {
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() != 200) {
          throw new Exception("status not 200");
        }
        if (response.getEntity() != null) {
          response.getEntity().writeTo(sock.getOutputStream());
          sock.getOutputStream().flush();
          response.getEntity().getContent().close();
        } else {
          Thread.sleep(1000);
        }
        get.releaseConnection();
        client.close();
      } catch (Exception e) {
        disconnectRemoteSocket();
        break;
      }
    }
  }
}
