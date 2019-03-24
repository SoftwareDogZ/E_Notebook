package javaclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.e_notebook.R;

public class myStaggeredItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private int dividerHeight = 2; //the height of the rect

    public myStaggeredItemDecoration(Context context){
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.myMainColor)); //set the paint color
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight; //set an offset around the item
        outRect.top = dividerHeight;
        outRect.left = dividerHeight;
        outRect.right = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int childCnt = parent.getChildCount(); //get the number of items
        //draw the rect
        for(int i = 0; i < childCnt; i++){
            View child = parent.getChildAt(i);
            //there are four rectangles to draw
            float left1 = child.getLeft() - dividerHeight;
            float right1 = child.getRight() + dividerHeight;
            float top1 = child.getBottom();
            float bottom1 = top1 + dividerHeight;
            c.drawRect(left1, top1, right1, bottom1, mPaint);

            float left2 = child.getLeft() - dividerHeight;
            float right2 = child.getLeft();
            float top2 = child.getTop();
            float bottom2 = child.getBottom();
            c.drawRect(left2, top2, right2, bottom2, mPaint);

            float left3 = child.getLeft() - dividerHeight;
            float right3 = child.getRight() + dividerHeight;
            float top3 = child.getTop() - dividerHeight;
            float bottom3 = child.getTop();
            c.drawRect(left3, top3, right3, bottom3, mPaint);

            float left4 = child.getRight();
            float right4 = child.getRight() + dividerHeight;
            float top4 = child.getTop();
            float bottom4 = child.getBottom();
            c.drawRect(left4, top4, right4, bottom4, mPaint);
        }
    }
}
