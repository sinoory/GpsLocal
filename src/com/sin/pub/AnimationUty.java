package com.sin.pub;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationUty{
	public static String TAG="";
	public static void animateLeft2RightView(View v) {

		AnimationSet animSet = new AnimationSet(true);

		RotateAnimation ranim = new RotateAnimation(0f, 180f,
				Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f); // , 200, 200);

		ranim.setDuration(1000*3);

		TranslateAnimation tanim=new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

		tanim.setDuration(600);

		//animSet.addAnimation(ranim);
		animSet.addAnimation(tanim);

		v.setAnimation(animSet);
	}

}