package com.btp.accessability.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.btp.accessability.ListItemAdapter;

public class AccessTextView extends TextView {

	int mGid;
	int mCid;
	ListItemAdapter mLia;

	
	public AccessTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AccessTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AccessTextView(Context context) {
		super(context);
	}

	
	public void setIdeces(int gid, int cid, ListItemAdapter lia) {
		this.mGid = gid;
		this.mCid = cid;
		this.mLia = lia;
	}

	public int getGid() {
		return mGid;
	}

	public void setGid(int gid) {
		this.mGid = gid;
	}

	public int getCid() {
		return mCid;
	}

	public void setCid(int cid) {
		this.mCid = cid;
	}

	public ListItemAdapter getmLia() {
		return mLia;
	}

	public void setmLia(ListItemAdapter lia) {
		this.mLia = lia;
	}

}
