package com.console.parking.control;

import com.console.canreader.service.CanInfo;
import com.console.canreader.service.ICanCallback;
import com.console.canreader.service.ICanService;
import com.console.parking.MainActivity;
import com.console.parking.R;
import com.console.parking.util.BytesUtil;
import com.console.parking.util.Contacts;
import com.console.parking.util.PreferenceUtil;
import com.console.parking.view.RailLineView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class SSTrumpchiGS5Control implements OnClickListener {

	private MainActivity mMainActivity;
	RelativeLayout layout;

	private ICanService mISpService;
	CanInfo mCanInfo;
	ImageView iv1;
	Button button;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				// dealwithPacket(Volkswagen)msg.obj);
				mCanInfo=(CanInfo) msg.obj;
				break;
			default:
				break;
			}
		}
	};

	public SSTrumpchiGS5Control(MainActivity mMainActivity,
			RelativeLayout layout) {
		// TODO Auto-generated constructor stub
		this.mMainActivity = mMainActivity;
		this.layout = layout;
		syncCanName();
		initView();
		bindService();
	}

	private void initView() {
		// TODO Auto-generated method stub
		layout.findViewById(R.id.radar_layout).setVisibility(View.GONE);
		layout.findViewById(R.id.rail_line).setVisibility(View.GONE);
		layout.findViewById(R.id.sstrumpchigs5_layout).setVisibility(
				View.VISIBLE);

		iv1 = (ImageView) layout.findViewById(R.id.sstru_iv);
		button = (Button) layout.findViewById(R.id.sstru_button);

		iv1.setOnClickListener(this);
		button.setOnClickListener(this);

	}

	int SterringSave = 0;

	private void updateView(CanInfo canInfo) {
		// TODO Auto-generated method stub

	}

	private void bindService() {
		Intent intent = new Intent();
		intent.setClassName("com.console.canreader",
				"com.console.canreader.service.CanService");
		mMainActivity.bindService(intent, mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void unBindService() {
		try {
			if (mISpService != null)
				mISpService.removeCliend(mICallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		mMainActivity.unbindService(mServiceConnection);
	}

	private ICanCallback mICallback = new ICanCallback.Stub() {

		@Override
		public void readDataFromServer(CanInfo canInfo) throws RemoteException {
			Message msg = new Message();
			msg.what = 1;
			msg.obj = canInfo;
			mHandler.sendMessage(msg);
		}
	};

	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mISpService = ICanService.Stub.asInterface(service);
			try {
				mISpService.addClient(mICallback);
				mCanInfo=mISpService.getCanInfo();
				Log.i("cxs","=getCanInfo==mCanInfo==="+mCanInfo);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			// sendMsg(Contacts.HEX_HOME_TO_FM);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mISpService = null;
		}
	};

	public void sendMsg(String msg) {
		try {
			if (mISpService != null) {
				switch (canFirtName) {
				case Contacts.CANFISRTNAMEGROUP.RAISE:
					mISpService.sendDataToSp(BytesUtil.addRZCCheckBit(msg));
					break;
				case Contacts.CANFISRTNAMEGROUP.HIWORLD:
					mISpService.sendDataToSp(BytesUtil.addSSCheckBit(msg));
					break;
				default:
					break;
				}

			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static String canName = "";
	public static String canFirtName = "";

	private void syncCanName() {
		canName = PreferenceUtil.getCANName(mMainActivity);
		canFirtName = PreferenceUtil.getFirstTwoString(mMainActivity, canName);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sstru_button:
			if(mCanInfo!=null){
				int temp=mCanInfo.CAMERA_MODE+1;
				if(temp>5){
					temp=1;
				}
				if(temp<1){
					temp=5;
				}
				sendMsg("5AA502F2070"+String.valueOf(temp));
			}	
			break;
		case R.id.sstru_iv:
			sendMsg("5AA502F20706");
			break;
		default:
			break;
		}
	}

}