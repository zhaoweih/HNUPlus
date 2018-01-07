package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobObject

/**
 * Created by Administrator on 2017/11/9.
 */

class Comment : BmobObject() {
    var content: String? = null//评论内容
    var user: MyUser? = null//评论的用户，Pointer类型，一对一关系
    var post: Post? = null //所评论的帖子，这里体现的是一对多的关系，一个评论只能属于一个微博
}
