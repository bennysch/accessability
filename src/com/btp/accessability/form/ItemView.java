package com.btp.accessability.form;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.btp.accessability.R;

public class ItemView extends LinearLayout {
//public class ItemView extends ViewGroup {

	//String mShortTExt;
	String mMoreInfo;
	SpinnerAdapter mFix1Options;
	SpinnerAdapter mFix2Options;
	AbsListView.LayoutParams mLayoutParams;
	
	Context mCtxt;

	//View view;
	Button moreInfo ;
	ImageButton takin;
	ImageButton doMeasure;
	ImageButton doPhoto;
	Spinner fix1;
	Spinner fix2;

	public ItemView(Context context) {
		super(context);
		
		init(context);
	}

	public ItemView(Context context, AttributeSet attr) {
		super(context, attr);
		
		init(context);
	}


	public void init(Context ctxt) {
		mCtxt = ctxt;
		//assigb value to 

//		//ItemView.inflate(ctxt, R.layout.item1, null);
		LayoutInflater inflater = LayoutInflater.from(ctxt);
		LinearLayout ll = (LinearLayout)inflater.inflate(R.layout.item1, this);
		//ViewGroup ll = (ViewGroup)inflater.inflate(R.layout.item1, this);
		this.addView(ll);
		
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
		
		mLayoutParams = new AbsListView.LayoutParams(super.getLayoutParams());
	}
	
	@Override
	public AbsListView.LayoutParams getLayoutParams() {
		return mLayoutParams;
	}

	
	
	public String getmShortTExt() {
		//return mShortTExt;
		return ((TextView)this.findViewById(R.id.short_text)).getText().toString();
	}


	public void setmShortTExt(String mShortTExt) {
		//this.mShortTExt = mShortTExt;
		TextView tv = (TextView)this.findViewById(R.id.short_text);
		tv.setText(mShortTExt);

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

//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		for(int i = 0; i < this.getChildCount(); i++){
//			this.getChildAt(i).layout(l, t, r, b);
//		}
//	}





}
