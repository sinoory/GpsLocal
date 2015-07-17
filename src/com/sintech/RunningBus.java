/*
 * Sintech Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.sintech;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sin.gpslocal.R;
import com.sin.pub.MJson;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Add / Edit bookmark activity.
 */
public class RunningBus extends Activity {
	
	private EditText mFirstEdit;
	private EditText mBottomEdit;
	private EditText mMidEdit;
	private EditText mUrlEditText;
	
	private Button mOkButton;
	private Button mCancelButton;
    SeekBar mseek;	
	private long mRowId = -1;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_bus);
    
        initBusStation();

    }

    ArrayList<View> stations=new ArrayList<View>();
	public void initBusStation(){
        SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
        String linename=sp.getString("lastLine","");
        if(linename.equals("")){
            Log.d("DBG","initBusStation no line");
            return;
        }
        String js=sp.getString(linename,"{'name':'"+linename+"','stationUp':[],'stationDn':[]}");
        try{
            JSONObject jsonObject = new JSONObject(js);
            JSONArray array = jsonObject.getJSONArray("stationUp");
            TextView busname=(TextView)findViewById(R.id.IdBusname);
            busname.setText(jsonObject.getString("name"));
            LinearLayout ll=(LinearLayout)findViewById(R.id.IdStations);
            for(int i=0;i<array.length();i++){  
                String stInfo=array.getString(i);
                TextView station=new TextView(this);
                station.setText(stInfo.split("=")[0]);
                ll.addView(station);
            }
        }catch(Exception e){
        }
    }


}

