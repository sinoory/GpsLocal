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
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.webkit.JavascriptInterface;
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
import com.sin.baidu.GpsApplication;
import com.sin.baidu.LocationOverlayDemo;
import com.sin.pub.IGridMenuDialog;
import com.sin.pub.IWebSocket.WSMsgListener;
import com.sin.pub.JsLocalHander;
import com.sin.pub.file.AndFileUty;
import com.sin.pub.SinDevice;

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

    class BusMenuDlg extends IGridMenuDialog{
        public void setupGridMenu() {
            setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);
            changeItem("start",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"start",new IMenuClickLis(){
                @Override
                public void onMenuClick() {
                    ((UserList)mAct).start();
                }}));
            changeItem("lines",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"lines",new IMenuClickLis(){
                @Override
                public void onMenuClick() {
                    Intent intent = new Intent();
                    intent.setClass(UserList.this, BusList.class);
                    mAct.startActivity(intent);
                }}));
            changeItem("serverlines",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"serverlines",new IMenuClickLis(){
                @Override
                public void onMenuClick() {
                    Intent intent = new Intent();
                    intent.setClass(UserList.this, ServerBusList.class);
                    mAct.startActivity(intent);
                }}));
            changeItem("map",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"map",new IMenuClickLis(){
                @Override
                public void onMenuClick() {
                    Intent intent = new Intent();
                    intent.setClass(UserList.this, LocationOverlayDemo.class);
                    mAct.startActivity(intent);
                }}));

            changeItem("exit",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"exit",new IMenuClickLis(){
                @Override
                public void onMenuClick() {
                    finish();
                    System.exit(0);
                }}));

        }
    }

	BusMenuDlg mMenu;
	class BusServer_{
        public String getBusInfo(){
            String linename=sp.getString("lastLine","");
            if(linename.equals("")){
                Log.d("DBG","initBusStation no line");
                return "";
            }
            String js=sp.getString(linename,"{'name':'"+linename+"','stationUp':[],'stationDn':[]}");
            Log.d("DBG","getBusInfo="+js);
            return js;
        }

        public void sendMsg(String msg){
            Log.d("DBG","sendMsg("+msg+")");
            app.ws.sendmsg(msg);
        }
        public String getDeviceId(){
            return SinDevice.getDeviceIdNum(UserList.this);
        }
	};
    WSMsgListener mwsListener=new WSMsgListener(){
        @Override
        public void onMessage(String message) {
            mWebView.loadUrl("javascript:onWsMessage('"+message+"')");
        }
    };

    public void start(){
        Log.d("DBG","next call js start");
        mWebView.loadUrl("javascript:start()");
    }
	
	BusServer mobj;
    JsLocalHander jlh;
	
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
            sb.append("{");
			//sb.append("时间 : ");
			//sb.append(location.getLocType());
			sb.append("\"la\":");
			sb.append(location.getLatitude());
			sb.append(",\"lo\":");
			sb.append(location.getLongitude());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append(",\"speed\":");
				sb.append(location.getSpeed());
			}
            sb.append("}");
            mWebView.loadUrl("javascript:onGpsPos('"+sb+"')");
			//Log.d("DBG","onReceiveLocation 1"+sb);

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
		option.setScanSpan(1000*10); // 设置发起定位请求的间隔时间为5000ms
		// 设置获取地址信息
		//option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		// 注册监听函数
		mLocationClient.registerLocationListener(myListener);
		// 调用此方法开始定位
		mLocationClient.start();
	}

    SharedPreferences sp=null;//PreferenceManager.getDefaultSharedPreferences(this);
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
        
        sp=PreferenceManager.getDefaultSharedPreferences(this);
        mobj=new BusServer(this);
        mobj.impl=new BusServer_();
        jlh=new JsLocalHander(sp);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
		//SCRIPT_MARK.d("Temp","w="+metric.widthPixels+",h="+metric.heightPixels+",dm.density="+metric.density);
        //mobj.setDeviceInfo((int)(metric.widthPixels/metric.density), (int)(metric.heightPixels/metric.density));
        //mobj.setSysinfo(WordUty.getDeviceIdNum(this));
        mWebView.addJavascriptInterface(mobj, "b"+"u"+"s"+"s");
        mWebView.addJavascriptInterface(jlh, "j"+"l"+"h");
        
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

        

        int fromsdcard=AndFileUty.isExist(this,"/sin/gps/html/","userlist.html");
        if(fromsdcard==0){
        	//SCRIPT_MARK.d(TAG,"Load from sdcard");
        	mWebView.loadUrl("file:///sdcard/sin/gps/html/userlist.html");
        }else{
        	//SCRIPT_MARK.d(TAG,"Load from assert,"+fromsdcard);
        	mWebView.loadUrl("file:///android_asset/html/userlist.html");
        }

        mMenu=new BusMenuDlg();
        mMenu.setupGridMenu();
        mMenu.onCreateInit(this);
        //mMenu.setupObj(mobj);
        
        startGps();

        app = (GpsApplication)this.getApplication();
        app.initConn();
        Log.d("DBG","UserList app="+app);
        
    }
    GpsApplication app = null;

    @Override
    protected void onDestroy() {
    	//退出时销毁定位
        Log.d("DBG","UserList onDestroy");
        if (mLocationClient != null)
            mLocationClient.stop();

        super.onDestroy();
    }

    void openMenuAccordingUrl(){
            mMenu.openMenu();
    }
    @Override
    protected void onResume() {
        app.registerListener(mwsListener);
    	super.onResume();
    }

    @Override
    protected void onPause() {
        app.removeListener(mwsListener);
    	super.onPause();
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
