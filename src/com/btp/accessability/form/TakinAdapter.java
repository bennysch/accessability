package com.btp.accessability.form;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.btp.accessability.data.TakinItem;

public class TakinAdapter<T> extends ArrayAdapter<T> {
	Context context;
	
	
	public TakinAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
		super(context, resource, textViewResourceId, objects);
		this.context = context;
	}

	public TakinAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv= new ImageView(context);
		iv.setImageBitmap(((TakinItem)getItem(position)).bitmap);
		LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
		iv.setLayoutParams(params);
		return iv;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ImageView iv= new ImageView(context);
		iv.setImageBitmap(((TakinItem)getItem(position)).bitmap);
		AbsListView.LayoutParams params = new AbsListView.LayoutParams( LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT);
		iv.setLayoutParams(params);
		return iv;
	}
	
	
	
}
