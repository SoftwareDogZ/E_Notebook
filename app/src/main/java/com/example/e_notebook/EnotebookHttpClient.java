package com.example.e_notebook;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnotebookHttpClient {

    //Send your content to the Server
    //address: address of Server
    //content: content that user wanna send
    public String send(String address, String content, String mode){
        HttpURLConnection conn = null;
        try{
            URL url = new URL(address);
            conn = (HttpURLConnection)url.openConnection(); //open the connection
            conn.setRequestMethod("POST"); //send something to Server
            conn.setRequestProperty("Content-type", mode);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            OutputStream os = conn.getOutputStream(); //get the outputstream of connection
            os.write(content.getBytes()); //write the content into the outputstream in bytes
            os.flush();
            os.close();
            //you must call this function and then the data would be send to Server
            InputStream is = conn.getInputStream();
            byte[] responseBytes = new byte[1024];
            StringBuilder responseStr = new StringBuilder();
            while(is.read(responseBytes) != -1)
                responseStr.append(new String(responseBytes));

            return responseStr.toString();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }finally {
            //close the connection
            if(conn != null)
                conn.disconnect();
        }
    }
}
