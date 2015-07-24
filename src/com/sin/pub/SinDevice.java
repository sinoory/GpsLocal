package com.sin.pub;

import java.net.URLEncoder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class SinDevice{
	
    private static final String deviceId=null;
	public static String getMobSimpId(Context ctx){
		final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}
	public static String getDeviceIdNum(Context ctx){
        if(deviceId!=null){
            return deviceId;
        }
		 final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

		    final String tmDevice, tmSerial, androidId;
		    tmDevice = "" + tm.getDeviceId();
		    //tmSerial = "" + tm.getSimSerialNumber();//depend on sim
		    //depend on google account
		    //androidId = "" + android.provider.Settings.Secure.getString(mCtx.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		    //UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		    String deviceId =tmDevice;//+"-"+tmSerial+"-"+androidId ;//deviceUuid.toString();
		    String phoneInfo = "{\"Product\": \"" + android.os.Build.PRODUCT+"\"";
	        phoneInfo += ", \"VERSION\": \"" + android.os.Build.VERSION.RELEASE+"\"";
	        phoneInfo += ", \"TIME\": \"" + android.os.Build.TIME+"\"";
	        phoneInfo += ", \"ID\": \"" + android.os.Build.ID+"\"";
	        phoneInfo += ", \"MANUFACTURER\": \"" + android.os.Build.MANUFACTURER+"\"";
	        phoneInfo += ", \"TELID\": \"" + deviceId+"\"";
	        phoneInfo += ", \"MOBTYPE\": \"" + "android"+"\"";
	        phoneInfo += ", \"APP\": \"" + "busgps"+"\"";
	        phoneInfo +="}";

            deviceId=phoneInfo;
	        //SCRIPT_MARK.d("DBG","phoneInfo="+phoneInfo);
		    return phoneInfo;
	}

    public static void initfirstday(final Context ctx){
        final SharedPreferences shp = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String FIRST_DAY="f"+"d";
        String encfirstday=shp.getString(FIRST_DAY,"");//first use day
        if(!encfirstday.equals("")){
            return;
        }
        final String serverdomain="121.43.234.157";
        new Thread(){
            public void run(){
                String url="";
				try {
					url = "https://"+serverdomain+":5050/querrylock?mobinfo="+URLEncoder.encode(SinDevice.getDeviceIdNum(ctx),"UTF-8");
                String res=httpsconn.conn(url);
                if(res.indexOf(":")==-1){
                    return;
                }
                String resa[]=res.split(":");
                shp.edit().putString(FIRST_DAY, resa[1])
                    .putString("i"+"d", resa[0])
                    .commit();
				} catch (Exception e1) {
					e1.printStackTrace();
                    return;
				}

            }
        }.start();
    }



}
