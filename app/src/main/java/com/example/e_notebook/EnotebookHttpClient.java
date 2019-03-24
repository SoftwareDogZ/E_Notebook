package com.example.e_notebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line);
            }

            return buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }finally {
            //close the connection
            if(conn != null)
                conn.disconnect();
        }
    }

    //download image from server
    public Bitmap getBitmapFromServer(String url){
        if(url.isEmpty()) return null;
        URL imgURL;
        Bitmap bitmap = null;
        try{
            imgURL = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imgURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
