package com.sin.gpslocal;

import com.sin.gpslocal.UserList.BusServer_;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class BusServer{
    Context mCtx=null;
    BusServer_ impl=null;
    public BusServer(Context ctx){
        mCtx=ctx;
    }

    @JavascriptInterface
    public String getBusInfo(){
    	return impl.getBusInfo();
    }

    @JavascriptInterface
    public void sendMsg(String msg){
        impl.sendMsg(msg);
    }
}
