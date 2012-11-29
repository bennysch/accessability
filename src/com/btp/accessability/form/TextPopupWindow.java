package com.btp.accessability.form;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.btp.accessability.R;
import com.btp.accessability.data.ItemData;

public class TextPopupWindow extends PopupWindow {
	
	Context mCtxt;
	TextView mSourceText;
	ItemData mItemData;

	public TextPopupWindow(Context ctxt) {
		super(ctxt);
		mCtxt = ctxt;
	}

	public void init(TextView source, ItemData itemData, int w, int h){

		LayoutInflater inflator = (LayoutInflater)mCtxt.getSystemService(mCtxt.LAYOUT_INFLATER_SERVICE);
		View view = inflator.inflate(R.layout.popup_item, null);

		mSourceText = source;
		mItemData = itemData;
		this.setContentView(view);
		this.setFocusable(true);
		this.setWidth(w);
		this.setHeight(h);

		Button sendB = (Button)view.findViewById(R.id.popup_send);
		sendB.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.e("Popup Send Button", this.getClass().getName());
				Log.e("Popup Send Button v", v.getClass().getName());
				Log.e("Popup Send Button v.parent 1", v.getParent().getClass().getName());
				Log.e("Popup Send Button v.parent 2", v.getParent().getParent().getClass().getName());
				Log.e("Popup Send Button v.parent 3", v.getParent().getParent().getParent().getClass().getName());
				Log.e("Popup Send Button v.parent 4", v.getParent().getParent().getParent().getParent().getClass().getName());
				
			}
		});
	}
	
	
	
	
	
	
}
