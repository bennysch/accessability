package com.btp.accessability.data;

import android.graphics.Bitmap;

public class TakinItem {

	public Bitmap bitmap;
	public String takinText;
	
	
	public TakinItem(Bitmap bitmap, String takinText) {
		super();
		this.bitmap = bitmap;
		this.takinText = takinText;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return takinText;
	}
	
	
	
}
