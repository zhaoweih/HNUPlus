package me.zhaoweihao.hnuplus;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.zhaoweihao.hnuplus.Adapter.NoteAdapter;
import me.zhaoweihao.hnuplus.Bmob.Note;

public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.rv_notes)
    RecyclerView recyclerView;
    @BindView(R.id.ll_note_bottom)
    LinearLayout linearLayout;

    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

//        Bmob.initialize(this,"a15e40755375ee7434e6be8c000c184b");

        ButterKnife.bind(this);

        setTitle("Note");

        updateData();


    }

    private void updateData(){
        BmobQuery<Note> bmobQuery = new BmobQuery<Note>();
        bmobQuery.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if(e==null){
                    Log.d("NA1",list.get(0).getContent());
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setNestedScrollingEnabled(false);
                    Note note = new Note();
                    note.setContent("");
                    list.add(note);
                    Collections.reverse(list);
                    noteAdapter = new NoteAdapter(list,NoteActivity.this);
                    recyclerView.setAdapter(noteAdapter);
                    linearLayout.setVisibility(View.VISIBLE);
                }else{
                    Log.d("NA1","failed");
                }
            }
        });
    }

    public void showDeleteDialog(final String objectID){
        String [] strings = new String[]{"Delete"};
        new AlertDialog.Builder(this)
                .setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            Note note = new Note();
                            note.setObjectId(objectID);
                            note.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e==null){
                                        Toast.makeText(NoteActivity.this, "delete successfully", Toast.LENGTH_SHORT).show();
                                        updateData();
                                    } else {
                                        Toast.makeText(NoteActivity.this, "delete failed", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }
                })
                .create().show();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        updateData();
        Log.d("NA","onRestart");
    }
}
