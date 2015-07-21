package com.sin.pub.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.util.EncodingUtils;

import android.database.Cursor;
import android.net.Uri;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class AndFileUty{
	static final String TAG="AndFileUty";
	public static String save(Context context,String stringToWrite,String sdDirname,String fileName){
		return saveEx(context,stringToWrite,sdDirname,fileName,true);
	}
	
	public static String clear(Context context,String sdDirname,String fileName){
		return saveEx(context,"",sdDirname,fileName,false);
	}
	public static int getLineNum(String filename){
        int cnt = 0;
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(filename));
            @SuppressWarnings("unused")
            String lineRead = "";
            while ((lineRead = reader.readLine()) != null) {
            }
            cnt = reader.getLineNumber();
        } catch (Exception ex) {
            cnt = -1;
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return cnt;
	}

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {

            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

	/**
	 * 
	 * @param context
	 * @param assertResource ,such "resource.irf"=asserts/resource.irf
	 * @param toFile : "/data/data/com.xxx.xxx/xxx/Resource.irf" ; or "/sdcard/Resource.irf"
	 * @return
	 */
	public static int rawCopy(Context context,String assertResource,String toFile){
		try {
			   //InputStream stream = context.getResources().openRawResource(assertResourceId);
			   AssetManager assets = context.getAssets();   
			   InputStream stream = assets.open(assertResource);
			   
			   File file = new File(toFile);
			   
			   File dir=new File(file.getParent());
			   if(!dir.exists()){
				   dir.mkdirs();
			   }
			   OutputStream out = new FileOutputStream(file);//context.openFileOutput(toFile,
					   //context.MODE_PRIVATE);
			//   OutputStream out = openFileOutput("/sdcard/Resource.irf", Activity.MODE_PRIVATE);
			   

			   byte buf[] = new byte[16384];
			   int numread = 0;
			   do {
			    numread = stream.read(buf);
			    if (numread <= 0) {
			     break;
			    } else {
			     out.write(buf, 0, numread);
			    }
			   } while (true);
			   out.close();

			   return 0;
			  } catch (IOException e) {
			   e.printStackTrace();
			   return -1;
			  }
	}
	public static String readToString(String fileName,String encoding) {
		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(filecontent);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			return new String(filecontent, encoding);
		} catch (Exception e) {
			System.err.println("The OS does not support " + encoding);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param context
	 * @param sdDirname example: if /sdcard/testDir/ , then should be /testDir/ . or null if the filename is full
	 * @param fileName
	 * @return
	 */
    public static int isExist(Context context,String sdDirname,String fileName){
		//if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
		try{
			String foldername="";
			if(sdDirname!=null){
			foldername = Environment.getExternalStorageDirectory().getPath()+ sdDirname;
		    File folder = new File(foldername);
			    if (folder == null || !folder.exists()) {
			    	//SCRIPT_MARK.d(TAG,"not exist "+foldername);
	                return -1;
			    }
			}
		    
			File targetFile = new File(foldername + fileName);
				if(!targetFile.exists()){
					////SCRIPT_MARK.d(TAG,"not exist targetFile "+targetFile);
                    return -2;
				}else{
                    return 0;
				}
			 } catch (Exception e) {
                e.printStackTrace();
				return -3;
			 }
    }
	
    public static String readFromAssets(Context ctx,String fileName){
        String result = null;
        try {
            InputStream in = ctx.getResources().getAssets().open(fileName);
            int lenght = in.available();
            byte[]  buffer = new byte[lenght];
            in.read(buffer);
            result = EncodingUtils.getString(buffer, "utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

public static String saveEx(Context context,String stringToWrite,String sdDirname,String fileName,boolean bAppend){
		
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			String foldername = Environment.getExternalStorageDirectory().getPath()+ sdDirname;
		    File folder = new File(foldername);
		    
		    if (folder == null || !folder.exists()) {
		    	folder.mkdirs();
		    }
		    
			File targetFile = new File(foldername + fileName);
			OutputStreamWriter osw;
	        
			 try{
				if(!targetFile.exists()){
					targetFile.createNewFile();
					osw = new OutputStreamWriter(new FileOutputStream(targetFile),"utf-8");
					osw.write(stringToWrite);  
					osw.close();
				}else{
					osw = new OutputStreamWriter(new FileOutputStream(targetFile,bAppend),"utf-8");
					osw.write("\n"+stringToWrite);
					osw.flush();
					osw.close();
				}
				return foldername + fileName;
			 } catch (Exception e) {
				Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();
				return null;
			 }
		}else{
			Toast.makeText(context,"未发现SD卡！",Toast.LENGTH_LONG).show();
			return null;
		}
		
		 
	    
	  }
}
