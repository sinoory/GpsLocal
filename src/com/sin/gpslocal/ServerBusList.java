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
import com.sin.pub.IWebSocket.WSMsgListener;
import com.sin.pub.JsLocalHander;
import com.sin.pub.file.AndFileUty;
import com.sintech.UserMenuDlg;

public class ServerBusList extends Activity {
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


	UserMenuDlg mMenu;
	
	JsLocalHander mobj;
	
	int cnt = 0;
    String mCurUrl="";
    ImageView mImgMenu=null;
    

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

        mobj=new JsLocalHander(sp);
        mobj.setApp((GpsApplication)this.getApplication());
        mWebView.addJavascriptInterface(mobj, "j"+"h");
       
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mImgMenu.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            /** 捕获点击事件 */ 
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
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
            	handler.proceed();
            }
            @Override
        	public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, final String host, final String realm) {
        		String username = null;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                mCurUrl=url;
                super.onPageFinished(view, url);
            }

        });

        int fromsdcard=AndFileUty.isExist(this,"/sin/gps/html/","serverbuslist.html");
        if(fromsdcard==0){
        	mWebView.loadUrl("file:///sdcard/sin/gps/html/serverbuslist.html");
        }else{
        	mWebView.loadUrl("file:///android_asset/html/serverbuslist.html");
        }

        mMenu=new UserMenuDlg();
        mMenu.setupGridMenu();
        mMenu.onCreateInit(this);
        
    }

    WSMsgListener mwsListener=new WSMsgListener(){
        @Override
        public void onMessage(String message) {
            mWebView.loadUrl("javascript:onWsMessage('"+message+"')");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void openMenuAccordingUrl(){
            mMenu.openMenu();
    }
    @Override
    protected void onResume() {
        ((GpsApplication)this.getApplication()).registerListener(mwsListener);
    	super.onResume();
    }

    @Override
    protected void onPause() {
        ((GpsApplication)this.getApplication()).removeListener(mwsListener);
    	super.onPause();
    }

    
}
