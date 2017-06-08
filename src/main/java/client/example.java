package client;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;

public class example {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		HttpClient client = HttpClientBuilder.create().build();
		File file = new File("C:\\Users\\Ratata1590\\Downloads\\site.zip");
		HttpPost post = new HttpPost("http://localhost:8080/dynamicCodeRest/uploadFile");
		FileBody fileBody = new FileBody(file);
		StringBody stringBody1 = new StringBody("C:\\Users\\Ratata1590\\Desktop\\New folder\\",
				ContentType.MULTIPART_FORM_DATA);
		InputStreamBody uploadFilePart = new InputStreamBody(new FileInputStream(file),
				ContentType.APPLICATION_OCTET_STREAM);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("file", fileBody);
		builder.addPart("path", stringBody1);
		HttpEntity entity = builder.build();
		//
		post.setEntity(entity);
		HttpResponse response = client.execute(post);
	}

}
