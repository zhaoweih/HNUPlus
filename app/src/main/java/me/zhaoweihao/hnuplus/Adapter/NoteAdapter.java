package me.zhaoweihao.hnuplus.Adapter;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import me.zhaoweihao.hnuplus.Bmob.Note;
import me.zhaoweihao.hnuplus.R;

/**
 * Created by Administrator on 2018/1/10.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> mNoteList;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CardView cardView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_notes);
            cardView = itemView.findViewById(R.id.cv_notes);
            imageView = itemView.findViewById(R.id.iv_notes);
        }
    }

    public NoteAdapter(List<Note> noteList,Context context) {

        mNoteList = noteList;
        mContext = context;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        Note note = mNoteList.get(position);
        if(position==0){
            holder.imageView.setVisibility(View.VISIBLE);
        }

        holder.textView.setText(note.getContent());
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

}
