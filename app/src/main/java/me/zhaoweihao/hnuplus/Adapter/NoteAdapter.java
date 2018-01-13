package me.zhaoweihao.hnuplus.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.zhaoweihao.hnuplus.Bmob.Note;
import me.zhaoweihao.hnuplus.NoteActivity;
import me.zhaoweihao.hnuplus.NoteEditActivity;
import me.zhaoweihao.hnuplus.R;

/**
 * Created by ZhaoWeihao on 2018/1/10.
 */

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> mNoteList;
    private Context mContext;

    public NoteAdapter(List<Note> noteList,Context context) {

        mNoteList = noteList;
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View noteView;
        TextView textView;
        CardView cardView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            noteView = itemView;
            textView = itemView.findViewById(R.id.tv_notes);
            cardView = itemView.findViewById(R.id.cv_notes);
            imageView = itemView.findViewById(R.id.iv_notes);
        }

    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.note_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Note note = mNoteList.get(position);

                    Log.d("NA",String.valueOf(position)+" "+String.valueOf(mNoteList.size()));

                    Intent intent = new Intent(mContext, NoteEditActivity.class);
                    if(position==0){
                        intent.putExtra("data",true);
                    }else {
                        intent.putExtra("data",false);
                        intent.putExtra("objectID",note.getObjectId());
                        intent.putExtra("content",note.getContent());
                        Log.d("NA",note.getContent());
                    }
                    mContext.startActivity(intent);


            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, final int position) {
        Note note = mNoteList.get(position);
        if(position==0){
            holder.imageView.setVisibility(View.VISIBLE);
        }

        holder.textView.setText(note.getContent());
        holder.noteView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(position==0){

                }else {
                    Log.d("NA",String.valueOf(position));
                    if(mContext instanceof NoteActivity){
                        ((NoteActivity)mContext).showDeleteDialog(mNoteList.get(position).getObjectId());
                    }
                }

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

}
