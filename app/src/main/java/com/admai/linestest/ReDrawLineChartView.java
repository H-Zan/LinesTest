package com.admai.linestest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 折线图 
 *
 * @created 2013年8月14日15:11:52 
 *
 */
@SuppressLint({ "DrawAllocation", "FloatMath" })
public class ReDrawLineChartView extends View{
	
	public float s = 500;//默认中间线位置  
	private boolean isTouched = false;//中间线点击位置变化控制  
	
	private String[] SPPED_SCALES1 = new String[6];//{ "0", "10", "20","30", "40","120"};  
	private String[] dates1 = new String[] { "1", "2", "3", "4", "5", "6", "7","8", "9", "10", "11", "12", "13", "14","15", "16",
	                                         "17", "18", "19", "20", "21" ,"22", "23", "24", "25", "26", "27", "28","29", "30", "31"};
	//这3组只是[替代数据]-非真实数据,仅仅是为了在绘图过程中方便[查看效果]。  
	//  public float[] firstPoints = {33, 14, 21, 11, 40, 15, 39 ,8, 45, 35,33, 14, 21, 11, 40,   
	//           15, 39 ,8, 45, 35,33, 14, 21, 11, 40, 15, 39 ,8, 45, 35,22};  
	public float[] firstPoints = {0,20,30,40,50,60,70,60, 70,70,70,40,30,20,10, 10,20,30,40,50,60,70,80, 70,60,50,40,30,20,10, 10,20,30,40,50,60,70,80, 70,60,50,40,30,20,10};
	public float[] secondPoints = {14, 31, 11, 21, 15, 40, 8, 30, 13, 45,14, 31, 11, 21, 15,
	                               40, 8, 30, 13, 45,14, 31, 11, 21, 15, 40, 8, 30, 13, 45,20};
	public float[] thirdPoints = {40, 47, 11, 38, 21, 14, 37, 29, 31, 32, 15, 49,20, 47, 11,
	                              38, 21, 14, 37, 29, 31, 32, 15, 49,20, 47, 11, 38, 21, 14, 37};
	
	private Paint linePaint = new Paint();//背景线。  
	private Paint textPaint = new Paint();//文字  
	private Paint yChartPaint = new Paint();//Y轴标题。  
	private Paint circleRedBlueGreenPaint = new Paint();//环形画笔。  
	private Paint circelPaint = new Paint();//拐点圆圈。  
	private Paint innerCircelPaint = new Paint();//内圆。  
	private Paint chartLinePaint = new Paint();//第一条线  
	private Paint secondChartPaint = new Paint();//第二条线。  
	private Paint thirdChartPaint = new Paint();//第三条线。  
	private Paint centerLinePaint = new Paint();//中间竖线。  
	
	private String totalChartNum = "1132";
	private String tongbiRatio = "0.19%";
	private String huanbiRatio = "3.92%";
	
	private int leftDirectionId = 1;//上面箭头方向。    
	private int rightDirectionId = 1;//下面箭头方向  
	
	
	public ReDrawLineChartView(Context context) {
		// TODO Auto-generated constructor stub  
		super(context);
	}
	
	public ReDrawLineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public ReDrawLineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	//setters  
	public void setTotalChartNum(String totalChartNum) {
		this.totalChartNum = totalChartNum;
	}
	
	public void setTongbiRatio(String tongbiRatio) {
		this.tongbiRatio = tongbiRatio;
	}
	
	public void setHuanbiRatio(String huanbiRatio) {
		this.huanbiRatio = huanbiRatio;
	}
	
	//控制上下箭头的指向。  
	public void setLeftArrowDirection(int upDirectionId) {
		this.leftDirectionId = upDirectionId;
	}
	
	public void setRightArrowDirection(int downDirectionId) {
		this.rightDirectionId = downDirectionId;
	}
	
	//进行真实数据赋值。  
	public void setData(float[] firstPoints, float[] secondPoints, float[] thirdPoints) {
		this.firstPoints = firstPoints;
		this.secondPoints = secondPoints;
		this.thirdPoints = thirdPoints;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//绘制模式-虚线作为背景线。  
		PathEffect effect = new DashPathEffect(new float[] { 6, 6, 6, 6, 6}, 2);
		//背景虚线路径.  
		Path path = new Path();
		
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth((float)0.7);
		linePaint.setColor(Color.WHITE);
		linePaint.setAntiAlias(true);// 锯齿不显示  
		textPaint.setStyle(Style.FILL);// 设置非填充  
		textPaint.setStrokeWidth(1);// 笔宽5像素  
		textPaint.setColor(Color.WHITE);// 设置为蓝笔  
		textPaint.setAntiAlias(true);// 锯齿不显示  
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setTextSize(15);
		yChartPaint.setStyle(Style.FILL);
		yChartPaint.setStrokeWidth(1);
		yChartPaint.setColor(Color.WHITE);
		yChartPaint.setAntiAlias(true);
		yChartPaint.setTextAlign(Align.CENTER);
		yChartPaint.setTextSize(18);
		circelPaint.setStyle(Style.FILL);
		circelPaint.setStrokeWidth(2);
		circelPaint.setColor(Color.YELLOW);
		circelPaint.setAntiAlias(true);
		innerCircelPaint.setStyle(Style.FILL);
		innerCircelPaint.setStrokeWidth(1);
		innerCircelPaint.setColor(Color.parseColor("#ffffff"));       //白色
		innerCircelPaint.setAntiAlias(true);
		chartLinePaint.setStyle(Style.FILL);
		chartLinePaint.setStrokeWidth(3);
		chartLinePaint.setColor(Color.rgb(255, 210, 0));//(1)黄色  
		chartLinePaint.setAntiAlias(true);
		secondChartPaint.setStyle(Style.FILL);
		secondChartPaint.setStrokeWidth(3);
		secondChartPaint.setColor(Color.rgb(169, 222, 63));//(2)绿色  
		secondChartPaint.setAntiAlias(true);
		thirdChartPaint.setColor(Color.rgb(108, 212, 255));//(3)蓝色  
		thirdChartPaint.setStrokeWidth(3);
		thirdChartPaint.setStyle(Style.FILL);
		thirdChartPaint.setAntiAlias(true);
		centerLinePaint.setColor(Color.parseColor("#7DA62D"));//中间动态线。  
		centerLinePaint.setStrokeWidth(3);
		
		circleRedBlueGreenPaint.setStrokeWidth(6);
		circleRedBlueGreenPaint.setAntiAlias(true);//消除锯齿。  
		circleRedBlueGreenPaint.setStyle(Style.STROKE);
		circleRedBlueGreenPaint.setColor(Color.parseColor("#4692B1"));//  
		
		//TODO 算Y轴的刻度。  
		float tempLine1Max = getMaxNumFromArr(firstPoints);
		float tempLine2Max = getMaxNumFromArr(secondPoints);
		float tempLine3Max = getMaxNumFromArr(thirdPoints);
		
		//刻度的最大值。  
		float maxNum = getMaxNumOfThree(tempLine1Max, tempLine2Max, tempLine3Max);//先算出最大值。  
		float finalMaxNum = getRelativeNum(maxNum);//根据最大值找出临近的最大值--方便Y刻度标注。  
		for(int i = 0; i <= 5; i++) {//循环对Y刻度赋值。便于显示。  
			if(maxNum >= 0 && maxNum < 1) {
				SPPED_SCALES1[i] =  String.valueOf((int)((float)finalMaxNum/5*i)) + "%";//百分数的Y轴刻度。  
			} else {
				SPPED_SCALES1[i] =  String.valueOf(0 + (int)((float)finalMaxNum/5*i));
			}
		}
		
		//基准点。  
		float gridX = 30+10;
		float gridY = getHeight() - 30;
		//XY间隔。  
		float xSpace = (float) ((getWidth()-60)/31-1.5);
		float ySpace = (getHeight()-160)/(SPPED_SCALES1.length-1)+3;
		//画Y轴(带箭头)。  
		canvas.drawLine(gridX, gridY-20-10, gridX, 30+20, linePaint);
		canvas.drawLine(gridX, 30+20, gridX-6, 30+14+20, linePaint);//Y轴箭头。  
		canvas.drawLine(gridX, 30+20, gridX+6, 30+14+20, linePaint);
		//画Y轴名字。  
		canvas.drawText("客流数", gridX, 50-5, yChartPaint);
		//TODO 【最上方】一栏。"1132"  
		canvas.drawText("合计:", gridX+300, 50-5, yChartPaint);
		canvas.drawText(totalChartNum, gridX+300+50+5+3, 50-5, yChartPaint);
		//第一个对比环-yellow-green(1)  
		circleRedBlueGreenPaint.setColor(Color.rgb(169, 222, 63));//绿色(底环)。  
		canvas.drawCircle(gridX+300+180, 30+20-10, 8, circleRedBlueGreenPaint);
		circleRedBlueGreenPaint.setColor(Color.rgb(255, 210, 0));//黄色(上环)。  
		canvas.drawCircle(gridX+300+180-8, 30+20-10, 8, circleRedBlueGreenPaint);
		//red-green-右侧百分比(1)"0.19%"  
		canvas.drawText(tongbiRatio, gridX+300+180-8+50+5, 50-5, yChartPaint);
		yChartPaint.setColor(Color.rgb(169, 222, 63));//将头颜色--底色--绿  
		if(leftDirectionId > 0) {
			canvas.drawText("↑", gridX+300+180-8+85+10, 50-5, yChartPaint);
		} else {
			canvas.drawText("↓", gridX+300+180-8+85+10, 50-5, yChartPaint);
		}
		
		//第二个对比环-red-blue(2)  
		circleRedBlueGreenPaint.setColor(Color.rgb(108, 212, 255));//蓝色(底环)。  
		canvas.drawCircle(gridX+300+180+50+50+50+50-10, 30+20-10, 8, circleRedBlueGreenPaint);
		circleRedBlueGreenPaint.setColor(Color.rgb(255, 210, 0));//黄色(上环)。  
		canvas.drawCircle(gridX+300+180+50+50+50+50-10-8, 30+20-10, 8, circleRedBlueGreenPaint);
		//red-blue-右侧百分比(2)"3.92%"  
		yChartPaint.setColor(Color.parseColor("#ffffffff"));//字体白色。  
		canvas.drawText(huanbiRatio, gridX+300+180+50+50+50+50-10-8+50+5, 50-5, yChartPaint);
		yChartPaint.setColor(Color.rgb(108, 212, 255));//箭头颜色--底色--蓝色。  
		if(rightDirectionId > 0) {
			canvas.drawText("↑", gridX+300+180+50+50+50+50-10-8+85+10, 50-5, yChartPaint);
		} else {
			canvas.drawText("↓", gridX+300+180+50+50+50+50-10-8+85+10, 50-5, yChartPaint);
		}
		
		float y = 0;//Y间隔。  
		//画X轴+背景虚线。  
		y = gridY-20;
		canvas.drawLine(gridX, y-10, getWidth()-55+10, y-10, linePaint);//X轴.  
		canvas.drawLine(getWidth()-55+10, y-10, getWidth()-55-14+10, y-6-10, linePaint);//X轴箭头。  
		canvas.drawLine(getWidth()-55+10, y-10, getWidth()-55-14+10, y+6-10, linePaint);
		for(int n = 0; n < SPPED_SCALES1.length; n++) {
			y = gridY-20 - n * ySpace;
			linePaint.setPathEffect(effect);//设法虚线间隔样式。  
			//画除X轴之外的------背景虚线-------  
			if(n > 0) {
				path.moveTo(gridX, y-10);//背景【虚线起点】。  
				path.lineTo(getWidth()-55+10, y-10);//背景【虚线终点】。  
				canvas.drawPath(path, linePaint);
			}
			//画Y轴刻度。  
			canvas.drawText(SPPED_SCALES1[n], gridX-6-7, y, textPaint);
		}
		//绘制X刻度坐标。  
		float x = 0;
		if(dates1[0] != null) {
			for(int n = 0; n < dates1.length; n++) {
				//取X刻度坐标.  
				x = gridX + (n+1) * xSpace;//在原点(0,0)处不画刻度,向右移动一个跨度。  
				//画X轴具体刻度值。  
				if(dates1[n] != null) {
					canvas.drawLine(x, gridY-30, x, gridY-18, linePaint);//短X刻度。  
					canvas.drawText(dates1[n], x, gridY+5, textPaint);//X具体刻度值。  
				}
			}
		}
		
		//起始点。  
		float lastPointX = 0;
		float lastPointY = 0;
		float currentPointX = 0;
		float currentPointY = 0;
		if(firstPoints != null) {
			//1.绘制第一条折线。  
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)firstPoints[n] / 60 * (this.getHeight() - ySpace)-(float)2.5;  
				//              currentPointY =  (1 - (float)firstPoints[n]/80) * (gridY-40);  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)firstPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				if(n > 0) {
					canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);//第一条线[蓝色]  
				}
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)firstPoints[n] / 60 * (this.getHeight() - ySpace)-(float)2.5;  
				//              currentPointY =  (1 - (float)firstPoints[n]/80) * (gridY-30);  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)firstPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				circelPaint.setColor(Color.rgb(255, 210, 0));//(1)黄色  
				canvas.drawCircle(lastPointX, lastPointY, 6, circelPaint);  //外圆:8:直径or半径
				canvas.drawCircle(lastPointX, lastPointY, 5, innerCircelPaint); //内圆
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
		}
		
		//重置起始点。  
		lastPointX = 0;//gridX+10;  
		lastPointY = 0;//gridY-90;  
		currentPointX = 0;
		currentPointY = 0;
		
		if(secondPoints != null) {
			//2.绘制第二条折线。  
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)secondPoints[n] / 60 * (this.getHeight() - ySpace)-(float)2.5;  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)secondPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				if(n > 0) {
					canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, secondChartPaint);//第二条线[绿色]  
				}
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)secondPoints[n] / 60 * (this.getHeight() - ySpace)-(float)2.5;  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)secondPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				circelPaint.setColor(Color.rgb(169, 222, 63));//(2)绿色  
				canvas.drawCircle(lastPointX, lastPointY, 8, circelPaint);
				canvas.drawCircle(lastPointX, lastPointY, 5, innerCircelPaint);
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
		}
		
		//重置起始点。  
		lastPointX = 0;//gridX+10;  
		lastPointY = 0;//gridY-240;  
		currentPointX = 0;
		currentPointY = 0;
		
		if(thirdPoints != null) {
			//3.绘制第三条折线。  
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)thirdPoints[n] / 60 * (this.getHeight() - ySpace)+(float)4.0;  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)thirdPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				if(n > 0) {
					canvas.drawCircle(lastPointX, lastPointY, 5, thirdChartPaint);
					canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, thirdChartPaint);//第三条线[橙色]  
				}
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
			for(int n = 0; n < dates1.length; n++) {
				//get current point  
				currentPointX =  n * xSpace + xSpace+xSpace+2;
				//              currentPointY =  (float)(getHeight()-ySpace*3/4) - (float)thirdPoints[n] / 60 * (this.getHeight() - ySpace)+(float)4.0;  
				currentPointY =  (float)(getHeight()-40)-15-5 - (float)thirdPoints[n]/((float)1.6*maxNum) * (getHeight()-40);
				//draw line  
				circelPaint.setColor(Color.rgb(108, 212, 255));//(3)蓝色  
				canvas.drawCircle(lastPointX, lastPointY, 8, circelPaint);
				canvas.drawCircle(lastPointX, lastPointY, 5, innerCircelPaint);
				lastPointX = currentPointX;
				lastPointY = currentPointY;
			}
		}
		
		//中间竖直线--  
		canvas.drawLine(s, gridY-40, s, gridY-5*ySpace-40, centerLinePaint);
		canvas.drawLine(s-10, gridY-40, s+10, gridY-40, centerLinePaint);
		canvas.drawLine(s-10, gridY-5*ySpace-40, s+10, gridY-5*ySpace-40, centerLinePaint);
	}
	
	int clickCount = 0;
	long firstClickTime = 0;
	long secondClickTime = 0;
	
	/**
	 * 手势监听--处理双击事件。 
	 */
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch(event.getAction() & MotionEvent.ACTION_MASK) {
//			case MotionEvent.ACTION_DOWN:
//				if(MainLSPKPIActivity.isForbidClick) {//当4个titleBtn被按下,禁止对折线图的一切操作。  
//					clickCount++;
//					if(clickCount == 1) {
//						firstClickTime = System.currentTimeMillis();
//						MainLSPKPIActivity.doubleClick = false;
//					} else if(clickCount == 2) {
//						secondClickTime = System.currentTimeMillis();
//						if(secondClickTime - firstClickTime < 1000) {
//							MainLSPKPIActivity.doubleClick = true;
//							clickCount = 0;
//							firstClickTime = 0;
//							secondClickTime = 0;
//						} else {
//							MainLSPKPIActivity.doubleClick = false;
//							clickCount = 0;
//							firstClickTime = 0;
//							secondClickTime = 0;
//						}
//					}
//					Log.i("action-down", " action-down");
//					if(event.getX() >=(s-30) && event.getX() <= (s+30)) {
//						isTouched = true;
//					}
//				}
//				
//				break;
//			case MotionEvent.ACTION_MOVE:
//				Log.i("action-move", " action-move");
//				if(!MainLSPKPIActivity.doubleClick)
//					if(isTouched) {
//						s = event.getX();//将此时手势x坐标记录下来, 根据此x重绘中间线、  
//						invalidate();
//					}
//				break;
//			case MotionEvent.ACTION_UP:
//				Log.i("action-up", " action-up");
//				if(!MainLSPKPIActivity.doubleClick) {
//					if(isTouched) {
//						s = event.getX();//记录当下位置坐标  
//						isTouched = false;
//						invalidate();
//					}
//				}
//				break;
//		}
//		
//		if(!MainLSPKPIActivity.doubleClick)
//			return true;//没双击时-子控件有焦点。  
//		else
//			return false;//将焦点传递给父控件。  
//	}
//	
	/**
	 * 取数组中的最大元素 
	 * @param numArr 取值的数组 
	 * @return 返回数组中的最大值。 
	 */
	private float getMaxNumFromArr(float[] numArr) {
		float maxNum = numArr[0];
		for(int i = 0; i < numArr.length; i++) {
			if(numArr[i] > maxNum) {
				maxNum = numArr[i];
			}
		}
		return maxNum;
	}
	
	/**
	 * 返回3个值中的最大值。 
	 * @param firstNum  第一个数值 
	 * @param secondNum 第二个数值 
	 * @param thirdNum  第三个数值 
	 * @return  返回传入的3个值中的最大值。 
	 */
	private float getMaxNumOfThree(float firstNum, float secondNum, float thirdNum) {
		float maxNum = 0;
		if(firstNum >= secondNum && firstNum >= thirdNum) {
			maxNum = firstNum;
		}
		if(secondNum >= firstNum && secondNum >= thirdNum) {
			maxNum = secondNum;
		}
		if(thirdNum >= firstNum && thirdNum >= secondNum) {
			maxNum = thirdNum;
		}
		return maxNum;
	}
	
	/**
	 * 根据数组最大值来获取临近最大值--来作为刻度的最大值----方便分配刻度。 
	 * @param num 传入的值 
	 * @return 返回num附近的整数值, 方便刻度分配。 
	 */
	private float getRelativeNum(float num) {
		float desNum = 0;
		if(num >= 0 && num < 1) {
			desNum = 100;
		} else if(num >= 1 && num <= 100) {
			desNum = 100;
		} else if(num > 100 && num < 1000) {
			int num1 = (int)num % 100;//余数-个十位。  
			int num2 = (int)num / 100;//高位-百位。  
			if(num1 > 0 && num1 <= 50) {
				desNum = num2 * 100 + 50;//取临近的整数值作为Y轴刻度。  
			} else if(num1 > 50 && num1 < 100) {
				desNum = num2 * 100 + 100;
			}
		} else if(num >= 1000 && num < 1500) {
			desNum = handleY(num, desNum, 1000);
		} else if(num >= 1500 && num < 2000) {
			desNum = handleY(num, desNum, 1000);
		} else if(num >= 2000 && num < 3000) {
			desNum = handleY(num, desNum, 1000);
		} else if(num >= 3000 && num < 4000) {
			desNum = handleY(num, desNum, 1000);
		} else if(num >= 4000 && num < 5000) {
			desNum = handleY(num, desNum, 1000);
		} else if(num >= 5000 && num < 10000) {
			desNum = handleY(num, desNum, 1000);
		}
		return desNum;
	}
	
	/**
	 * 具体处理Y轴刻度 
	 * @param sourceNum      传入的实际值 
	 * @param desNum         要返回的目标值 
	 * @param highPostionNum 实际值的最高位 
	 * @return 将传入的目标值赋值后返回
	 */
	private float handleY(float sourceNum, float desNum, int highPostionNum) {
		int num1 = (int)sourceNum % highPostionNum;//余数-个十位。  
		int num2 = (int)sourceNum / highPostionNum;//高位-百位。  
		if(num1 > 0 && num1 <= highPostionNum/2) {
			desNum = num2 * highPostionNum + highPostionNum/2;//取临近的整数值作为Y轴刻度。  
		} else if(num1 > highPostionNum/2 && num1 < highPostionNum) {
			desNum = num2 * highPostionNum + highPostionNum;
		}
		return desNum;
	}
	
}  