package me.zhaoweihao.hnuplus.Bmob

import cn.bmob.v3.BmobUser

/**
 * Created by ZhaoWeihao on 2017/11/9.
 */

class MyUser : BmobUser() {
    var sex: Boolean? = null
    var nick: String? = null
    var age: Int? = null
}