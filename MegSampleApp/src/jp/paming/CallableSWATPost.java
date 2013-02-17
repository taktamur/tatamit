package jp.paming;

import java.io.*;
import java.util.concurrent.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.mime.*;
import org.apache.http.entity.mime.content.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;

public class CallableSWATPost implements Callable<InputStream> {
	
	private byte[] image;
	public CallableSWATPost(byte [] _images){
		image = _images;
		
	}
	@Override
	public InputStream call() throws Exception {
		// TODO Auto-generated method stub
	    HttpClient objHttp = new DefaultHttpClient();  
	    HttpParams params = objHttp.getParams();  
	    HttpConnectionParams.setConnectionTimeout(params, 30000); //接続のタイムアウト  
	    HttpConnectionParams.setSoTimeout(params, 30000); //データ取得のタイムアウト  
	    String xmlStr = ""; 
	    String sUrl = "http://pbr.gwmj.jp/mucurator/pbrmatching.Servlet"; 
	    InputStream objStream =null;
	    try {  
//	    	HttpClient httpClient = new DefaultHttpClient();
	    	HttpPost post = new HttpPost(sUrl);
	    	MultipartEntity entity = new MultipartEntity();
	    	entity.addPart("application_id",new StringBody("yhd05-i"));
//	        File file = new File("/Users/tak/Desktop/yhd05_hiyoko2.jpg"); 
	        entity.addPart("image", new ByteArrayBody(image, "image/jpeg","tatamit2"));
            post.setEntity(entity);

	        HttpResponse objResponse = objHttp.execute(post);  
	        if (objResponse.getStatusLine().getStatusCode() < 400){  
	            objStream = objResponse.getEntity().getContent(); 
	            /*
	            InputStreamReader objReader = new InputStreamReader(objStream,"UTF-8");  
	            BufferedReader objBuf = new BufferedReader(objReader,10240);  
	            StringBuilder xmlStrBuf = new StringBuilder();  
	            String sLine;  
	            while((sLine = objBuf.readLine()) != null){  
	            	xmlStrBuf.append(sLine);  
	            }  
	            xmlStr = xmlStrBuf.toString();  
	            objStream.close();  
	            */
	        }  
	    } catch (IOException e) {  
	    	return null;
	    }  
	
	    return objStream; 

	}

}
