package com.sin.baidu;


import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.sin.pub.IWebSocket;
import com.sin.pub.IWebSocket.WSMsgListener;


public class GpsApplication extends Application {
	
    private static GpsApplication mInstance = null;
    public boolean m_bKeyRight = true;
    BMapManager mBMapManager = null;

	
	@Override
    public void onCreate() {
	    super.onCreate();
		mInstance = this;
		initEngineManager(this);
        //initConn();
        Log.d("DBG","GpsApplication onCreate finis,this="+this);
        //new Exception("e").printStackTrace();
	}
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(new MyGeneralListener())) {
            Toast.makeText(GpsApplication.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
	}

	public static IWebSocket ws = null;
    public boolean initConn(){
        synchronized(this){
            if(ws!=null){
                Log.d("DBG","App initConn ws inited,ws="+ws+" ,ignore");
                return true;
            }
            try {
                Map<String, String> httpHeaders = new HashMap<String, String>();
                httpHeaders.put("Origin","http://121.43.234.157:5050");
                ws = new IWebSocket(new URI("ws://121.43.234.157:5050/testwebsocket"),httpHeaders, 6000);
                Log.d("DBG","new socket finished,ws="+ws);
                //ws = new IWebSocket(new URI("ws://121.43.234.157:5050/app/feed?user=android"),httpHeaders, 60);
                ws.addListener(new WSMsgListener() {
                    @Override
                    public void onMessage(String message) {
                        Log.d("DBG", "WSMsgListener msg=" + message);
                        for(WSMsgListener ls : mWsLisers){
                            ls.onMessage(message);
                        }
                        //mh.receiveWebMsg(message);
                    }
                });
                return true;
            } catch (Exception e) {
                Log.d("DBG", "XXXXXXXXXXXXXXXIWebSocket exception runXXXXXXXXXXX");
                e.printStackTrace();
                return false;
            }
        }
    }
    private ArrayList<WSMsgListener> mWsLisers=new ArrayList<WSMsgListener>();
    public void registerListener(WSMsgListener ls){
        mWsLisers.add(ls);
    }
    public void removeListener(WSMsgListener ls){
        mWsLisers.remove(ls);
    }
	
	public static GpsApplication getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(GpsApplication.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(GpsApplication.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(GpsApplication.getInstance().getApplicationContext(), 
                        "请在 GpsApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                GpsApplication.getInstance().m_bKeyRight = false;
            }
            else{
            	GpsApplication.getInstance().m_bKeyRight = true;
            	Toast.makeText(GpsApplication.getInstance().getApplicationContext(), 
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
