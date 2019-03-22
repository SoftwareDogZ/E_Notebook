package javaclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_notebook.R;

import java.util.List;


public class NotebooksAdapter extends RecyclerView.Adapter<NotebooksAdapter.ViewHolder> {

    private List<Notebook> mNotebookList;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        View NotebookView;
        ImageView NotebookImageView;
        TextView NotebookNameView;

        public ViewHolder(View v){
            super(v);
            NotebookView = v;
            NotebookImageView = (ImageView)v.findViewById(R.id.notebook_image);
            NotebookNameView = (TextView)v.findViewById(R.id.notebook_name);
        }
    }

    public NotebooksAdapter(List<Notebook> NotebookList){
        mNotebookList = NotebookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notebook_view, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.NotebookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Notebook notebook = mNotebookList.get(i);
        viewHolder.NotebookImageView.setImageResource(notebook.getImageId());
        viewHolder.NotebookNameView.setText(notebook.getName());
    }

    @Override
    public int getItemCount() {
        return mNotebookList.size();
    }
}

