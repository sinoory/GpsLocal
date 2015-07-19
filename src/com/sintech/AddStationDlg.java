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
import android.widget.SeekBar;

/**
 * Add / Edit bookmark activity.
 */
public class AddStationDlg extends Activity {
	
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
        
        Window w = getWindow();
		w.requestFeature(Window.FEATURE_LEFT_ICON);
        
        setContentView(R.layout.add_station_dlg);
        
        //w.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,	android.R.drawable.ic_input_add);
        
        mFirstEdit = (EditText) findViewById(R.id.IdFirstEdit);
        mBottomEdit = (EditText) findViewById(R.id.IdBottomEdit);
        mMidEdit = (EditText) findViewById(R.id.IdMidEdit);
        

  
        mOkButton = (Button) findViewById(R.id.EditBookmarkActivity_BtnOk);
        mCancelButton = (Button) findViewById(R.id.EditBookmarkActivity_BtnCancel);
        
        Intent ii=getIntent();
        Bundle extras = ii.getExtras();
        mOkButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {				
                String line=mFirstEdit.getText().toString();
                MJson.addStation(AddStationDlg.this,mFirstEdit.getText().toString(),
                    mMidEdit.getText().toString(),true,mBottomEdit.getText().toString(),-1);
                SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(AddStationDlg.this);
                String alllines=sp.getString("alllines","");
                Log.d("DBG","addStation alllines="+alllines+",line="+line);
                if(alllines.indexOf(line)==-1){
                    alllines+=","+line;
                    sp.edit().putString("alllines",alllines).commit();;
                }
                    
				finish();
			}
		});
        
        mCancelButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
        
        String prog="0";
    	if (extras != null) {
            mBottomEdit.setText(extras.getString("BOTTOM_EDIT_TXT"));
            mMidEdit.setText(extras.getString("MID_EDIT_TXT"));
            mFirstEdit.setText(extras.getString("FIRST_EDIT_TXT"));
    	}


        setTitle("SetStation");



    }
    int maxProg=100;
 
}

