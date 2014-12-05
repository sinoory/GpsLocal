package com.iflytek.tts.TtsService;

import android.media.AudioTrack;
import android.util.Log;
import com.iflytek.tts.TtsService.b;

public class AudioData
{
  private static AudioData a;
  private AudioTrack b;
  private int c = 0;
  private b d;

  public static AudioData a()
  {
    if (a == null)
      a = new AudioData();
    return a;
  }

  private void a(int paramInt)
  {
    this.c = paramInt;
    if (this.d != null)
      this.d.c(this.c);
  }

  private void a(int paramInt, byte[] paramArrayOfByte)
  {
    AudioTrack localAudioTrack = d();
    if (localAudioTrack.getPlayState() != 3)
      localAudioTrack.play();
    localAudioTrack.write(paramArrayOfByte, 0, paramInt);
  }

  private AudioTrack d()
  {
    if (this.b == null)
      this.b = e();
    return this.b;
  }

  private AudioTrack e()
  {
    AudioTrack localAudioTrack = new AudioTrack(3, 16000, 2, 2, 8000, 1);
    float f = AudioTrack.getMaxVolume();
    localAudioTrack.setStereoVolume(0.9F * f, f * 0.9F);
    Log.d("Tts audio", " AudioTrack create ok");
    return localAudioTrack;
  }

  public static void onJniOutData(int paramInt, byte[] paramArrayOfByte)
  {
    a().a(paramInt, paramArrayOfByte);
  }

  public static void onJniWatchCB(int paramInt)
  {
    a().a(paramInt);
  }

  public void a(b paramb)
  {
    this.d = paramb;
  }

  public void b()
  {
    if (this.b != null)
    {
      this.b.pause();
      this.b.flush();
    }
  }

  public void c()
  {
    if (this.b != null)
    {
      this.b.release();
      this.b = null;
    }
  }
}

/* Location:           /home/sin/wkspace/soft/apk/vbook/tst/classes_dex2jar.jar
 * Qualified Name:     com.iflytek.tts.TtsService.AudioData
 * JD-Core Version:    0.6.0
 */