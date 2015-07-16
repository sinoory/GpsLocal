
package com.sin.pub;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class MJson{
    public static JSONArray insertJSONArrayElem(JSONObject root,String arrayName, JSONArray jarray,int pos,String element) {
        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){  
                if(i!=pos)
                    Njarray.put(jarray.get(i));  
                else{
                    Njarray.put(element);
                    Njarray.put(jarray.get(i));  
                }
            }
            root.put(arrayName,Njarray);
        }catch (Exception e){e.printStackTrace();}
        return Njarray;
    }

    public static void stringToJsonLine(){
        String js="{'name':'line8','stationUp':['guanLanLu=123.2:323.7','jinkeLu=323.2:343.6']}";
        try{
            JSONObject jsonObject = new JSONObject(js);
            Log.d("DBG","linename="+jsonObject.getString("name"));
            JSONArray array = jsonObject.getJSONArray("stationUp");
            array=MJson.insertJSONArrayElem(jsonObject,"stationUp",array,1,"zhangjian=3232.32:432.3");
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
