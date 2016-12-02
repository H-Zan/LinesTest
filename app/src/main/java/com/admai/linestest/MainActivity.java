package com.admai.linestest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.admai.linestest.browser.WebViewUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    
    private static final String TAG = "Today24HourView";
    private IndexHorizontalScrollView indexHorizontalScrollView;
    private Today24HourView today24HourView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        indexHorizontalScrollView = (IndexHorizontalScrollView)findViewById(R.id.indexHorizontalScrollView);
        today24HourView = (Today24HourView)findViewById(R.id.today24HourView);
        indexHorizontalScrollView.setToday24HourView(today24HourView);
//        LinearLayout ll_chart = (LinearLayout) findViewById(R.id.ll_chart);
//        ReDrawLineChartView chartView = new ReDrawLineChartView(this);
//        ll_chart.addView(chartView);
    
    }
    
    
    public void btnClick(View view) {
        int maxTemp = 30;
        int minTemp = -10;
        int maxWindy = 16;
        int minWindy = 1;
        switch (view.getId()) {
        
            case R.id.btn_weather:
                changeWeather(maxTemp, minTemp, maxWindy, minWindy);
                break;
        
            //改变时间的时候数据也要重新传入,改变时间要重新拉取数据呢??
            case R.id.btn_time: //把时间与温度绑定??
                setTime();
                break;
         case R.id.btn_w: //把时间与温度绑定??
             WebViewUtils.openPageInSDK(this,"http://yun.rili.cn/wnl/m/index.html"," ");
                break;
        
            default:
            
                break;
        }
    }
    
    private void setTime() {
        DateFormat df = new SimpleDateFormat("HH:mm");
        String time = df.format(new Date());
        int hour = Integer.valueOf(time.substring(0, 2));
        int second = Integer.valueOf(time.substring(3, 5));
        if (second > 40) {
            hour = hour+1;
        }
        Log.e(TAG, "time:" + time + ",hour:" + hour + ",second:" + second);
        //接下来把 时间=hour 的下标改为现在..
        today24HourView.setTime(hour);
    }
    
    
    private void changeWeather(int maxTemp, int minTemp, int maxWindy, int minWindy) {
        ArrayList<Integer> listTemp = new ArrayList<>();
        ArrayList<Integer> listWindy = new ArrayList<>();
        ArrayList<Integer> listWeatherRes = new ArrayList<>();
        Integer[] weather = {-1, R.mipmap.w0, R.mipmap.w1, R.mipmap.w2, -1,
                             R.mipmap.w3, R.mipmap.w4, R.mipmap.w5, R.mipmap.w6, R.mipmap.w7,
                             R.mipmap.w8, R.mipmap.w9, -1, R.mipmap.w10, -1,
                             R.mipmap.w13, R.mipmap.w14, R.mipmap.w15, R.mipmap.w16, R.mipmap.w17,
                             R.mipmap.w18, -1, R.mipmap.w19, R.mipmap.sun_loading, R.mipmap.sunrise,
                             R.mipmap.sunset}; 
        Integer[] weather2 = {R.mipmap.w0, R.mipmap.w1, R.mipmap.w2, 
                             R.mipmap.w3, R.mipmap.w4, R.mipmap.w5, R.mipmap.w6, R.mipmap.w7,
                             R.mipmap.w8, R.mipmap.w9,  R.mipmap.w10, 
                             R.mipmap.w13, R.mipmap.w14, R.mipmap.w15, R.mipmap.w16, R.mipmap.w17,
                             R.mipmap.w18, R.mipmap.w19, R.mipmap.sun_loading, R.mipmap.sunrise,
                             R.mipmap.sunset};
        
        Random random = new Random();
        for (int i = 0; i < 24; i++) {
            int e = random.nextInt(maxTemp) % (maxTemp - minTemp + 1) + minTemp;
            listTemp.add(e);
            int e1 = random.nextInt(maxWindy) % (maxWindy - minWindy + 1) + minWindy;
            listWindy.add(e1);
            
        }
    
        int e2 = random.nextInt(21);
        listWeatherRes.add(weather2[e2]);
         for (int i = 0; i < 23; i++) {
             int e3 = random.nextInt(26);
             listWeatherRes.add(weather[e3]);
        }
        
        
        today24HourView.setMaxMin(maxTemp,minTemp,maxWindy,minWindy);
        today24HourView.setWeatherData(listTemp, listWindy, listWeatherRes);
        Log.e(TAG, listWeatherRes.toString());
    }
}
