package me.zhaoweihao.hnuplus.Interface

import android.net.Uri

/**
 * Created by ZhaoWeihao on 2017/12/3.
 */
/**
 * Help PostActivity call PostFragment's public method
 */
interface PostInterface {
    fun myMethod()
    fun showImage(imageUri: Uri)
}