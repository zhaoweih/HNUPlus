package me.zhaoweihao.hnuplus.Bmob;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/1/7.
 */

public class Note extends BmobObject {

    private String content;

    private MyUser author;

    public MyUser getAuthor() {
        return author;
    }

    public void setAuthor(MyUser author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
