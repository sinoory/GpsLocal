package com.sin.gpslocal;

import org.json.JSONObject;



import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebView.HitTestResult;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.sintech.UserMenuDlg;

public class UserList extends Activity {
	private static final String TAG = "WordAct";

	Button mButton;
	WebView mWebView;
	JSONObject j;

    public static final int MSG_SCROLL_WORD=1;
    public static final int MSG_GOBACK=2;
    public static final int MSG_START_LISTEN_VOLKEY=3;
    public static final int MSG_STOP_LISTEN_VOLKEY=4;
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
            switch(msg.what){
            case MSG_SCROLL_WORD:
                //SCRIPT_MARK.d(TAG,"MSG_SCROLL_WORD");
				//mWebView.loadUrl("javascript:dbg('MSG_SCROLL_WORD-from_handler')");
                break;
            case MSG_GOBACK:
                mWebView.goBack();
                break;
            case MSG_START_LISTEN_VOLKEY:
                //SCRIPT_MARK.d(TAG,"MSG_START_LISTEN_VOLKEY");
                break;
            case MSG_STOP_LISTEN_VOLKEY:
                //SCRIPT_MARK.d(TAG,"MSG_STOP_LISTEN_VOLKEY");
                break;
            }

        }
    };

    void postGoBack(){ 
        Message message = new Message(); 
        message.what = MSG_GOBACK; 
        mHandler.sendMessage(message); 
    }


	private Handler mUiHandler = new Handler();
	UserMenuDlg mMenu;
	class MyObject{
	};
	
	MyObject mobj;
	
	int cnt = 0;
    String mCurUrl="";
    ImageView mImgMenu=null;
    
 // 定位客户端类
 	public LocationClient mLocationClient = null;
 	// 定位监听器类
 	public BDLocationListener myListener = new MyLocationListener();
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("时间 : ");
			sb.append(location.getLocType());
			sb.append("\n纬度 : ");
			sb.append(location.getLatitude());
			sb.append("\n经度 : ");
			sb.append(location.getLongitude());
			sb.append("\n半径 : ");
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\n速度 : ");
				sb.append(location.getSpeed());
			}
			Log.d("DBG","onReceiveLocation "+sb);
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			
		}
	}
	public void startGps(){
		mLocationClient = new LocationClient(getApplicationContext());
		// 设置定位参数
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开GPRS
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000); // 设置发起定位请求的间隔时间为5000ms
		// 设置获取地址信息
		//option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		// 注册监听函数
		mLocationClient.registerLocationListener(myListener);
		// 调用此方法开始定位
		mLocationClient.start();
	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);//disable lock screen
        setContentView(R.layout.userlist);
        //SCRIPT_MARK.d(TAG,"onCreate");
        mImgMenu=(ImageView)findViewById(R.id.BubbleRightView);
        mImgMenu.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                openMenuAccordingUrl();
            }
        });

        mWebView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = mWebView.getSettings();        
        webSettings.setJavaScriptEnabled(true);
        try{
        	mWebView.getSettings().setDatabaseEnabled(true);  
        webSettings.setDomStorageEnabled(true);
        String databasePath = this.getApplicationContext().getDir("database", this.MODE_PRIVATE).getPath();
        mWebView.getSettings().setDatabasePath(databasePath);
        
        mWebView.getSettings().setAppCacheEnabled(true);
        String appCaceDir =this.getApplicationContext().getDir("cache", this.MODE_PRIVATE).getPath();
        mWebView.getSettings().setAppCachePath(appCaceDir);
        }catch(Exception e){
        	
        }
        
        
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    	WindowManager winManager=(WindowManager)getSystemService(this.WINDOW_SERVICE);
        
        //mobj=new MyObject(this,mHandler,mUiHandler);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
		//SCRIPT_MARK.d("Temp","w="+metric.widthPixels+",h="+metric.heightPixels+",dm.density="+metric.density);
        //mobj.setDeviceInfo((int)(metric.widthPixels/metric.density), (int)(metric.heightPixels/metric.density));
        //mobj.setSysinfo(WordUty.getDeviceIdNum(this));
        mWebView.addJavascriptInterface(mobj, "my"+"Ob"+"je"+"ct");
        
        PackageManager pm = getPackageManager();//context为当前Activity上下文 
        PackageInfo pi;
		try {
			pi = pm.getPackageInfo(getPackageName(), 0);
			String version = pi.versionName;
			//mobj.setversion(version);
			//SCRIPT_MARK.d("DBG","version="+version);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mImgMenu.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            /** 捕获点击事件 */ 
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //in some mobile , click url do not load by the app itself.
                //maybe need override shouldOverrideUrlLoading of WebViewClient
                //SCRIPT_MARK.d("dbg","shouldOverrideUrlLoading url="+url);
            	if(url.indexOf("file://")>=0 || url.indexOf("121.43.234.157")>=0){
                    mWebView.loadUrl(url);
            	}else{
            		Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
            	}
                return true;              
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                postGoBack();
            	Toast.makeText(getApplicationContext(), "无法连接服务器，请稍后再试。。。",
            		     Toast.LENGTH_SHORT).show();
            }
            @Override
        	public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            	//SCRIPT_MARK.d("DBG","onReceivedSslError");
            	handler.proceed();
            }
            @Override
        	public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, final String host, final String realm) {
        		String username = null;
        		//SCRIPT_MARK.d("DBG","onReceivedHttpAuthRequest");
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                //SCRIPT_MARK.d("dbg","onPageFinished url="+url);
                mCurUrl=url;
                super.onPageFinished(view, url);
            }

        });

        

        int fromsdcard=1 ; //AndFileUty.isExist(this,"/words/html/","wordsMaintype.html");
        if(fromsdcard==0){
        	//SCRIPT_MARK.d(TAG,"Load from sdcard");
        	mWebView.loadUrl("file:///sdcard/words/html/wordsMaintype.html");
        }else{
        	//SCRIPT_MARK.d(TAG,"Load from assert,"+fromsdcard);
        	mWebView.loadUrl("file:///android_asset/html/userlist.html");
        }

        mMenu=new UserMenuDlg();
        mMenu.setupGridMenu();
        mMenu.onCreateInit(this);
        //mMenu.setupObj(mobj);
        
        startGps();
        
    }
    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        Log.d("DBG","UserList onDestroy");
        if (mLocationClient != null)
            mLocationClient.stop();

    }

    void openMenuAccordingUrl(){
            mMenu.openMenu();
    }
    @Override
    protected void onResume() {
    	super.onResume();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
    	//SCRIPT_MARK.d(TAG,"onKeyDown keyCode="+keyCode);
        if ((keyCode == KeyEvent.KEYCODE_BACK) ){
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
        }else if(keyCode==KeyEvent.KEYCODE_VOLUME_UP){
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	 if(keyCode == KeyEvent.KEYCODE_MENU) {
    		 return true;
    	 }
    	return super.onKeyUp(keyCode, event);
    }	


    
}