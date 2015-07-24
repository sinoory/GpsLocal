package com.sin.pub;

import android.content.SharedPreferences;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JsLocalHander{
    private SharedPreferences sp=null;

    public JsLocalHander(SharedPreferences s){
        sp=s;
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
};

