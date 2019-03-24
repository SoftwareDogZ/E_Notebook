package javaclass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_notebook.EnotebookHttpClient;
import com.example.e_notebook.NoteBooks;
import com.example.e_notebook.Notes;
import com.example.e_notebook.R;
import com.example.e_notebook.UserNoteContent;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> mNoteList;
    private Context mContext;

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        View NoteView;
        ImageView NoteImageView;
        TextView NoteTextView;

        public NoteViewHolder(View v){
            super(v);
            NoteView = v;
            NoteImageView = (ImageView)v.findViewById(R.id.note_image);
            NoteTextView = (TextView)v.findViewById(R.id.note_text);
        }
    }

    public NotesAdapter(List<Note> NoteList, Context context){
        mNoteList = NoteList;
        mContext = context;
    }


    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_view, viewGroup, false);
        NoteViewHolder noteViewHolder = new NoteViewHolder(view);
        return noteViewHolder;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder noteViewHolder, int i) {
        Note note = mNoteList.get(i);
        noteViewHolder.NoteImageView.setImageBitmap(note.getBitmap());
        noteViewHolder.NoteTextView.setText(note.getTitle());
        noteViewHolder.NoteView.setBackgroundColor(R.color.myMainColor);

        noteViewHolder.NoteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserNoteContent.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }
}
