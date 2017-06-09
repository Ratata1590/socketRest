package client;

import java.net.Socket;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class CleanUpThread extends Thread {

  private Socket clientSocket;

  @Override
  public void run() {
    while (true) {
      HttpClient client = HttpClientBuilder.create().build();
      HttpGet get = new HttpGet("http://localhost:8080/dynamicCodeRest/socketHandlerDown");
      try {
        HttpResponse response = client.execute(get);
        if (response.getStatusLine().getStatusCode() == 200) {
          response.getEntity().writeTo(clientSocket.getOutputStream());
        }
        clientSocket.getOutputStream().flush();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void setClientSocket(Socket clientSocket) {
    this.clientSocket = clientSocket;
    start();
  }
}
