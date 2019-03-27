package myownview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.EditText;

import com.example.e_notebook.EnotebookHttpClient;

import java.util.ArrayList;
import java.util.List;

import javaclass.IpConfig;

public class PictureAndTextEditorView extends AppCompatEditText {
    private final String TAG = "PATEditorView";
    private Context mContext;
    private List<String> mContentList;

    public static final String mBitmapTag = "☆";
    private String mNewLineTag = "\n";

    public PictureAndTextEditorView(Context context) {
        super(context);
        init(context);
    }

    public PictureAndTextEditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PictureAndTextEditorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mContentList = getmContentList();
        insertData();
    }

    /**
     * insert data
     */
    private void insertData() {
        if (mContentList.size() > 0) {
            for (String str : mContentList) {
                if (str.indexOf(mBitmapTag) != -1) {//judge the path whether point to a picture
                    String path = str.replace(mBitmapTag, "");
                    //insert picture
                    Bitmap bitmap = getSmallBitmap(path, 480, 800);
                    insertBitmap(path, bitmap, 1);
                } else {
                    //insert words
                    SpannableString ss = new SpannableString(str);
                    append(ss);
                }
            }
        }
    }


    /**
     * insert bitmap
     *
     * @param bitmap
     * @param path
     * @return
     */
    private SpannableString insertBitmap(String path, Bitmap bitmap, int flag) {
        Editable edit_text = getEditableText();
        int index = getSelectionStart(); // get cursor pos
        SpannableString newLine = new SpannableString("\n");
        if(flag == 1){
            //insert line break
            edit_text.insert(index, newLine);
        }
        //create a SpannableString Object to be replaced by the ImageSpan Object
//        if(path.indexOf("/") != -1){ //only keep the img name in variable 'path'
//            String[] path_strs = path.split("/");
//            path = path_strs[path_strs.length - 1];
//        }
        path = mBitmapTag + path + mBitmapTag;
        SpannableString spannableString = new SpannableString(path);
        // Create a ImageSpan through a bitmap
        //ImageSpan imageSpan = new ImageSpan(mContext, bitmap);
        ImageSpan imageSpan = new ImageSpan(bitmap);
        // replace the SpannableString with ImageSpan
        spannableString.setSpan(imageSpan, 0, path.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (index < 0 || index >= edit_text.length()) {
            edit_text.append(spannableString);
        } else {
            edit_text.insert(index, spannableString);
        }
        if(flag == 1){
            edit_text.insert(index, newLine);
        }
        return spannableString;
    }


    /**
     * insert the bitmap
     *
     * @param path
     */
    public void insertBitmap(String path, int flag) {
        Bitmap bitmap = getSmallBitmap(path, 480, 800);
        insertBitmap(path, bitmap, flag);
    }

    //insert the bitmap from the server
    public void insertBitmapFromServer(String path, Bitmap bitmap, int flag){
        insertBitmap(path, bitmap, flag);
    }

    //insert the words from server
    public void insertWordsFromServer(String words){
        String realWords = words.replace("**LIML**", mNewLineTag);
        SpannableString ss = new SpannableString(realWords);
        append(ss);
    }

    /**
     * get the content list in the edittext
     *
     * @return
     */
    public List<String> getmContentList() {
        if (mContentList == null) {
            mContentList = new ArrayList<String>();
        }
        String s = getText().toString();
        String content = getText().toString().replaceAll(mNewLineTag, "**LIML**");
        if (content.length() > 0 && content.contains(mBitmapTag)) {
            String[] split = content.split("☆");
            mContentList.clear();
            for (String str : split) {
                mContentList.add(str);
            }
        } else {
            mContentList.add(content);
        }

        return mContentList;
    }

    /**
     * set the content list
     *
     * @param contentList
     */
    public void setmContentList(List<String> contentList) {
        if (mContentList == null) {
            mContentList = new ArrayList<>();
        }
        this.mContentList.clear();
        this.mContentList.addAll(contentList);
        insertData();
    }


    float oldY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldY = event.getY();
                requestFocus();
                break;
            case MotionEvent.ACTION_MOVE:
                float newY = event.getY();
                if (Math.abs(oldY - newY) > 20) {
                    clearFocus();
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    // compress the bitmap
    public Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int w_width = w_screen;
        int b_width = bitmap.getWidth();
        int b_height = bitmap.getHeight();
        int w_height = w_width * b_height / b_width;
        bitmap = Bitmap.createScaledBitmap(bitmap, w_width, w_height, false);
        return bitmap;
    }

    //calculate inSampleSize
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
