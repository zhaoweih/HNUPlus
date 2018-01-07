package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.datatype.BmobRelation

/**
 * Created by Administrator on 2017/11/9.
 */

class Post : BmobObject() {
    var title: String? = null//帖子标题
    var content: String? = null// 帖子内容
    var author: MyUser? = null//帖子的发布者，这里体现的是一对一的关系，该帖子属于某个用户
    var image: BmobFile? = null//帖子图片
    var likes: BmobRelation? = null//多对多关系：用于存储喜欢该帖子的所有用户
}
