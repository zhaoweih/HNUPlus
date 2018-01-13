package me.zhaoweihao.hnuplus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhaoweihao.hnuplus.Gson.Translate;
import me.zhaoweihao.hnuplus.Utils.HttpUtil;
import me.zhaoweihao.hnuplus.Utils.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Zhaoweihao on 17/7/6.
 * 如果对我的项目有任何疑问可以给我发邮件或者提issues
 * Email:zhaoweihaochn@gmail.com
 * 如果觉得我的项目写得好可以给我star和fork
 * 谢谢！
 */

public class TranslateActivity extends AppCompatActivity {

    @BindView(R.id.word_input)
    EditText editText;
    @BindView(R.id.translate_btn)
    FloatingActionButton button;
    @BindView(R.id.translation_layout)
    LinearLayout translationLayout;
    @BindView(R.id.phonetic_text)
    TextView phoneticText;
    @BindView(R.id.explains_layout)
    LinearLayout explainsLayout;
    @BindView(R.id.query_text)
    TextView queryText;
    @BindView(R.id.web_layout)
    LinearLayout webLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.iv_clear)
    ImageView clearView;
    @BindView(R.id.copy)
    ImageView copyImage;
    @BindView(R.id.share)
    ImageView shareImage;
    @BindView(R.id.translate_title)
    TextView translateTitle;
    @BindView(R.id.explains_title)
    TextView explainsTitle;
    @BindView(R.id.web_title)
    TextView webTitle;
    @BindView(R.id.mixLayout)
    LinearLayout mixLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        setTitle("Translate");

        ButterKnife.bind(this);

        button.setVisibility(View.INVISIBLE);
        clearView.setVisibility(View.INVISIBLE);
        copyImage.setVisibility(View.INVISIBLE);
        shareImage.setVisibility(View.INVISIBLE);
        mixLayout.setVisibility(View.INVISIBLE);

        //删除按钮点击事件
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        //editText输入监控事件，如果有文字输入显示删除按钮和翻译按钮，如果为空则隐藏按钮
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editText.getEditableText().toString().length() != 0){
                    clearView.setVisibility(View.VISIBLE);
                    button.setVisibility(View.VISIBLE);
                }else {
                    clearView.setVisibility(View.INVISIBLE);
                    button.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //翻译按钮监控事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果编辑框输入文字为空则snackbar提示输入文本为空
                if(editText.getText().toString().isEmpty()){
                    Snackbar.make(button,R.string.input_empty, Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    //显示progressbar
                    progressBar.setVisibility(View.VISIBLE);
                    //收起输入法
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(button.getWindowToken(), 0);}
                    //获取输入框的文字内容
                    String word = editText.getText().toString();
                    //获取有道api初始url
                    String url = "http://fanyi.youdao.com/openapi.do?keyfrom=zhaotranslator&key=1681711370&type=data&doctype=json&version=1.1&q=";
                    //okhttp发送网络请求,url+word是初始url拼接word组成api

                    HttpUtil.sendOkHttpRequest(url + word, new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            //网络请求失败，开启子进程，更新页面
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(TranslateActivity.this, R.string.translate_fail, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            //网络请求成功，获取json数据
                            String responseData = response.body().string();
                            //利用gson处理json数据，转化为对象
                            try{
                                final Translate translate = Utility.handleTranslateResponse(responseData);
                                //开启子进程更新界面
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //json数据中errorcode为0表示获取数据成功,20代表文本过长,其他就是出现错误，具体可查有道api使用事项
                                        if (translate.getErrorCode() == 0) {
                                            //将数据显示在界面上
                                            showTranslateInfo(translate);
                                        } else if (translate.getErrorCode() == 20) {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(TranslateActivity.this, R.string.translate_overlong, Toast.LENGTH_SHORT).show();
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(TranslateActivity.this, R.string.translate_fail, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }
            }
        });
    }



    private void showTranslateInfo(final Translate translate){
        //先清空layout里面的东西
        translationLayout.removeAllViews();
        explainsLayout.removeAllViews();
        webLayout.removeAllViews();
        //收藏成功按钮隐藏
//        collectDoneImage.setVisibility(View.INVISIBLE);
        //设置title
        translateTitle.setText(R.string.translate_title);
        explainsTitle.setText(R.string.explains_title);
        webTitle.setText(R.string.web_title);
        //将请求回来的数据显示在textview上
        for (int i = 0;i<translate.getTranslation().length;i++)
        {
            View view= LayoutInflater.from(this).inflate(R.layout.translation_item,translationLayout,false);
            TextView translateText= (TextView) view.findViewById(R.id.translation_text);
            translateText.setText(translate.getTranslation()[i]);
            translateText.setTextColor(getResources().getColor(R.color.white));
            translateText.setTextSize(25);
            translationLayout.addView(view);
        }

        queryText.setText(translate.getQuery());
        if(translate.getBasic()==null){
            phoneticText.setVisibility(View.INVISIBLE);
            explainsLayout.setVisibility(View.INVISIBLE);
        }else {

        phoneticText.setText("["+translate.getBasic().getPhonetic()+"]");
            for (int i = 0;i<translate.getBasic().getExplains().length;i++){
                View view= LayoutInflater.from(this).inflate(R.layout.explains_item,explainsLayout,false);
                TextView explainsText= (TextView) view.findViewById(R.id.expalins_text);
                explainsText.setText(translate.getBasic().getExplains()[i]);
                explainsLayout.addView(view);
                phoneticText.setVisibility(View.VISIBLE);
                explainsLayout.setVisibility(View.VISIBLE);
            }
        }


        if(translate.getWeb()==null){
            webLayout.setVisibility(View.INVISIBLE);
        }else {

        for (int i = 0;i<translate.getWeb().size();i++){
            View view= LayoutInflater.from(this).inflate(R.layout.web_item,webLayout,false);
            TextView keyText= (TextView) view.findViewById(R.id.key_text);
            TextView valueText= (TextView) view.findViewById(R.id.value_text);
            keyText.setText(translate.getWeb().get(i).getKey());
            String values=getFinalValue(translate.getWeb().get(i).getValue());
            valueText.setText(values);
            webLayout.addView(view);
            webLayout.setVisibility(View.VISIBLE);
        }
        }
        //显示好后隐藏progressbar
        progressBar.setVisibility(View.GONE);
        //复制按钮监听事件
        copyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", translate.getTranslation()[0]);
                manager.setPrimaryClip(clipData);
                Snackbar.make(button,R.string.copy_success, Snackbar.LENGTH_SHORT)
                        .show();
            }
        });
        //分享按钮监听事件
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND).setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,translate.getTranslation()[0]);
                startActivity(Intent.createChooser(intent,getString(R.string.share_choice)));
            }
        });

        copyImage.setVisibility(View.VISIBLE);
        shareImage.setVisibility(View.VISIBLE);
        mixLayout.setVisibility(View.VISIBLE);
    }

    private String getFinalValue(String[] value) {
        String finalValue="";
        for (int i = 0;i<value.length;i++){
            if(i==value.length-1){
                finalValue=finalValue+value[i];
            }else {
                finalValue = finalValue + value[i] + ",";
            }
        }
        return finalValue;
    }

}
