package com.sin.pub;

import com.sin.baidu.GpsApplication;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsLocalHander{
    private SharedPreferences sp=null;
    private GpsApplication app=null;

    public JsLocalHander(SharedPreferences s){
        sp=s;
    }

    public void setApp(GpsApplication a){
        app=a;
    }

    @JavascriptInterface
    public String getShp(String key){
        String val=sp.getString(key,"");
        Log.d("DBG","getShp("+key+")="+val);
        return val;
    }
    @JavascriptInterface
    public void setShp(String key,String val){
        sp.edit().putString(key,val).commit();
    }
    @JavascriptInterface
    public void rmShp(String key){
        sp.edit().remove(key).commit();
    }

    @JavascriptInterface
    public void sendMsg(String msg){
        app.ws.sendmsg(msg);
    }
};

