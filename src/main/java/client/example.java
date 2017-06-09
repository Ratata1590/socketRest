package client;

import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

public class example {

  public static void main(String[] args) throws Exception {

    ServerSocket serverSocket = new ServerSocket(1521);
    Socket clientSocket = serverSocket.accept();
    CleanUpThread recv = new CleanUpThread();
    recv.setClientSocket(clientSocket);
    byte[] tmpbuff = new byte[0];
    while (true) {
      // TODO Auto-generated method stub
      HttpClient client = HttpClientBuilder.create().build();
      // File file = new File("C:\\Users\\Ratata1590\\Downloads\\site.zip");
      HttpPost post = new HttpPost("http://localhost:8080/dynamicCodeRest/socketHandlerUp");
      // FileBody fileBody = new FileBody(file);

      // MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      // builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      // builder.addPart("file", fileBody);
      // builder.addPart("path", stringBody1);
      // HttpEntity entity = builder.build();
      //
      byte[] resultBuff = new byte[clientSocket.getReceiveBufferSize()];
      tmpbuff = new byte[clientSocket.getInputStream().read(resultBuff, 0, resultBuff.length)];
      System.arraycopy(resultBuff, 0, tmpbuff, 0, tmpbuff.length);
      String data = new String(tmpbuff);
      System.out.println(data);

      post.setEntity(new ByteArrayEntity(tmpbuff));
      client.execute(post);

      // response.getEntity().writeTo(clientSocket.getOutputStream());
      // clientSocket.getOutputStream().flush();


    }
  }

}
