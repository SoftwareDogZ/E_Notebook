package javaclass;

import android.graphics.Bitmap;

public class Note{
    private String title;
    private Bitmap bitmap;

    public Note(String title, Bitmap bitmap){
        this.title = title;
        this.bitmap = bitmap;
    }

    public String getTitle(){
        return title;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
