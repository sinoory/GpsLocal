package com.sintech;

import android.util.Log;

import com.sin.pub.IGridMenuDialog;
import com.sin.gpslocal.R;
import com.sin.gpslocal.UserList;
public class UserMenuDlg extends IGridMenuDialog{
    public void setupGridMenu() {
		setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);
		changeItem("start",new GridMenuItem( R.drawable.button_play,R.drawable.button_pause,"start",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
                ((UserList)mAct).start();
			}}));

        //changeRead2Read();
        //addnew();
        //adddel();
    }
/*
    void changeRead2Read(){
    }

    void changeRead2Stop(){
		changeItem("read",new GridMenuItem( R.drawable.button_pause,R.drawable.button_pause,"stopread",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
                changeRead2Read();
			}}));
    }
    void addnew(){
		changeItem("new",new GridMenuItem( R.drawable.button_add,R.drawable.button_pause,"new",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
			}}));
    }
    void adddel(){
		changeItem("del",new GridMenuItem( R.drawable.button_del,R.drawable.button_pause,"del",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
			}}));
    }
*/
    


}
