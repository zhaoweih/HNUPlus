package me.zhaoweihao.hnuplus;

import android.app.Fragment;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.taishi.flipprogressdialog.FlipProgressDialog;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import me.zhaoweihao.hnuplus.Adapter.CommentAdapter;
import me.zhaoweihao.hnuplus.Interface.CommentInterface;
import me.zhaoweihao.hnuplus.Bmob.Comment;
import me.zhaoweihao.hnuplus.Bmob.MyUser;
import me.zhaoweihao.hnuplus.Bmob.Post;
import me.zhaoweihao.hnuplus.Utils.Utility;

/**
 * Created by ZhaoWeihao on 2017/11/10.
 */

public class CommentFragment extends Fragment implements CommentInterface {

    /**
     * Butter Knife
     */
    @BindView(R.id.rv_comments) RecyclerView recyclerView;
    @BindView(R.id.tv_comment_author) TextView commentAuthorTextView;
    @BindView(R.id.tv_comment_content) TextView commentContentTextView;
    @BindView(R.id.iv_comment_image) ImageView commentImageView;
    @BindView(R.id.fl_delete) FrameLayout deleteFrameLayout;

    private LinearLayoutManager layoutManager;
    private CommentAdapter adapter;
    private ProgressDialog progressDialog;
    private String objectID,authorObjectID,imageUrl;
    private MyUser user;
    private FlipProgressDialog flipProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View commentLayout = inflater.inflate(R.layout.comment_layout,
                container, false);

        ButterKnife.bind(this, commentLayout);

        user = BmobUser.getCurrentUser(MyUser.class);
        //Get data from CommunityFragment
        Intent intent = getActivity().getIntent();
        commentAuthorTextView.setText(intent.getStringExtra("author"));
        commentContentTextView.setText(intent.getStringExtra("content"));
        objectID = intent.getStringExtra("objectID");
        authorObjectID = intent.getStringExtra("authorObjectID");
        //If imageUrl is null,it means no photo in this post
        if (intent.getStringExtra("imageUrl") == null){

        }else{
            Log.d("CF",intent.getStringExtra("imageUrl"));
            Glide.with(this).load(intent.getStringExtra("imageUrl")).into(commentImageView);
            imageUrl = intent.getStringExtra("imageUrl");
        }
        //If this imageview clicked,go to PhotoViewActivity for scale look
        commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getActivity(),PhotoViewActivity.class);
                intent1.putExtra("imageUrl",imageUrl);
                startActivity(intent1);
            }
        });
        //If this user equals to the post author,show delete button
        if(user == null){
            deleteFrameLayout.setVisibility(View.GONE);
        } else if(user.getObjectId().equals(authorObjectID)){
            deleteFrameLayout.setVisibility(View.VISIBLE);
        }else{
            deleteFrameLayout.setVisibility(View.GONE);
        }

        deleteFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder (getActivity());
                dialog.setTitle(R.string.comment_delete_title);
                dialog.setMessage(R.string.comment_delete_message);
                dialog.setCancelable(false);
                dialog.setPositiveButton(R.string.comment_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Post p = new Post();
                        p.setObjectId(objectID);
                        p.delete( new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    Toast.makeText(getActivity(), R.string.delete_success, Toast.LENGTH_SHORT).show();
                                    getActivity().finish();
                                }else{
                                    Toast.makeText(getActivity(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
                                }
                            }

                        });
                    }
                });
                dialog.setNegativeButton(R.string.delete_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                dialog.show();

            }
        });

        flipProgressDialog=Utility.myDialog();
        flipProgressDialog.show(getFragmentManager(),"");

        refreshCommentData(objectID);

        return commentLayout;
    }
    //Refresh comments's data
    private void refreshCommentData(String objectID){

        BmobQuery<Comment> query = new BmobQuery<Comment>();
        Post post = new Post();
        post.setObjectId(objectID);
        query.addWhereEqualTo("post",new BmobPointer(post));
        query.include("user,post.author");
        query.findObjects(new FindListener<Comment>() {

            @Override
            public void done(List<Comment> objects,BmobException e) {
                if(objects.size()==0){
                    flipProgressDialog.dismiss();
                }else{
                Collections.reverse(objects);
                recyclerView.setNestedScrollingEnabled(false);
                layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                adapter = new CommentAdapter(objects);
                recyclerView.setAdapter(adapter);
                flipProgressDialog.dismiss();}
            }
        });
    }

    public void myAction(String data) {

        if(user==null){

            Snackbar.make(recyclerView, R.string.not_signin_text,Snackbar.LENGTH_SHORT)
                    .setAction("Sign in", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),SigninActivity.class);
                            startActivity(intent);
                        }
                    }).show();

        }else if(data.equals("")){

            Toast.makeText(getActivity(), R.string.empty_text, Toast.LENGTH_SHORT).show();

        }else{

            flipProgressDialog.show(getFragmentManager(),"");

            Post post = new Post();
            post.setObjectId(objectID);
            final Comment comment = new Comment();
            comment.setContent(data);
            comment.setPost(post);
            comment.setUser(user);
            comment.save(new SaveListener<String>() {

                @Override
                public void done(String objectId,BmobException e) {
                    if(e==null){
                        refreshCommentData(objectID);
                        Toast.makeText(getActivity(), R.string.add_commit_success, Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getActivity(), R.string.add_commit_failed, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
