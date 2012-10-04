package com.btp.accessability.form;

import com.btp.accessability.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class CopyOfItemView extends View {

	public CopyOfItemView(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
	}

	//String mShortTExt;
	String mMoreInfo;
	SpinnerAdapter mFix1Options;
	SpinnerAdapter mFix2Options;
	
	Context mCtxt;

	//View view;
	Button moreInfo ;
	ImageButton takin;
	ImageButton doMeasure;
	ImageButton doPhoto;
	Spinner fix1;
	Spinner fix2;


	public void init(Context ctxt) {
		mCtxt = ctxt;
		//assigb value to 

		CopyOfItemView.inflate(ctxt, R.layout.item1, null);
		moreInfo = (Button)this.findViewById(R.id.more_info);
		takin = (ImageButton)this .findViewById(R.id.takin);
		doMeasure = (ImageButton)this .findViewById(R.id.measure);
		doPhoto = (ImageButton)this .findViewById(R.id.photo);
		fix1 = (Spinner)this.findViewById(R.id.fix_1);
		fix2 = (Spinner)this.findViewById(R.id.fix_2);

		
		
		
		
		moreInfo.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AlertDialog.Builder(mCtxt)
				.setTitle(mCtxt.getString(R.string.not_yet_title))
				.setMessage(mMoreInfo)
				.setNeutralButton("Close",
						new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dlg, int sumthin) {
						// do nothing – it will close on its
						// own
					}
				}).show();
			}
		});
		
		
	}


	
	public String getmShortTExt() {
		//return mShortTExt;
		return ((TextView)this.findViewById(R.id.short_text)).getText().toString();
	}


	public void setmShortTExt(String mShortTExt) {
		//this.mShortTExt = mShortTExt;
		((TextView)this.findViewById(R.id.short_text)).setText(mShortTExt);

	}


	public String getmMoreInfo() {
		return mMoreInfo;
	}

	public void setmMoreInfo(String mMoreInfo) {
		this.mMoreInfo = mMoreInfo;
	}

	public SpinnerAdapter getmFix1Options() {
		return mFix1Options;
	}

	public void setmFix1Options(SpinnerAdapter mFix1Options) {
		this.mFix1Options = mFix1Options;
	}

	public SpinnerAdapter getmFix2Options() {
		return mFix2Options;
	}

	public void setmFix2Options(SpinnerAdapter mFix2Options) {
		this.mFix2Options = mFix2Options;
	}





}
