package com.sin.pub;


import java.util.ArrayList;
import java.util.HashMap;


import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class IGridMenuActivity extends Activity {
	
	AlertDialog menuDialog;// menu菜单Dialog
	GridView menuGrid;
	View menuView;

	public interface IMenuClickLis{
		public void onMenuClick();
	}
	
	
	public class GridMenuItem{
		
		int mMenuImageId;
		String mMenuText;
		IMenuClickLis mMenuClickLs;
		public GridMenuItem(int MenuImageId,String MenuText,IMenuClickLis MenuClickLs){
			this.mMenuImageId=MenuImageId;
			this.mMenuText=MenuText;
			this.mMenuClickLs=MenuClickLs;
		}
	}
	
	public void setMenu( ArrayList<GridMenuItem> arrGridMenuItem){
		mArrGridMenuItem=arrGridMenuItem;
	}
	public void setResouce(int LayoutGridviewMenu,int IdGridView,int LayoutItemMenu,int IdItemImage,int IdItemItext){
		mLayoutGridviewMenu=LayoutGridviewMenu;
		mIdGridView=IdGridView;  
		mLayoutItemMenu=LayoutItemMenu;
		mIdItemImage=IdItemImage;
		mIdItemItext=IdItemItext;
	}
	
	ArrayList<GridMenuItem> mArrGridMenuItem = new ArrayList<GridMenuItem>();
	HashMap<Integer,GridMenuItem> mMapGridMenuItem= new HashMap<Integer,GridMenuItem>();

	/*
	 * R.layout.gridview_menu
	 */
	int mLayoutGridviewMenu=0;
	
	/*
	 * R.id.gridview
	 */
	int mIdGridView=0;
	
	int mLayoutItemMenu=0;//R.layout.item_menu 
	int mIdItemImage=0;//R.id.item_image
	int mIdItemItext=0;//R.id.item_text
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//setContentView(R.layout.main);
		
		menuView = View.inflate(this,mLayoutGridviewMenu, null);
		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
					dialog.dismiss();
				return false;
			}
		});

		menuGrid = (GridView) menuView.findViewById(mIdGridView);
		menuGrid.setAdapter(getMenuAdapter());
		/** 监听menu选项 **/
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				IMenuClickLis mcl=mMapGridMenuItem.get(arg2).mMenuClickLs;
				if(mcl!=null){
					mcl.onMenuClick();
				}
				menuDialog.dismiss();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项
		return super.onCreateOptionsMenu(menu);
	}
	
	private SimpleAdapter getMenuAdapter() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i <this.mArrGridMenuItem.size(); i++) {
			GridMenuItem gm = mArrGridMenuItem.get(i);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gm.mMenuImageId);
			map.put("itemText", gm.mMenuText);
			data.add(map);
			this.mMapGridMenuItem.put(i, gm);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				mLayoutItemMenu, new String[] { "itemImage", "itemText" },
				new int[] { mIdItemImage, mIdItemItext});
		return simperAdapter;
	}
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
		} else {
			menuDialog.show();
		}
		return false;// 返回为true 则显示系统menu
	}
	
}