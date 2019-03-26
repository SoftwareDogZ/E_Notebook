package com.example.e_notebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javaclass.IpConfig;

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

    //upload picture to server
    public String sendBitmapToServer(String imgURL){
        String[] strings = imgURL.split("/");
        String filename = strings[strings.length - 1];
        HttpURLConnection conn = null;
        String boundary = "**LIML**";
        String end = "\r\n";
        String hyphens = "--";
        try{
            URL url = new URL(new IpConfig().getIpPath()+"getpicture.php");
            conn = (HttpURLConnection)url.openConnection();
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            OutputStream os = conn.getOutputStream();
            os.write(hyphens.getBytes());
            os.write(boundary.getBytes());
            os.write(end.getBytes());
            os.write(("Content-Disposition:form-data;"+"name=\"file\";filename=\""+filename+"\""+end).getBytes());
            os.write(end.getBytes());

            FileInputStream fstream = new FileInputStream(imgURL);
            int buffersize = 8192;
            byte[] buffer = new byte[buffersize];
            int length = -1;
            while((length = fstream.read(buffer)) != -1)
                os.write(buffer, 0, length);

            os.write(end.getBytes());
            os.write((hyphens+boundary+hyphens+end).getBytes());

            fstream.close();
            os.flush();
            os.close();

            InputStream is = conn.getInputStream();
            StringBuffer inBuffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = reader.readLine()) != null)
                inBuffer.append(line);
            return inBuffer.toString();

        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }finally {
            if(conn != null)
                conn.disconnect();
        }
    }
}
