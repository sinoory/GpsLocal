package com.sin.gpslocal;
import android.view.View;

import com.sin.pub.IGridMenuDialog;
import com.sin.pub.IGridMenuActivity.GridMenuItem;
import com.sin.pub.IGridMenuActivity.IMenuClickLis;


public class MenuDlg extends IGridMenuDialog{
	public void setupGridMenu(){
		setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_nightmode,"调试信息",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub

			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_nightmode,"test",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub

			}}));
		
	}
}