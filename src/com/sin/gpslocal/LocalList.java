package com.sin.gpslocal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

public class LocalList extends ListActivity {


	// private List<String> data = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.listitem,
				new String[]{"title","info","img"},
				new int[]{R.id.title,R.id.info,R.id.img});
		setListAdapter(adapter);
	}

	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = null;
		for(int i=0;i<50;i++){
		map = new HashMap<String, Object>();
		map.put("title", "G"+i);
		map.put("info", "google "+i);
		map.put("img", R.drawable.listimg);
		list.add(map);
		}
		map = new HashMap<String, Object>();
		map.put("title", "G2");
		map.put("info", "google 2");
		map.put("img", R.drawable.listimg);
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("title", "G3");
		map.put("info", "google 3");
		map.put("img", R.drawable.listimg);
		list.add(map);
		
		return list;
	}
}