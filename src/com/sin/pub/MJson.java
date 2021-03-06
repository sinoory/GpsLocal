
package com.sin.pub;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MJson{
    public static JSONArray insertJSONArrayElem(JSONObject root,String arrayName, JSONArray jarray,int pos,JSONObject element) {
        JSONArray Njarray=new JSONArray();
        try{
            boolean inserted=false;
            for(int i=0;i<jarray.length();i++){  
                if(i!=pos){
                    Njarray.put(jarray.get(i));
                }
                else{
                    Njarray.put(element);
                    Njarray.put(jarray.get(i));  
                    inserted=true;
                }
            }
            if(!inserted){//invalid pos, so add to tail
                Njarray.put(element);
            }
            root.put(arrayName,Njarray);
        }catch (Exception e){e.printStackTrace();}
        return Njarray;
    }

    public static String STATION_SEP="=";
    public static void addStation(Context ctx,String linename,String stationname,
            boolean direct,String location,int index,String area){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(ctx);
        String line=sp.getString(linename,"{'name':'"+linename+"','stations':[],'stationDn':[],'lver':0,'linechanged':false,'area':''}");
        Log.d("DBG","addStation line="+line);
        try{
            JSONObject jsonObject = new JSONObject(line);
            int lineversion=jsonObject.getInt("lver");
            String jsarea=jsonObject.getString("area");
            if(jsarea.equals("") && !area.equals("")){
                jsonObject.put("area",area);
            }
            boolean linechanged=jsonObject.getBoolean("linechanged");
            String stationtype=direct?"stations":"stationDn";
            JSONArray array=jsonObject.getJSONArray(stationtype);
            JSONObject eleStation=new JSONObject();
            eleStation.put("stname",stationname);
            eleStation.put("lo",location.split(":")[0]);
            eleStation.put("la",location.split(":")[1]);
            insertJSONArrayElem(jsonObject,stationtype,array,index,eleStation);
            jsonObject.put("lver", lineversion+1);
            jsonObject.put("linechanged", true);
            Log.d("DBG","addStation newline="+jsonObject.toString());
            sp.edit()
                .putString(linename,jsonObject.toString())
                .putString("lastLine",linename)
                .putString("selectlineChanged","true")
                .commit();
        }catch(Exception e){
        }
    }
    private static void conmit() {
		// TODO Auto-generated method stub
		
	}
	public static void stringToJsonLine(){
        String js="{'name':'line8','stations':['guanLanLu=123.2:323.7','jinkeLu=323.2:343.6']}";
        try{
            JSONObject jsonObject = new JSONObject(js);
            Log.d("DBG","linename="+jsonObject.getString("name"));
            JSONArray array = jsonObject.getJSONArray("stations");
            //array=MJson.insertJSONArrayElem(jsonObject,"stations",array,0,"zhangjian=3232.32:432.3");
            String firstStation=array.getString(1);
            Log.d("DBG","new array="+array.toString());
            Log.d("DBG","new json string="+jsonObject.toString());
        }catch(Exception e){
        }
    }

    public static JSONArray RemoveJSONArray( JSONArray jarray,int pos) {
        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){  
                if(i!=pos)
                    Njarray.put(jarray.get(i));  
            }
        }catch (Exception e){e.printStackTrace();}
        return Njarray;
    }
}
