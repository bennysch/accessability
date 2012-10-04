package com.btp.accessability;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.Instruction;

public class InstructionsAdapter extends BaseExpandableListAdapter implements DBConstants{

	Instruction inst = new Instruction();
	DatabaseHelper dbHelper;
	SQLiteDatabase db = null;
	Context context;
	Instruction[] instgroups;
	Instruction[] instructions;
	Instruction instruction;
	


	public InstructionsAdapter(Context context){
		this.context = context;

		//get info from DB
		Instruction inst = new Instruction();
		Cursor dbRes;
		//open DB
		try {
			dbHelper = new DatabaseHelper(context);
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		dbRes = db.rawQuery("select * from "+FORM_INSTRUCTIONS_TABLE+" where "+INST_ID+" = '-'", null);
		int i = 0;
		instgroups = new Instruction[1];
		if(dbRes.moveToFirst()){
			instruction = new Instruction();
			instruction.setID(dbRes.getString(0));
			instruction.setText(dbRes.getString(1));
			instgroups[i] = instruction;
		}
		dbRes.close();
		dbRes = db.rawQuery("select * from "+FORM_INSTRUCTIONS_TABLE+" where "+INST_ID+" != '-'", null);
		instructions = new Instruction[dbRes.getCount()];
		if(dbRes.moveToFirst()){
			do{
				instruction = new Instruction();
				instruction.setID(dbRes.getString(0));
				instruction.setText(dbRes.getString(1));
				instructions[i++] = instruction;
			}while(dbRes.moveToNext());
		}
		dbRes.close();
		// close DB
		db.close();
	}



	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return instructions[childPosition];
	}



	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return Integer.valueOf(instructions[childPosition].getID());
	}



	public View getChildView(int gid, int cid,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View child = View.inflate(context, R.layout.instruction_item, null);
		((TextView)child.findViewById(R.id.inst_id)).setText(instructions[cid].getID());
		((TextView)child.findViewById(R.id.inst_text)).setText(instructions[cid].getText());
		return child;
	}



	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return instructions.length;
	}



	public Object getGroup(int groupPosition) {
		
		return instgroups[groupPosition];
	}



	public int getGroupCount() {
		// TODO Auto-generated method stub
		return 1;
	}



	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}



	public View getGroupView(int gid, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView textView = getGenericView();

		textView.setText(((Instruction)getGroup(gid)).getText());
		return textView;
		
	}

	public TextView getGenericView() {

		Resources res = context.getResources();
		Drawable bg = res.getDrawable(R.layout.gradient_titlegroup);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 40);
		TextView textView = new TextView(context);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		textView.setBackgroundDrawable(bg);
		//textView.setBackgroundColor(Color.rgb(221, 221, 221));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP ,22.0f);
		textView.setTypeface(Typeface.DEFAULT_BOLD);//,
		//		style.TextAppearance_Widget_Button);

		textView.setTextColor(Color.BLACK);

		textView.setPadding(40, 0, 0, 0);
		return textView;

	}
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}



	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}


}
