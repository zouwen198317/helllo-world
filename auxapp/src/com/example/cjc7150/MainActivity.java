package com.example.cjc7150;

import android.util.Log;

public class MainActivity {
	
	public static int setmode(byte value){
		Log.i("cxs","=======com.example.cjc7150==MainActivity======="+Setmode(value));
		return Setmode(value);
	}
	
	public static int getmode(){
		Log.i("cxs","=======com.example.cjc7150==MainActivity====Getmode()==="+Getmode());
		return Getmode();
	}

	public native static int Setmode(byte value); // 设置0或者1
	public native static int Getmode(); // 获取底层通道
	static {
		System.loadLibrary("cjc7150");
	}
}
