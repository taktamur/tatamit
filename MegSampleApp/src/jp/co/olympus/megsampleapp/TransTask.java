package jp.co.olympus.megsampleapp;

import java.io.*;
import java.util.concurrent.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;

public class TransTask implements Callable<String> {

	@Override
	public String call() {
	    HttpClient objHttp = new DefaultHttpClient();  
	    HttpParams params = objHttp.getParams();  
	    HttpConnectionParams.setConnectionTimeout(params, 1000); //�ڑ��̃^�C���A�E�g  
	    HttpConnectionParams.setSoTimeout(params, 1000); //�f�[�^�擾�̃^�C���A�E�g  
	    String sReturn = ""; 
	    String sUrl = "http://cda69fl-aqr-app000.c4sa.net"; 
	    try {  
	        HttpGet objGet   = new HttpGet(sUrl);  
	        HttpResponse objResponse = objHttp.execute(objGet);  
	        if (objResponse.getStatusLine().getStatusCode() < 400){  
	            InputStream objStream = objResponse.getEntity().getContent();  
	            InputStreamReader objReader = new InputStreamReader(objStream);  
	            BufferedReader objBuf = new BufferedReader(objReader);  
	            StringBuilder objJson = new StringBuilder();  
	            String sLine;  
	            while((sLine = objBuf.readLine()) != null){  
	                objJson.append(sLine);  
	            }  
	            sReturn = objJson.toString();  
	            objStream.close();  
	        }  
	    } catch (IOException e) {  
	    	return "�ʐM�G���[";
	    }     
	    return sReturn; 
	}


}
