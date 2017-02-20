package com.console.canreader.fragment.SSJeepFreedom;

import com.console.canreader.R;
import com.console.canreader.activity.BaseActivity;
import com.console.canreader.service.CanInfo;
import com.console.canreader.utils.Contacts;
import com.console.canreader.view.CarSelectedView;
import com.console.canreader.view.CarSelectedView.OnPositionChangedListener;
import com.console.canreader.view.VerticalSeekBar;
import com.console.canreader.view.VerticalSeekBar.OnProgressChangedListener;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuPaAcitivity extends BaseActivity {

	private VerticalSeekBar seekBarTRE;
	private VerticalSeekBar seekBarMID;
	private VerticalSeekBar seekBarBAS;
	private VerticalSeekBar seekBarVOL;
	private TextView treTv;
	private TextView midTv;
	private TextView basTv;
	private TextView volTv;
	private CarSelectedView mCarSelectedView;

	private ImageView imageView;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Contacts.MSG_UPDATA_UI:
				try {
					CanInfo caninfo = getCanInfo();
					seekBarTRE.setNumProgress(caninfo.TRE_VOLUME+9);
					seekBarMID.setNumProgress(caninfo.MID_VOLUME+9);
					seekBarBAS.setNumProgress(caninfo.BAS_VOLUME+9);
					seekBarVOL.setNumProgress(caninfo.EQL_VOLUME+9);
					mCarSelectedView.setPosition((int)((caninfo.LR_BALANCE+9)/9f*7f),(int)((caninfo.FB_BALANCE+9)/9f*7f)
							);
	
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.equalizer_layout);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		mCarSelectedView = (CarSelectedView) findViewById(R.id.mMainCarImg);

		seekBarTRE = (VerticalSeekBar) findViewById(R.id.SeekBarTRE);
		seekBarMID = (VerticalSeekBar) findViewById(R.id.SeekBarMID);
		seekBarBAS = (VerticalSeekBar) findViewById(R.id.SeekBarBas);
		seekBarVOL = (VerticalSeekBar) findViewById(R.id.SeekBarVol);

		seekBarTRE.setusrProgress(18);
		seekBarMID.setusrProgress(18);
		seekBarBAS.setusrProgress(18);
		seekBarVOL.setusrProgress(0x3F);

		treTv = (TextView) findViewById(R.id.tre_tv);
		midTv = (TextView) findViewById(R.id.mid_tv);
		basTv = (TextView) findViewById(R.id.bas_tv);
		volTv = (TextView) findViewById(R.id.vol_tv);

		imageView = (ImageView) findViewById(R.id.menu);

		seekBarMID
				.setOnProgressChangedListener(new OnProgressChangedListener() {

					@Override
					public void OnChange(int progress) {
						// TODO Auto-generated method stub
						midTv.setText((progress-9) + "");
					}

					@Override
					public void OnStop(int progress) {
						// TODO Auto-generated method stub
						sendMsg("5AA502AD05" + adjustNum(progress-9));

					}
				});

		seekBarTRE
				.setOnProgressChangedListener(new OnProgressChangedListener() {

					@Override
					public void OnChange(int progress) {
						// TODO Auto-generated method stub
						treTv.setText((progress-9)+ "");

					}

					@Override
					public void OnStop(int progress) {
						// TODO Auto-generated method stub
						sendMsg("5AA502AD06" + adjustNum(progress-9));
					}
				});

		seekBarBAS
				.setOnProgressChangedListener(new OnProgressChangedListener() {

					@Override
					public void OnChange(int progress) {
						// TODO Auto-generated method stub
						basTv.setText((progress-9) + "");
					}

					@Override
					public void OnStop(int progress) {
						// TODO Auto-generated method stub
						sendMsg("5AA502AD04" + adjustNum(progress-9));

					}
				});

		seekBarVOL
				.setOnProgressChangedListener(new OnProgressChangedListener() {

					@Override
					public void OnChange(int progress) {
						// TODO Auto-generated method stub
						volTv.setText(progress + "");
					}

					@Override
					public void OnStop(int progress) {
						// TODO Auto-generated method stub
						sendMsg("5AA502AD0105");

					}
				});

		mCarSelectedView
				.setOnValueChangedListener(new OnPositionChangedListener() {

					@Override
					public void OnChange(int mColumnIndex, int mRowIndex) {
						// TODO Auto-generated method stub
						sendMsg("5AA502AD02" + adjustNum((int) ((mColumnIndex-7)/7f*9f)));
						sendMsg("5AA502AD03" + adjustNum((int) ((mRowIndex-7)/7f*9f)));
					}
				});
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MenuPaAcitivity.this,
						MenuPaSelectActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.left_in, R.anim.stay);
			}
		});

	}

	private String adjustNum(int num) {
		String tmp = Integer.toHexString(num).toUpperCase();
		if (tmp.length() > 2) {
			tmp = tmp.substring(tmp.length() - 2, tmp.length());
		} else if (tmp.length() == 1) {
			tmp = "0" + tmp;
		}
		return tmp;
	}

	@Override
	public void show(CanInfo mCaninfo) {
		// TODO Auto-generated method stub
		super.show(mCaninfo);
		
		//mHandler.sendEmptyMessageDelayed(Contacts.MSG_UPDATA_UI, 500);
	}

	@Override
	public void serviceConnected() {
		// TODO Auto-generated method stub
		super.serviceConnected();
		mHandler.sendEmptyMessageDelayed(Contacts.MSG_UPDATA_UI, 500);
	}

}
