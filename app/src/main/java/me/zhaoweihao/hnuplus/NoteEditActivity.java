package me.zhaoweihao.hnuplus;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobACL;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.zhaoweihao.hnuplus.Bmob.MyUser;
import me.zhaoweihao.hnuplus.Bmob.Note;

public class NoteEditActivity extends AppCompatActivity {

    @BindView(R.id.et_note)
    EditText editText;

    private boolean isAdd;
    private String contentBefore;
    private String objectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);
        ButterKnife.bind(this);
        setTitle(getString(R.string.edit_title));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        isAdd=intent.getBooleanExtra("data",true);
        if(isAdd){

        }else {
            contentBefore=intent.getStringExtra("content");
            objectID=intent.getStringExtra("objectID");
            editText.setText(contentBefore);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                updateOrSaveData();
                break;
                default:
        }
        return true;
    }

    private void updateOrSaveData(){
        String contentAfter=editText.getText().toString();
        if(isAdd){
            if(contentAfter.equals("")){
                finish();
            }else{
                MyUser user = BmobUser.getCurrentUser(MyUser.class);
                Note note = new Note();
                note.setContent(contentAfter);
                note.setAuthor(user);

                BmobACL bmobACL = new BmobACL();
                bmobACL.setReadAccess(user,true);
                bmobACL.setWriteAccess(user,true);

                note.setACL(bmobACL);
                note.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Toast.makeText(NoteEditActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(NoteEditActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }else {
            if(contentAfter.equals(contentBefore)){
                finish();
            }else{
                Note note = new Note();
                note.setContent(contentAfter);
                note.update(objectID, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            Toast.makeText(NoteEditActivity.this, R.string.update_success, Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(NoteEditActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        updateOrSaveData();
    }
}
