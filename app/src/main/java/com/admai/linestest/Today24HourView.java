package com.admai.linestest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 0.屏幕适配
 * 1.折线图水平线向上移动 X
 * 2.数据传入    X
 * 3.随当前时间变化 half  
 * 4.卡顿解决 
 * 5.可能的话配置attrs
 * 
 * 第一个点的res必须要存在..(第一个点必须要有天气).(之后出现天气转折的地方有res)
 * 
 * 应该从7点开始...而不是00:00.:时间点在外部的json中传入, 时间点与温度风力等绑定 
 * 
 */
public class Today24HourView extends View{
    private static int ALL_HEIGHT = 160; //总高度
    
    private static final String TAG = "Today24HourView";
    private static final int ITEM_SIZE = 24;  //24小时
    private static int ITEM_WIDTH = 10; //每个Item的宽度 提示气泡背景与item 宽度一致
    
    private static int MARGIN_LEFT_ITEM = 70; //左边预留宽度
    private static int MARGIN_RIGHT_ITEM = 70; //右边预留宽度

    private static final int windyBoxAlpha = 60;  //未选中的风力的透明度
    private static int windyBoxMaxHeight = 80;
    private static int windyBoxMinHeight = 20;
    private static int windyBoxSubHight = windyBoxMaxHeight - windyBoxMinHeight;
    private static int bottomTextHeight = 30;
    private static final int POP_WIDTH = 88;
    
    private int mHeight, mWidth;
    private int tempBaseTop;  //温度折线的上边Y坐标
    private int tempBaseBottom; //温度折线的下边Y坐标
    private Paint bitmapPaint, windyBoxPaint, linePaint, innerPointPaint,outPointPaint, dashLinePaint;
    private TextPaint textPaint,popTextPaint;
    
    //color
    private int colorLine = Color.rgb(96,171,207);
    private int colorBackground = Color.rgb(214,231,233);
    private int colorTxt = Color.rgb(72,63,65);
    private int colorTxtPop = Color.rgb(218,84,12);
    
    private List<HourItem> listItems;
    private int maxScrollOffset = 0;//滚动条最长滚动距离
    private int scrollOffset = 0; //滚动条偏移量
    private int currentItemIndex = 0; //当前滚动的位置所对应的item下标
    private int currentWeatherRes = -1;

    private int maxTemp = 40;  //最告温度
    private int minTemp = -10; //最底温度
    private int maxWindy = 18; //最大风
    private int minWindy = 1;  //最小风
    private Integer[] TEMP = {    //温度集合
        40, 35, 23, 28, 29,
        36, 15, 20, 25, 22,
        21, -10, 0, 22, 23,
        -5, 24, 24, -10, 25,
        25, 26, 30, 35
    };
    private Integer[] WINDY = {   //风力集合
        1, 2, 18, 3, 3,
        4, 13, 18, 12, 15,
        3, 4, 10, 4, 4,
        2, 13, 9, 18, 3,
        3, 5, 5, 18
    };
    private Integer[] WEATHER_RES ={  //天气图片集合
        R.mipmap.w0, R.mipmap.w1, R.mipmap.w3, -1, -1,
        R.mipmap.w5, R.mipmap.w7, R.mipmap.w9, -1, -1, 
        -1, R.mipmap.w10, R.mipmap.w15, -1, -1, 
        -1, -1, -1, -1, -1,
        R.mipmap.w18, -1, -1, R.mipmap.w19
    };
    
    public void setWeatherRes(ArrayList<Integer> listWeatherRes) {
        WEATHER_RES = (Integer[]) listWeatherRes.toArray();
    }
    
    public void setWindy(ArrayList listWindy) {
       WINDY = (Integer[]) listWindy.toArray(); 
    }
    
    public void setTemp(ArrayList listTemp) {
       TEMP = (Integer[]) listTemp.toArray(); 
    }
     public void setWeatherData(ArrayList<Integer> listTemp,ArrayList<Integer> listWindy,ArrayList<Integer> listWeatherRes) {
         TEMP = listTemp.toArray(new Integer[24]);
         WEATHER_RES = listWeatherRes.toArray(new Integer[24]);
         WINDY = listWindy.toArray(new Integer[24]);
         init();
         invalidate();
         Log.e(TAG, TEMP[1]+"");
     }
    
    public void setMaxMin(int maxTemp,int minTemp,int maxWindy,int minWindy) {
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.maxWindy = maxWindy;        
        this.minWindy = minWindy;
    }
    private  int hour;
    public void setTime(int hour) {
        this.hour = hour;
    }
    
    public Today24HourView(Context context) {
        this(context, null);
    }

    public Today24HourView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Today24HourView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ALL_HEIGHT = DisplayUtil.dip2px(getContext(), 160);
        Log.e(TAG, "ALL_HEIGHT: "+ALL_HEIGHT );
        ITEM_WIDTH =  DisplayUtil.dip2px(getContext(), 50);
        MARGIN_LEFT_ITEM =  DisplayUtil.dip2px(getContext(), 40);
        MARGIN_RIGHT_ITEM =  DisplayUtil.dip2px(getContext(), 40);
        bottomTextHeight =  DisplayUtil.dip2px(getContext(), 18);
        windyBoxMaxHeight =  DisplayUtil.dip2px(getContext(), 34);
        windyBoxMinHeight =  DisplayUtil.dip2px(getContext(), 4);
        windyBoxSubHight =  windyBoxMaxHeight - windyBoxMinHeight;
        Log.e(TAG, "ITEM_WIDTH: "+ITEM_WIDTH );
        init();
    }

    private void init() {
        mWidth = MARGIN_LEFT_ITEM + MARGIN_RIGHT_ITEM + ITEM_SIZE * ITEM_WIDTH;    //宽度
        mHeight = ALL_HEIGHT; //暂时先写死    高度
        
        tempBaseTop = (mHeight - bottomTextHeight)/4;      //折线图高度
//        tempBaseBottom = (mHeight - bottomTextHeight)*2/3;
        //前提是 最大风力和最小风力要设对
        tempBaseBottom = (mHeight              //总高度
                          - bottomTextHeight   //底部时间高度
                          - windyBoxMaxHeight  //最大风力条高度
                          - DisplayUtil.dip2px(getContext(), 26)); //风力提示文字高度 20 + 空隙 4  

        initHourItems();
        initPaint();
    }

    private void initPaint() {
        //内圆画笔
        innerPointPaint = new Paint();
        innerPointPaint.setColor(colorLine);
        innerPointPaint.setAntiAlias(true);
        
        //外圆画笔
        outPointPaint = new Paint();
        outPointPaint.setColor(colorBackground);
        outPointPaint.setAntiAlias(true);
        
        //线画笔
        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(DisplayUtil.sp2px(getContext(), 2));
        
//        PathEffect effect1 = new DashPathEffect(new float[]{85, 15, 85, 15}, 1);
//        linePaint.setPathEffect(effect1);
        
        
        
        
        //虚线画笔
        dashLinePaint = new Paint();
        dashLinePaint.setColor(Color.WHITE);
        //new float[]{第一条实线的长度, 第一条虚线的长度, 第二条5, 5}
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);   //虚线
        dashLinePaint.setPathEffect(effect);
        dashLinePaint.setStrokeWidth(DisplayUtil.sp2px(getContext(), 2));
        dashLinePaint.setAntiAlias(true);
        dashLinePaint.setStyle(Paint.Style.STROKE);
        
        //条形画笔
        windyBoxPaint = new Paint();
        windyBoxPaint.setColor(colorLine);
        windyBoxPaint.setAlpha(windyBoxAlpha);
        windyBoxPaint.setAntiAlias(true);
        
        //文字画笔
        textPaint = new TextPaint();
        textPaint.setTextSize(DisplayUtil.sp2px(getContext(), 12));
        textPaint.setColor(colorTxt);
        textPaint.setAntiAlias(true);
        
        //提示框文字画笔
        popTextPaint = new TextPaint();
        popTextPaint.setTextSize(DisplayUtil.sp2px(getContext(), 14));
        popTextPaint.setColor(colorTxtPop);  //提示框文字
        popTextPaint.setAntiAlias(true);
        
        //图片画笔
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }

    //底部时间
    //简单初始化下，后续改为由外部传入
    private void initHourItems(){
        listItems = new ArrayList<>();
        //修改现在
        DateFormat df = new SimpleDateFormat("HH:mm");
        String timeC = df.format(new Date());
        hour = Integer.valueOf(timeC.substring(0, 2));
        int second = Integer.valueOf(timeC.substring(3, 5));
        if (second > 40) {
            hour = hour+1;
        }
        for(int i=0; i<ITEM_SIZE; i++){
            String time;
            if (hour == i) {
                time = "现在"; 
            } else {
                if (i < 10) {
                    time = "0" + i + ":00";
                } else {
                    time = i + ":00";
                }
            }
            int left = MARGIN_LEFT_ITEM  +  i * ITEM_WIDTH;
            int right = left + ITEM_WIDTH - DisplayUtil.sp2px(getContext(), 1);  //box缝隙在这
            int top = (int) (mHeight 
                            -bottomTextHeight 
                            + (maxWindy - WINDY[i])*1.0/(maxWindy - minWindy)*windyBoxSubHight 
                            - windyBoxMaxHeight);
            int bottom = mHeight - bottomTextHeight;
            Rect rect = new Rect(left, top, right, bottom);
            Point point = calculateTempPoint(left, right, TEMP[i]);

            HourItem hourItem = new HourItem();
            hourItem.windyBoxRect = rect;
            hourItem.time = time;
            hourItem.windy = WINDY[i];
            hourItem.temperature = TEMP[i];
            hourItem.tempPoint = point;
            hourItem.res = WEATHER_RES[i];
            listItems.add(hourItem);
        }
    }

    private Point calculateTempPoint(int left, int right, int temp){
        double minHeight = tempBaseTop;
        double maxHeight = tempBaseBottom;
        double tempY = maxHeight - (temp - minTemp)* 1.0/(maxTemp - minTemp) * (maxHeight - minHeight);
        Point point = new Point((left + right)/2, (int)tempY);
        return point;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: " );
        for(int i=0; i<listItems.size(); i++){
            Rect rect = listItems.get(i).windyBoxRect;
            Point point = listItems.get(i).tempPoint;
            //画风力的box和提示文字    只是风力
            onDrawBox(canvas, rect, i);
           
            //画表示天气图片
            if(listItems.get(i).res != -1 && i != currentItemIndex){
                Drawable drawable = ContextCompat.getDrawable(getContext(), listItems.get(i).res);
                drawable.setBounds(
                    point.x - DisplayUtil.dip2px(getContext(), 10),
                    point.y - DisplayUtil.dip2px(getContext(), 25),
                    point.x + DisplayUtil.dip2px(getContext(), 10),
                    point.y - DisplayUtil.dip2px(getContext(), 5)
                                  );
                drawable.draw(canvas);
            }
            //只是折线
            onDrawLine(canvas, i);
//            //画温度的点  点和小提示窗口
//            onDrawTemp(canvas, i);
    
            //只是时间
            onDrawText(canvas, i);
        }
        for(int i=0; i<listItems.size(); i++) {
            //画温度的点  点和小提示窗口
            onDrawTemp(canvas, i);
        }        
            //底部水平的线
        linePaint.setColor(colorLine);
        linePaint.setAlpha(windyBoxAlpha); //设置透明度
        linePaint.setStrokeWidth(DisplayUtil.dip2px(getContext(), 1));
        canvas.drawLine(0, mHeight - bottomTextHeight, mWidth, mHeight - bottomTextHeight, linePaint);
        
//        //中间温度的上下虚线
//        Path path1 = new Path();
//        path1.moveTo(MARGIN_LEFT_ITEM, tempBaseTop);
//        path1.quadTo(mWidth - MARGIN_RIGHT_ITEM, tempBaseTop, mWidth - MARGIN_RIGHT_ITEM, tempBaseTop);
//        canvas.drawPath(path1, dashLinePaint);
//        Path path2 = new Path();
//        path2.moveTo(MARGIN_LEFT_ITEM, tempBaseBottom);
//        path2.quadTo(mWidth - MARGIN_RIGHT_ITEM, tempBaseBottom, mWidth - MARGIN_RIGHT_ITEM, tempBaseBottom);
//        canvas.drawPath(path2, dashLinePaint);
    }

    private void onDrawTemp(Canvas canvas, int i) {
        HourItem item = listItems.get(i);
        Point point = item.tempPoint;
       

        if(currentItemIndex == i) {
            //选中的点
            outPointPaint.setColor(Color.WHITE);
            innerPointPaint.setColor(colorTxtPop);
            canvas.drawCircle(point.x, point.y, DisplayUtil.dip2px(getContext(), 5), outPointPaint);
            canvas.drawCircle(point.x, point.y, DisplayUtil.dip2px(getContext(), 3), innerPointPaint);
            
            //计算提示文字的运动轨迹
            int Y = getTempBarY();
            //画气泡背景图片
//          Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.hour_24_float);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.background);
            
            drawable.setBounds(
                getScrollBarX() - DisplayUtil.dip2px(getContext(), 6), 
                Y - DisplayUtil.dip2px(getContext(), 29),
                getScrollBarX() + ITEM_WIDTH + DisplayUtil.dip2px(getContext(), 6) ,
                Y - DisplayUtil.dip2px(getContext(), 6)
                              );
            
            drawable.draw(canvas);
            //画气泡天气图片
            int res = findCurrentRes(i);
            if(res != -1) {
                Drawable drawTemp = ContextCompat.getDrawable(getContext(), res);
//                drawTemp.setBounds(
//                    getScrollBarX()+POP_WIDTH/2 + (POP_WIDTH/2 - DisplayUtil.dip2px(getContext(), 16))/2 , //变小右移
//                    Y - DisplayUtil.dip2px(getContext(), 26),
//                    getScrollBarX()+POP_WIDTH - (POP_WIDTH/2 - DisplayUtil.dip2px(getContext(), 24))/2 ,  //变小左移
//                    Y - DisplayUtil.dip2px(getContext(), 7)
//                                  ); 
                
                drawTemp.setBounds(
                    getScrollBarX()+DisplayUtil.dip2px(getContext(), 6)+ITEM_WIDTH/2 , //变小右移
                    Y - DisplayUtil.dip2px(getContext(), 27),  //上
                    getScrollBarX()+ITEM_WIDTH ,  //变小左移
                    Y - DisplayUtil.dip2px(getContext(), 8)    //下
                                  );
                drawTemp.draw(canvas);
            }
            //画气泡温度提示                                                        
            int offset = ITEM_WIDTH/2;
            if(res == -1) {
                offset = ITEM_WIDTH;
            } 
            
            Rect targetRect = new Rect(getScrollBarX() + DisplayUtil.dip2px(getContext(),6), //left 2* (6)
                                       Y - DisplayUtil.dip2px(getContext(), 30), //top
                                       getScrollBarX() + offset - DisplayUtil.dip2px(getContext(), 6)+ITEM_WIDTH/16, //right
                                       Y - DisplayUtil.dip2px(getContext(), 5)); //bottom
            
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            popTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(item.temperature + "°/", targetRect.centerX(), baseline, popTextPaint);
        }else{
            outPointPaint.setColor(Color.parseColor("#D6E7E9"));
            innerPointPaint.setColor(colorLine);
            canvas.drawCircle(point.x, point.y, DisplayUtil.dip2px(getContext(), 5), outPointPaint);
            canvas.drawCircle(point.x, point.y, DisplayUtil.dip2px(getContext(), 3), innerPointPaint); 
        }
    }

    private int findCurrentRes(int i) {
        if(listItems.get(i).res != -1)
            return listItems.get(i).res;
        for(int k=i; k>=0; k--){
            if(listItems.get(k).res != -1)
                return listItems.get(k).res;
        }
        return -1;
    }

    //画底部风力的BOX
    private void onDrawBox(Canvas canvas, Rect rect, int i) {
        // 新建一个矩形
        RectF boxRect = new RectF(rect);
        HourItem item = listItems.get(i);
        if(i == currentItemIndex) {
            windyBoxPaint.setAlpha(255);
            canvas.drawRoundRect(boxRect, 4,4 , windyBoxPaint);// 4 圆角
            //画出box上面的风力提示文字
            Rect targetRect = new Rect(getScrollBarX(), 
                                       rect.top - DisplayUtil.dip2px(getContext(), 20), 
                                       getScrollBarX()+ITEM_WIDTH - DisplayUtil.dip2px(getContext(), 6), 
                                       rect.top - DisplayUtil.dip2px(getContext(), 0));
            
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            textPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(item.windy + "级", targetRect.centerX(), baseline, textPaint);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.mipmap.sun_loading);
            drawable.setBounds(
                getScrollBarX()+DisplayUtil.dip2px(getContext(), 4), //left
                rect.top - DisplayUtil.dip2px(getContext(), 18), //top
                getScrollBarX() + DisplayUtil.dip2px(getContext(), 19), //right
                rect.top - DisplayUtil.dip2px(getContext(), 3)
                              );
    
            drawable.draw(canvas);
            
        } else {
            windyBoxPaint.setAlpha(windyBoxAlpha);
            canvas.drawRoundRect(boxRect, 4, 4, windyBoxPaint);
        }
    }

    //温度的折线,为了折线比较平滑，做了贝塞尔曲线
    private void onDrawLine(Canvas canvas, int i) {
        linePaint.setColor(colorLine);
        linePaint.setAlpha(255);
        linePaint.setStrokeWidth(DisplayUtil.dip2px(getContext(), 2));
        Point point = listItems.get(i).tempPoint;
        if(i != 0){
            Point pointPre = listItems.get(i-1).tempPoint;
            Path path = new Path();
            path.moveTo(pointPre.x, pointPre.y);
            if(i % 2 == 0) {
                //贝塞尔
                //二阶
                path.cubicTo((pointPre.x + point.x) / 2,
                             (pointPre.y + point.y) / 2 - 5,
                             (pointPre.x + point.x) / 2,
                             (pointPre.y + point.y) / 2 + 5,
                             point.x,
                             point.y
                            );
                //一阶
//                path.quadTo((pointPre.x + point.x) / 2,(pointPre.y + point.y) / 2+4,point.x,point.y);
            } else {
                //一阶
//                path.quadTo((pointPre.x + point.x) / 2,(pointPre.y + point.y) / 2-4,point.x,point.y);
                //二阶
                path.cubicTo((pointPre.x + point.x) / 2,
                             (pointPre.y + point.y) / 2 + 5,
                             (pointPre.x + point.x) / 2,
                             (pointPre.y + point.y) / 2 - 5,
                             point.x,
                             point.y
                            );
            }
            canvas.drawPath(path, linePaint);
        }
    }

    //绘制底部时间
    private void onDrawText(Canvas canvas, int i) {
        //此处的计算是为了文字能够居中
        Rect rect = listItems.get(i).windyBoxRect;
        Rect targetRect = new Rect(rect.left, rect.bottom, rect.right, rect.bottom + bottomTextHeight);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        textPaint.setTextAlign(Paint.Align.CENTER);

        String text = listItems.get(i).time;
        canvas.drawText(text, targetRect.centerX(), baseline, textPaint);
    }
    
    //画最左侧的文字
    public void drawLeftTempText(Canvas canvas, int offset){
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(maxTemp + "°", DisplayUtil.dip2px(getContext(), 6) + offset, tempBaseTop, textPaint);
        canvas.drawText(minTemp + "°", DisplayUtil.dip2px(getContext(), 6) + offset, tempBaseBottom, textPaint);
    }

    //设置scrollerView的滚动条的位置，通过位置计算当前的时段
    public void setScrollOffset(int offset, int maxScrollOffset){
        this.maxScrollOffset = maxScrollOffset;
        scrollOffset = offset;
        int index = calculateItemIndex(offset);
        currentItemIndex = index;
        invalidate();   //会重新绘制整个. 四参的用法?
    }

    //通过滚动条偏移量计算当前选择的时刻
    private int calculateItemIndex(int offset){
//        Log.d(TAG, "maxScrollOffset = " + maxScrollOffset + "  scrollOffset = " + scrollOffset);
        int x = getScrollBarX();
        int sum = MARGIN_LEFT_ITEM  - ITEM_WIDTH/2;
        for(int i=0; i<ITEM_SIZE; i++){
            sum += ITEM_WIDTH;
            if(x < sum)
                return i;
        }
        return ITEM_SIZE - 1;
    }

    private int getScrollBarX(){
        int x = (ITEM_SIZE - 1) * ITEM_WIDTH * scrollOffset / maxScrollOffset;
        x = x + MARGIN_LEFT_ITEM;
        return x;
    }

    //计算温度提示文字的运动轨迹
    private int getTempBarY(){
        int x = getScrollBarX();
        int sum = MARGIN_LEFT_ITEM ;
        Point startPoint = null, endPoint;
        int i;
        for(i=0; i<ITEM_SIZE; i++){
            sum += ITEM_WIDTH;
            if(x < sum) {
                startPoint = listItems.get(i).tempPoint;
                break;
            }
        }
        if(i+1 >= ITEM_SIZE || startPoint == null)
            return listItems.get(ITEM_SIZE-1).tempPoint.y;
        endPoint = listItems.get(i+1).tempPoint;

        Rect rect = listItems.get(i).windyBoxRect;
        int y = (int)(startPoint.y + (x - rect.left)*1.0/ITEM_WIDTH * (endPoint.y - startPoint.y));
        return y;
    }

}
