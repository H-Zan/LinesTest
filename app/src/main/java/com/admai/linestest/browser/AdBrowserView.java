package com.admai.linestest.browser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URLDecoder;

/**
 *
 *LandingPage 界面
 * 
 */
public class AdBrowserView extends Activity implements DownloadListener {
	//相关信息定义
	private static final int MSG_TOOLS_OK = 0;
	//UI尺寸定义
	private static final int BUTTON_SIZE = 48;
	private static final int WAITING_SIZE = 64;
	private static final int PROGRESS_SIZE = 5;
	
	public WebView mWebview;
	/**
	 * loading GIF
	 */                   
	//    private GifImageView mWaitingImg;
	
	/**
	 * contents宿主
	 */
	protected RelativeLayout mCtrlBarLayout;
	/**
	 * 是否打开web页
	 */
	public static boolean isopenweb = false;
	/**
	 * 透明
	 */
	private boolean mIsTras = true;
	//广告相关信息，aid，bid，cid，contid，index，message
	private String info = null;
	
	/**
	 * 主宿主
	 */
	//    LinearLayout mainLayout;
	RelativeLayout mainLayout;
	/**
	 * button宿主
	 */
	LinearLayout btnRight;
	LinearLayout btnLeft;
	/**
	 * 手柄宿主
	 */
	LinearLayout slidingHandleLayout;
	/**
	 * 背景宿主
	 */
	RelativeLayout sldingDrawer; // buttons and---
	/**
	 * 标题宿主
	 */
	RelativeLayout titleDrawer;
	TextView mTextView;
	TextView textView;
	
	
	//    ImageButton buttonBack;
	//    ImageButton buttonGo;
	//    ImageButton buttonClose;
	
	Button buttonBack;
	//    Button buttonGo;
	Button buttonClose;
	
	Drawable drawable_admai_bg;
	Bitmap bitmap_admai_back;
	Bitmap bitmap_admai_finish;
	Bitmap bitmap_admai_go;
	
	int buttonHeight;
	int buttonWidth;
	int progressHeight;
	//    int onTouchDownColor = Color.DKGRAY;
	//    int onTouchDownColor = Color.rgb(174,233,239);
	int onTouchDownColor = Color.rgb(10, 66, 143);
	
	final String file_back_name = "com.admai.assets/admai_back.png";
	final String file_bg_name = "com.admai.assets/admai_bg.png";
	final String file_finish_name = "com.admai.assets/admai_finish.png";
	final String file_go_name = "com.admai.assets/admai_go.png";
	
	// 全屏播放视频代码
	private FrameLayout mBrowserVideoLayout;
	private FrameLayout mCustomViewContainer;
	private FrameLayout mContentView;
	private View mCustomView;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	
	//    // 控制器
	//    private static AdBannerLaunchListener mAdBannerLaunchListener;
	//    private static AdFsLaunchListener mAdFsLaunchListener;
	//    private static AdSplashLaunchListener mAdSplashLaunchListener;
	
	// 上传文件需要
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;
	private String mCameraFilePath;
	
	private ProgressBar mProgressBar;
	
	
	private MyHandler mHandler = new MyHandler(this);
	private int textColor = Color.rgb(174, 233, 239);
	private int progressColor = Color.rgb(174, 233, 239);
	private int mTopColor = Color.rgb(55, 130, 233);
	private int mTopColorTint = Color.rgb(52, 127, 229);
	private int statusBarHeight;
	private int sldingDrawerHeight;
	
	private static class MyHandler extends Handler {
		WeakReference<AdBrowserView> mActivity;
		
		MyHandler(AdBrowserView activity) {
			mActivity = new WeakReference<AdBrowserView>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			AdBrowserView theActivity = mActivity.get();
			switch (msg.what) {
				case MSG_TOOLS_OK:
					//                    theActivity.initTools();
					//                    theActivity.initProgressBar();
					break;
				default:
					break;
			}
		}
	}
	
	private void setTranslucentStatus(boolean on) {
		Window win = getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		if (on) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
		}
		win.setAttributes(winParams);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//硬件加速
		setHardWare();
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			setTranslucentStatus(true);
			MaiSystemBar tintManager = new MaiSystemBar(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setStatusBarTintColor(mTopColorTint);//通知栏所需颜色 
			statusBarHeight = tintManager.getConfig().getStatusBarHeight();
			Log.e("statusBarHeight", "onCreate: " + statusBarHeight);
		} 
		setButtonHeight();
		//        LogUtil.LOG_D(this, "AdBrowserView onCreate");
		String browserurl = initData();
		//通知相关广告容器，landingPage页面开始展示
		//        if (mAdBannerLaunchListener != null)
		//            mAdBannerLaunchListener.bannerLandingPagePause();
		//        if (mAdFsLaunchListener != null)
		//            mAdFsLaunchListener.fsLandingPageParse();
		//        if (mAdSplashLaunchListener != null)
		//            mAdSplashLaunchListener.splashLandingPagePause();
		
		//设置主视图
		if (mainLayout == null) {
			//            mainLayout = new LinearLayout(this);
			mainLayout = new RelativeLayout(this);
			mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			//            mainLayout.setOrientation(LinearLayout.VERTICAL);
		}
		
		
		//        new LoadThread().start();
		//初始化WebView及控制
		setUpControls();
		
		//        LogUtil.LOG_E(this, "browserurl:" + browserurl);
		//加载landingPage
		mWebview.loadUrl(browserurl);
		//        LogUtil.LOG_D(this, "AdBrowserView onResume");
		
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
	}
	
	private void initProgressBar() {
		
		if (mProgressBar == null) {
			mProgressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
			
			if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mProgressBar.setProgressTintList(ColorStateList.valueOf(progressColor));
				
			} else {
				//                mProgressBar.getProgressDrawable().setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
			}
			
			
			//            mProgressBar.getIndeterminateDrawable().setColorFilter(Color.BLACK,PorterDuff.Mode.SRC_IN);
			//            mProgressBar.getProgressDrawable().setColorFilter(progressColor, PorterDuff.Mode.SRC_IN);
			
			
			//            mProgressBar.getProgressDrawable().setColorFilter();
			//            }
			//            mProgressBar.setBackgroundColor(Color.LTGRAY);
			
			
			if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				mProgressBar.setScaleY(2f);
			}
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, AdUtils
				                                                                                                .px2dip(this, progressHeight));
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mProgressBar.setLayoutParams(params);
			sldingDrawer.addView(mProgressBar);
		}
	}
	
	private void initTitle() {
		
		if (titleDrawer == null) {
			if (mTextView == null) {
				mTextView = new TextView(this);
			}
			mTextView.setTextColor(textColor);
			mTextView.setTextSize(18);
			DisplayMetrics dm = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			final int ScreenWidth = dm.widthPixels;
			//                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			RelativeLayout.LayoutParams paramsT = new RelativeLayout.LayoutParams(ScreenWidth - 2 * buttonWidth, LayoutParams.WRAP_CONTENT);
			Log.e("initTitle: ", buttonWidth + "");
			paramsT.addRule(RelativeLayout.CENTER_IN_PARENT);
			paramsT.setMargins(2 * buttonWidth, 0, 0, 0);
			mTextView.setLayoutParams(paramsT);
			mTextView.setGravity(Gravity.CENTER);
			if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mTextView.setPadding(0, buttonHeight / 2, 0, 0);
			} else {
				mTextView.setPadding(0, 2, 0, 0);
			}
			
			//                mTextView.setPadding(30,0,0,10);
			mTextView.setSingleLine();
			mTextView.setEllipsize(TextUtils.TruncateAt.END);
			//                mTextView.setTypeface(Typeface.SERIF,Typeface.BOLD);
			//                mTextView.setBackgroundColor(Color.TRANSPARENT);
			mTextView.setBackgroundColor(Color.TRANSPARENT);
			
			
			if (textView == null) {
				textView = new TextView(this);
			}
			RelativeLayout.LayoutParams paramsLin = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 5);
			paramsLin.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			textView.setBackgroundColor(Color.TRANSPARENT);
			textView.setLayoutParams(paramsLin);
			textView.setVisibility(View.GONE);
			sldingDrawer.addView(textView);
			
			
			titleDrawer = new RelativeLayout(this);
			titleDrawer.setBackgroundColor(Color.TRANSPARENT);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			titleDrawer.setLayoutParams(params);
			titleDrawer.addView(mTextView);
			
			sldingDrawer.addView(titleDrawer);
		}
	}
	
	/**
	 * 根据屏幕密度设置导航条宽高
	 */
	private void setButtonHeight() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		// 屏幕密度（0.75 / 1.0 / 1.5 / 2.0）
		float density = metric.density;
		//        sldingDrawerHeight = (int) ((statusBarHeight + buttonHeight )* density);
		buttonWidth = buttonHeight = (int) (BUTTON_SIZE * density);
		progressHeight = (int) (PROGRESS_SIZE * density);
		sldingDrawerHeight = statusBarHeight + buttonHeight;
	}
	
	//    public final static void setAdBannerLaunchListener(AdBannerLaunchListener adLaunchListener) {
	//        mAdBannerLaunchListener = adLaunchListener;
	//    }
	
	//    public final static void setAdFsLaunchListener(AdFsLaunchListener adLaunchListener) {
	//        mAdFsLaunchListener = adLaunchListener;
	//    }
	
	//    public final static void setAdSplashLaunchListener(AdSplashLaunchListener adSplashLaunchListener) {
	//        mAdSplashLaunchListener = adSplashLaunchListener;
	//    }
	
	/**
	 * 设置开启硬件加速
	 */
	private void setHardWare() {
		if (VERSION.SDK_INT >= 11) {
			getWindow().setFlags(16777216, 16777216);
		}
	}
	
	/**
	 * @author zj
	 *         加载控制栏背景及相关按钮位图线程
	 */
	class LoadThread extends Thread {
		@Override
		public void run() {
			//
			//            if (AdManager.getImageSource() != null && AdManager.getImageSource().containsKey(file_bg_name)) {
			//                int sourceId = AdManager.getImageSource().get(file_bg_name);
			//                if (AdUtils.isExists(getResources(), sourceId)) {
			//                    drawable_admai_bg = getResources().getDrawable(sourceId);
			//                }
			//            }
			//
			if (drawable_admai_bg == null) {
				InputStream is = AdUtils.loadAssetsInputStream(AdBrowserView.this, file_bg_name);
				drawable_admai_bg = Drawable.createFromStream(is, "bg");
				AdUtils.closeStream(is);
			}
			bitmap_admai_back = AdUtils.loadAssetsBitmap(AdBrowserView.this, file_back_name);
			bitmap_admai_finish = AdUtils.loadAssetsBitmap(AdBrowserView.this, file_finish_name);
			bitmap_admai_go = AdUtils.loadAssetsBitmap(AdBrowserView.this, file_go_name);
			if (mHandler != null) {
				mHandler.sendEmptyMessage(MSG_TOOLS_OK);
			}
		}
	}
	
	/**
	 * 初始化数据
	 *
	 * @return
	 */
	private String initData() {
		String browserurl = "";
		Bundle b = this.getIntent().getExtras();
		if (b != null) {
			isopenweb = b.getBoolean("isopenweb");
			mIsTras = b.getBoolean("istransparent");
			browserurl = b.getString("browserurl");
			if (b.getString("info") != null) {
				//设置track消息
				info = b.getString("info");
			}
			try {
				browserurl = URLDecoder.decode(browserurl, "utf-8");
			} catch (UnsupportedEncodingException e) {
				
					e.printStackTrace();
			}
			//            if (isopenweb)
			//                AdBaseController.browserView = this;
		}
		return browserurl;
	}
	
	private void setUpControls() {
		initTools();
		initProgressBar();
		setUpCtrlBarUi();
		setUpWebView();
		
		//        setUpWaitingUi();
	}
	
	private void setUpWebView() {
		if (mWebview == null) {
			mWebview = new WebView(this);
			intWebViewSettings();
			initVideoUi();
			mCtrlBarLayout.addView(mBrowserVideoLayout);
			
			mWebview.setWebViewClient(new LocalClient());
			mWebview.setWebChromeClient(new LocalWebChomeClient());
			mWebview.setDownloadListener(this);
			mWebview.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getPointerCount() > 1) {
						//多点触摸,开放缩放选项
						mWebview.getSettings().setSupportZoom(true);
						mWebview.getSettings().setBuiltInZoomControls(true);
						return false;
					} else {
						mWebview.getSettings().setSupportZoom(false);
						mWebview.getSettings().setBuiltInZoomControls(false);
						return false;
					}
				}
			});
		}
	}
	
	private void initVideoUi() {
		// **********************添加支持webview播放视频代码*****************
		mBrowserVideoLayout = new FrameLayout(this);
		mBrowserVideoLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		//        mCustomViewContainer = new FrameLayout(this);
		//        mCustomViewContainer.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		//        mCustomViewContainer.setVisibility(View.GONE);
		//        mBrowserVideoLayout.addView(mCustomViewContainer);
		
		LinearLayout mContentViewLayout = new LinearLayout(this);
		mContentViewLayout.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mContentView = new FrameLayout(this);
		mContentView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mContentViewLayout.addView(mContentView);
		mContentView.addView(mWebview);
		mBrowserVideoLayout.addView(mContentViewLayout);
	}
	
	private void intWebViewSettings() {
		if (mIsTras) {
			if (VERSION.SDK_INT >= 11) {
				try {
					Method MethodsetScrollbarFadingEnabled2 = mWebview.getClass()
					                                                  .getMethod("setLayerType", int.class, Paint.class);
					MethodsetScrollbarFadingEnabled2.invoke(mWebview, View.class.getField("LAYER_TYPE_SOFTWARE")
					                                                            .getInt(View.class), null);
				} catch (Exception e) {
				}
			}
			mWebview.setBackgroundColor(Color.TRANSPARENT);
		}
		WebSettings webSettings = mWebview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		//        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setSupportZoom(true);
		if (VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			webSettings.setDisplayZoomControls(false);
		}
		//设置缓存模式
		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		//设置滚动条样式:在内容显示内部显示
		mWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
	}
	
	//    private void setUpWaitingUi() {
	//        // 添加LoadingImage
	//        int width, height;
	//        width = AdUtils.dip2px(this, WAITING_SIZE);
	//        height = AdUtils.dip2px(this, WAITING_SIZE);
	//        if (mWaitingImg == null) {
	//            mWaitingImg = new GifImageView(getApplicationContext());
	//            mCtrlBarLayout.addView(mWaitingImg);
	//            mWaitingImg.setScaleType(ScaleType.FIT_CENTER);
	//            mWaitingImg.bringToFront();
	//        }
	//        RelativeLayout.LayoutParams gifViewLayout = new RelativeLayout.LayoutParams(width, height);
	//        gifViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT);
	//        mWaitingImg.setLayoutParams(gifViewLayout);
	//            String defaultGifPath = "com.admai.assets/admai_loading.gif";
	//            mWaitingImg.setGif(AdUtils.loadAssetsInputStream(getApplicationContext(), defaultGifPath));
	//    }
	
	private void setUpCtrlBarUi() {     //init webview==
		if (mCtrlBarLayout == null) {
			int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
			int statusBarHeight = getResources().getDimensionPixelSize(resourceId);//顶栏的高度
			mCtrlBarLayout = new RelativeLayout(getApplicationContext());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, getResources()
				                                                                                                .getDisplayMetrics().heightPixels - statusBarHeight - buttonHeight);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			mCtrlBarLayout.setLayoutParams(params);
			//            mCtrlBarLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getResources().getDisplayMetrics().heightPixels - SstatusBarHeight  - 2*buttonHeight));
			
			mainLayout.addView(mCtrlBarLayout);
			this.setContentView(mainLayout);
		}
	}
	
	
	private void initTools() {
		// 抽屉content
		btnRight = new LinearLayout(this);
		RelativeLayout.LayoutParams paramsRight = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, buttonHeight);
		paramsRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		paramsRight.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnRight.setLayoutParams(paramsRight);
		btnRight.setBackgroundColor(Color.TRANSPARENT);
		btnLeft = new LinearLayout(this);
		RelativeLayout.LayoutParams paramsLeft = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, buttonHeight);
		paramsLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		paramsLeft.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		btnLeft.setLayoutParams(paramsLeft);
		LinearLayout.LayoutParams lp_button = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
		
		//        buttonBack = new ImageButton(this);
		//        buttonGo = new ImageButton(this);
		//        buttonClose = new ImageButton(this);
		//        setButton(buttonBack, bitmap_admai_back, lp_button);
		//        setButton(buttonGo, bitmap_admai_go, lp_button);
		//        setButton(buttonClose, bitmap_admai_finish, lp_button);
		
		buttonBack = new Button(this);
		buttonClose = new Button(this);
		setButton(buttonBack, "<", lp_button);
		setButton(buttonClose, "X", lp_button);
		btnRight.addView(buttonClose);
		btnLeft.addView(buttonBack);
		
		buttonBack.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						buttonBack.setBackgroundColor(onTouchDownColor);
						break;
					case MotionEvent.ACTION_UP:
						buttonBack.setBackgroundColor(Color.TRANSPARENT);
						if (mWebview != null && mWebview.canGoBack()) {
							mWebview.goBack();
						}
						break;
				}
				return true;
			}
		});
		//        buttonGo.setOnTouchListener(new OnTouchListener() {
		//            @Override
		//            public boolean onTouch(View v, MotionEvent event) {
		//                switch (event.getAction()) {
		//                    case MotionEvent.ACTION_DOWN:
		//                        buttonGo.setBackgroundColor(onTouchDownColor);
		//                        break;
		//                    case MotionEvent.ACTION_UP:
		//                        buttonGo.setBackgroundColor(Color.TRANSPARENT);
		//                        if (mWebview != null && mWebview.canGoForward())
		//                            mWebview.goForward();
		//                        break;
		//                }
		//                return true;
		//            }
		//        });
		
		buttonClose.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						buttonClose.setBackgroundColor(onTouchDownColor);
						break;
					case MotionEvent.ACTION_UP:
						buttonClose.setBackgroundColor(Color.TRANSPARENT);
						closeAdBrowserView();
						break;
				}
				return true;
			}
		});
		
		// 工具条手柄
		slidingHandleLayout = new LinearLayout(this);
		RelativeLayout.LayoutParams lp_handle = new RelativeLayout.LayoutParams(buttonWidth, buttonHeight);
		lp_handle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		slidingHandleLayout.setLayoutParams(lp_handle);
		slidingHandleLayout.setGravity(Gravity.CENTER);
		if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			slidingHandleLayout.setBackground(drawable_admai_bg);
		}
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int ScreenWidth = dm.widthPixels;
		sldingDrawer = new RelativeLayout(this);
		final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, sldingDrawerHeight);
		lp.gravity = Gravity.TOP;   //
		sldingDrawer.setLayoutParams(lp);
		sldingDrawer.addView(btnRight);
		sldingDrawer.addView(btnLeft);
		slidingHandleLayout.setBackgroundColor(Color.TRANSPARENT); //最右边
		//        sldingDrawer.setBackgroundDrawable(drawable_admai_bg);
		
		if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			//            sldingDrawer.setBackground(drawable_admai_bg);
		}
		
		
		sldingDrawer.setBackgroundColor(mTopColor);    //导航条背景
		//        sldingDrawer.addView(slidingHandleLayout);
		initTitle();
		mainLayout.addView(sldingDrawer);
		
	}
	
	
	//imageButton
	//    private void setButton(ImageButton btn, Bitmap bm, LayoutParams lp) {
	//        btn.setLayoutParams(lp);
	//        btn.setImageBitmap(bm);
	//        btn.setScaleType(ScaleType.CENTER_INSIDE);
	//        btn.setBackgroundColor(Color.TRANSPARENT);
	//    }
	
	//Normal
	private void setButton(Button btn, String bm, LayoutParams lp) {
		btn.setPadding(0, 0, 0, 0);
		btn.setLayoutParams(lp);
		btn.setText(bm);
		btn.setTextSize(16);
		btn.setTextColor(Color.BLACK);
		btn.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override //should force vertical
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setUpControls();
		//        removeWaiting();
		mainLayout.removeView(sldingDrawer);
		initTools();
	}
	
	/**
	 * 移除loading UI
	 */
	//    private void removeWaiting() {
	//        if (mWaitingImg != null) {
	//            mWaitingImg.setVisibility(View.GONE);
	//            mWaitingImg.stopGif();
	//            mWaitingImg = null;
	//        }
	//    }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//        LogUtil.LOG_D(this, "adBrowseview onDestroy start");
		
		//释放位图资源
		disposeBitmap(bitmap_admai_back);
		disposeBitmap(bitmap_admai_finish);
		disposeBitmap(bitmap_admai_go);
		bitmap_admai_finish = bitmap_admai_back = bitmap_admai_go = null;
		
		//        removeWaiting();
		//        LogUtil.LOG_D(this, "adBrowseview onDestroy end");
	}
	
	private void disposeBitmap(Bitmap bm) {
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebview != null && mWebview.canGoBack()) {
				mWebview.goBack();
				return true;
			} else {
				closeAdBrowserView();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onDownloadStart(String url, String userAgent,
	                            String contentDisposition, String mimetype, long contentLength) {
		try {
			//            LogUtil.LOG_D(this, "onDownloadStart");
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (mimetype.equalsIgnoreCase("video/mp4")) {
				intent.setDataAndType(uri, mimetype);
			}
			startActivity(intent);
		} catch (Exception e) {
			//            LogUtil.LOG_E(this, e.getMessage());
		}
	}
	
	
	private void closeAdBrowserView() {
		//        LogUtil.LOG_D(this, "closeAdBrowserView");
		clearWebView();
		AdBrowserView.this.finish();
	}
	
	private void clearWebView() {
		if (mWebview != null) {
			mWebview.stopLoading();
			mWebview.clearCache(false);
			mCtrlBarLayout.removeView(mWebview);
			mWebview.clearHistory();
			mWebview = null;
		}
		//        removeWaiting();
		
		if (mCustomView != null) {
			mCustomView.setVisibility(View.GONE);
			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
		}
		// System.gc();
		
		//        try {
		//
		//            if (mAdBannerLaunchListener != null)
		//                mAdBannerLaunchListener.bannerLandingPageResume();
		//            if (mAdFsLaunchListener != null)
		//                mAdFsLaunchListener.fsLandingPageResume();
		//            if (mAdSplashLaunchListener != null)
		//                mAdSplashLaunchListener.splashLandingPageResume();
		//
		//            mAdBannerLaunchListener = null;
		//            mAdFsLaunchListener = null;
		//            mAdSplashLaunchListener = null;
		//            if (isopenweb)
		//                AdBaseController.browserView = null;
		//        } catch (Exception e) {
		//            e.printStackTrace();
		//        }
	}
	
	class LocalClient extends WebViewClient {
		
//		@Override
//		public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//			super.onReceivedError(view, request, error);
//			mTextView.setText("Noooooooooooooooooooooo");
//			Toast.makeText(getApplicationContext(), "Noooooo", Toast.LENGTH_SHORT).show();
//		}
		//// TODO: 16/12/1 加载失败   
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			mTextView.setText("Noooooooooooooooooooooo");
			Toast.makeText(getApplicationContext(), "Noooooo", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				Uri uri = Uri.parse(url);
				//                removeWaiting();
				if (url.toLowerCase().startsWith("https://")
				    || url.toLowerCase().startsWith("http://")) {
					WebViewUtils.goToPage(view, url);
				} else {
					if (uri.getScheme().equalsIgnoreCase("mailto")) {
						startActivity(WebViewUtils.createMailIntent(url));
					} else if (uri.getScheme().equalsIgnoreCase("sms")) {
						try {
							startActivity(WebViewUtils.createSmsIntent(url));
						} catch (Exception e) {
							startActivity(WebViewUtils.createNewActivtyIntent(uri));
						}
					} else if (uri.getScheme().equalsIgnoreCase("tel")) {
						startActivity(WebViewUtils.createTelIntent(uri));
					} else {
						startActivity(WebViewUtils.createNewActivtyIntent(uri));
					}
				}
			} catch (Exception e) {
			}
			return true;
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.e("onPageFinished: ", "onPageFinished");
			//            removeWaiting();
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
	}
	
	class LocalWebChomeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			WebViewUtils.JSAlert(AdBrowserView.this, message, result);
			return true;
		}
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				newProgress = 0;
				if (mProgressBar.getVisibility() == View.VISIBLE) {
					mProgressBar.setVisibility(View.GONE);
				}
				if (textView.getVisibility() == View.GONE) {
					textView.setVisibility(View.VISIBLE);
				}
			} else {
				if (mProgressBar.getVisibility() == View.GONE) {
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				if (textView.getVisibility() == View.VISIBLE) {
					textView.setVisibility(View.GONE);
				}
			}
			mProgressBar.setProgress(newProgress);
			super.onProgressChanged(view, newProgress);
			Log.e("onProgressChanged2", newProgress + "");
			
		}
		
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
			super.onGeolocationPermissionsShowPrompt(origin, callback);
			callback.invoke(origin, true, false);
		}
		
		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			//            mWebview.setVisibility(View.GONE);
			//
			//            // if a view already exists then immediately terminate the new one
			//            if (mCustomView != null) {
			//                callback.onCustomViewHidden();
			//                return;
			//            }
			//
			//            mCustomViewContainer.addView(view);
			//            mCustomView = view;
			//            mCustomViewCallback = callback;
			//            mCustomViewContainer.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onHideCustomView() {
			//            if (mCustomView == null)
			//                return;
			//            mCustomView.setVisibility(View.GONE);
			//
			//            // Remove the custom view from its container.
			//            mCustomViewContainer.removeView(mCustomView);
			//            mCustomView = null;
			//            mCustomViewContainer.setVisibility(View.GONE);
			//            mCustomViewCallback.onCustomViewHidden();
			//
			//            mWebview.setVisibility(View.VISIBLE);
		}
		
		@Override
		public Bitmap getDefaultVideoPoster() {
			return super.getDefaultVideoPoster();
		}
		
		@Override
		public View getVideoLoadingProgressView() {
			return super.getVideoLoadingProgressView();
		}
		
		//// TODO: 16/12/1   加载失败
		@Override
		public void onReceivedTitle(WebView view, String title) {
			if (title.contains("网页无法打开")) {
				AdBrowserView.this.setTitle("哎呀,加载失败");
				mTextView.setText("哎呀,加载失败");
			}else {
				AdBrowserView.this.setTitle(title);
				mTextView.setText(title);
			}
		}
		
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			mUploadMessage = uploadMsg;
			openFileChooser(uploadMsg, acceptType);
		}
		
		// 3.0 + 调用这个方法
		public void openFileChooser(ValueCallback<Uri> uploadMsg,
		                            String acceptType) {
			mUploadMessage = uploadMsg;
			startActivityForResult(createDefaultOpenableIntent(),
			                       AdBrowserView.FILECHOOSER_RESULTCODE);
		}
		
		// Android < 3.0 调用这个方法
		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			mUploadMessage = uploadMsg;
			openFileChooser(uploadMsg, "");
		}
	}
	
	private Intent createDefaultOpenableIntent() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.setType("*/*");
		
		Intent chooser = createChooserIntent(createCameraIntent(),
		                                     createCamcorderIntent(), createSoundRecorderIntent());
		chooser.putExtra(Intent.EXTRA_INTENT, i);
		return chooser;
	}
	
	private Intent createChooserIntent(Intent... intents) {
		Intent chooser = new Intent(Intent.ACTION_CHOOSER);
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
		chooser.putExtra(Intent.EXTRA_TITLE, "File Chooser");
		return chooser;
	}
	
	private Intent createCameraIntent() {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File externalDataDir = Environment
			                       .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		File cameraDataDir = new File(externalDataDir.getAbsolutePath()
		                              + File.separator + "browser-photos");
		cameraDataDir.mkdirs();
		mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
		                  + System.currentTimeMillis() + ".jpg";
		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
		                      Uri.fromFile(new File(mCameraFilePath)));
		return cameraIntent;
	}
	
	private Intent createCamcorderIntent() {
		return new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	}
	
	private Intent createSoundRecorderIntent() {
		return new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (result == null && data == null && resultCode == Activity.RESULT_OK) {
				File cameraFile = new File(mCameraFilePath);
				if (cameraFile.exists()) {
					result = Uri.fromFile(cameraFile);
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
				}
			}
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}
	
}
