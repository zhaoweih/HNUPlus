package me.zhaoweihao.hnuplus.Utils;

import com.google.gson.Gson;

import me.zhaoweihao.hnuplus.Gson.Weather;


/**
 * Created by Zhaoweihao on 2018/1/6.
 */

public class Utility {
    public static Weather handleWeatherResponse(String response){
        try{
            Gson gson=new Gson();
            Weather weather=gson.fromJson(response,Weather.class);
            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }
            return null;
    }

}
