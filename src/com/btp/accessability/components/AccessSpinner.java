package com.btp.accessability.components;

import com.btp.accessability.ListItemAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

public class AccessSpinner extends Spinner {
	
	int mGid;
	int mCid;
	ListItemAdapter mThis;

	public AccessSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AccessSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AccessSpinner(Context context) {
		super(context);
	}

	public void setIdeces(int gid, int cid, ListItemAdapter lia) {
		this.mGid = gid;
		this.mCid = cid;
		this.mThis = lia;
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

	public ListItemAdapter getmThis() {
		return mThis;
	}

	public void setmThis(ListItemAdapter mThis) {
		this.mThis = mThis;
	}
	
}
