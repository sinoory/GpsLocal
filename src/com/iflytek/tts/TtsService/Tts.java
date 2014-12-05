package com.iflytek.tts.TtsService;

import android.util.Log;

public final class Tts
{
  static
  {
    try
    {
      System.loadLibrary("Aisound");
      //return;
    }
    catch (Exception localException)
    {
      Log.e("Error", "Failed to load lib.\n" + localException.getMessage());
    }
  }

  public static native int JniCreate(String paramString);

  public static native int JniDestory();

  public static native int JniGetParam(int paramInt);

  public static native int JniSetParam(int paramInt1, int paramInt2);

  public static native int JniSpeak(String paramString);

  public static native int JniStop();
}

/* Location:           /home/sin/wkspace/soft/apk/vbook/tst/classes_dex2jar.jar
 * Qualified Name:     com.iflytek.tts.TtsService.Tts
 * JD-Core Version:    0.6.0
 */