package com.sin.pub;


import java.util.ArrayList;
import java.util.HashMap;


//import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * Usage:
 * 1. extends your own MyMenudialog from IGridMenuDialog
 * 2. new MyMenudialog in some activity when activity.OnCreate,
 * and then setup you own grid menu by override setupGridMenu(), for example
    public void setupGridMenu() {
		setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);
        // will use the R.layout.gridview_menu and R.layout.item_menu to set menu. They are example layout.Change them at your needs.
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_nightmode,"调试信息",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
			}}));
		//....add other menu and events
    }
	3.take MyMenudialog as a member of your activity,and add the bellow code to show dialog:
    onCreate(){
        ....
        mMenu=new MyMenudialog();
        mMenu.setupGridMenu();
        mMenu.onCreateInit(this);
        //u can change it by use changeItem and setItemEnable later
    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	 if(keyCode == KeyEvent.KEYCODE_MENU) {
    		 mMenu.openMenu();
    		 //new Exception("").printStackTrace();
    		 return true;
    	 }
    	return super.onKeyUp(keyCode, event);
    }	
 */
public class IGridMenuDialog  {
	
	AlertDialog menuDialog;// menu菜单Dialog
	GridView menuGrid;
	View menuView;

	
	public interface IMenuClickLis{
		public void onMenuClick();
	}
	
	
	public class GridMenuItem{
		
		int mMenuImageId;
		public String mMenuText;
		IMenuClickLis mMenuClickLs;
        boolean isDisabled=false;//use can set disable
        int mMenuDisableImageId;
        int mMenuEnableImageId;
		public GridMenuItem(int MenuImageId,int disImgId,String MenuText,IMenuClickLis MenuClickLs){
			this.mMenuImageId=MenuImageId;
			this.mMenuEnableImageId=MenuImageId;
			this.mMenuText=MenuText;
			this.mMenuClickLs=MenuClickLs;
            this.mMenuDisableImageId=disImgId;
		}
	}
	
    public void setupGridMenu() {
		//setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);

    }

	public void setResouce(int LayoutGridviewMenu,int IdGridView,int LayoutItemMenu,int IdItemImage,int IdItemItext){
		mLayoutGridviewMenu=LayoutGridviewMenu;
		mIdGridView=IdGridView;  
		mLayoutItemMenu=LayoutItemMenu;
		mIdItemImage=IdItemImage;
		mIdItemItext=IdItemItext;
	}
	
	public ArrayList<GridMenuItem> mArrGridMenuItem = new ArrayList<GridMenuItem>();
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
	
	public Activity mAct=null;
	public void onCreateInit(Activity act) {
		// TODO Auto-generated method stub
		mAct=act;
		
		//setContentView(R.layout.main);
		
		menuView = View.inflate(mAct,mLayoutGridviewMenu, null);
		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(act).create();
		Window window = menuDialog.getWindow();
		
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) ;// 显示对话框时，后面的Activity不变暗，可选操作。
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();  
        lp.alpha = 0.95f ;// 这是指定对话框的透明度，[0,1] float类型，所有要加f  
        //lp.x = -100 5  
        //lp.y = -100 ;  
        window.setAttributes(lp);  		
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(0x7f070002); 
        
		menuDialog.setView(menuView);
		
		
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				//usually the activity which contain the menudialog need to override the onKeyUp 
				//and call menudialog.openMenu() to show menudialog. 
				//then menudialog will dissmiss itself when menukey up event happend.
				//Lesson:without accurately judge the  KeyEvent.ACTION_UP==event.getAction(), 
				//when menu pressed, the dialog will dissmiss first , then show again by activity.onKeyUp
				//seems the dialog failed to swallow the keyUp event and passed to activity again
				if (keyCode == KeyEvent.KEYCODE_MENU && KeyEvent.ACTION_UP==event.getAction()){// 监听按键
					dialog.dismiss();
					return true;
				}
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
                if(!mMapGridMenuItem.get(arg2).isDisabled){
                    if(mcl!=null){
                        mcl.onMenuClick();
                    }
                    menuDialog.dismiss();
                }
			}
		});
	}
	ArrayList<String> mAMenuNames = new ArrayList<String>();
    HashMap<String,GridMenuItem> mMNameMenus=new HashMap<String,GridMenuItem>();
    //called by use when first create menu dialog
    public void addItem(String itemName,GridMenuItem item){
        mAMenuNames.add(itemName);
        mMNameMenus.put(itemName,item);
    }
    //user can add item, or change item after created;
    public void changeItem(String itemName,GridMenuItem newItem){
        mMNameMenus.put(itemName,newItem);
        if(!mAMenuNames.contains(itemName)){
        	mAMenuNames.add(itemName);
        }
        if(menuGrid!=null){
		    menuGrid.setAdapter(getMenuAdapter());
        }
    }
    public void changeItem(String itemName,String menustr){
        GridMenuItem item=getItem(itemName);
        item.mMenuText=menustr;
        changeItem(itemName,item);
    }
    public GridMenuItem getItem(String itemName){
        return mMNameMenus.get(itemName);
    }
    public void setItemEnable(String itemName,boolean enable){
        GridMenuItem item=mMNameMenus.get(itemName);
        item.mMenuImageId=(enable)?item.mMenuEnableImageId:item.mMenuDisableImageId;
    }
	private SimpleAdapter getMenuAdapter() {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i <this.mAMenuNames.size(); i++) {
			GridMenuItem gm = mMNameMenus.get(mAMenuNames.get(i));
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", gm.mMenuImageId);
			map.put("itemText", gm.mMenuText);
			data.add(map);
			this.mMapGridMenuItem.put(i, gm);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(mAct, data,
				mLayoutItemMenu, new String[] { "itemImage", "itemText" },
				new int[] { mIdItemImage, mIdItemItext});
		return simperAdapter;
	}

	public boolean openMenu() {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(mAct).setView(menuView).show();
		} else {
			menuDialog.show();
		}
		return false;// 返回为true 则显示系统menu
	}

	
	
	
}
