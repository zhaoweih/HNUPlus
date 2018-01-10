package me.zhaoweihao.hnuplus;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import me.zhaoweihao.hnuplus.Adapter.NoteAdapter;
import me.zhaoweihao.hnuplus.Bmob.MyUser;
import me.zhaoweihao.hnuplus.Bmob.Note;

public class NoteActivity extends AppCompatActivity {

    @BindView(R.id.rv_notes)
    RecyclerView recyclerView;
    @BindView(R.id.save)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

//        Button button = new Button(this);
//        button.setText("save");

//        Button button1 = new Button(this);
//        button1.setText("query");
//
//        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll_note);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        linearLayout.addView(button,layoutParams);
//        linearLayout.addView(button1,layoutParams);

        Bmob.initialize(this,"a15e40755375ee7434e6be8c000c184b");

        ButterKnife.bind(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                Note note = new Note();
                note.setContent("text will be saved");
                note.setAuthor(user);

                BmobACL bmobACL = new BmobACL();
                bmobACL.setReadAccess(user,true);
                bmobACL.setWriteAccess(user,true);

                note.setACL(bmobACL);
                note.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(NoteActivity.this, "save successfully", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NoteActivity.this, "save failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
//
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                BmobQuery<Note> bmobQuery = new BmobQuery<Note>();
//                bmobQuery.findObjects(new FindListener<Note>() {
//                    @Override
//                    public void done(List<Note> list, BmobException e) {
//                        if(e==null){
//                            Log.d("NA1",list.get(0).getContent());
//                        }else{
//                            Log.d("NA1","failed");
//                        }
//                    }
//                });
//            }
//        });

        BmobQuery<Note> bmobQuery = new BmobQuery<Note>();
        bmobQuery.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if(e==null){
                    Log.d("NA1",list.get(0).getContent());
                    StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);
                    Note note = new Note();
                    note.setContent("");
                    list.add(note);
                    Collections.reverse(list);
                    NoteAdapter noteAdapter = new NoteAdapter(list,NoteActivity.this);
                    recyclerView.setAdapter(noteAdapter);
                }else{
                    Log.d("NA1","failed");
                }
            }
        });

    }


}
