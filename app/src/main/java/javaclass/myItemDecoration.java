package javaclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.e_notebook.R;

//create the item decoration (draw a rectangle between two items)
public class myItemDecoration extends RecyclerView.ItemDecoration{
    private Paint mPaint;
    private int dividerHeight = 8; //the height of the rect

    public myItemDecoration(Context context){
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.colorAccent)); //set the paint color
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight; //set an offset under the item
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft(); //get the left coordinate
        int right = parent.getWidth() - parent.getPaddingRight(); //get the right coordinate
        int childCnt = parent.getChildCount(); //get the number of items
        //draw the rect
        for(int i = 0; i < childCnt; i++){
            View child = parent.getChildAt(i);
            float top = child.getBottom();
            float bottom = top + dividerHeight;
            c.drawRect(left, top, right, bottom, mPaint);
        }
    }
}
