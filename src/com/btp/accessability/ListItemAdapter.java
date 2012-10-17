package com.btp.accessability;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.Item;
import com.btp.accessability.data.SectionData;
import com.btp.accessability.data.TakinItem;
import com.btp.accessability.form.TakinAdapter;

public class ListItemAdapter extends BaseExpandableListAdapter implements DBConstants{

	List<SectionData> mGroups;
	Item [][] mItems;
	//	Fix[][][] fix1s;
	//	Fix[][][] fix2s;
	String[][][] mFix1s;
	String[][][] mFix2s;
	View[][] mItemLoaded; // to be removed after persistance is in place; 
	TakinItem[] mTakinArray;
	Context ctxt =  null;
	ItemList parent = null;
	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;
	String mSheetId = "";

	public ListItemAdapter(){
		super();
		init(null, null, "");
	}

	public ListItemAdapter(Context c, String sId){
		super();

		init(c, null, sId);

	}

	public ListItemAdapter(Context c, ItemList parent, String sId){
		super();
		init(c, parent, sId);
	}

	private void init(Context c, ItemList parent, String sId){

		ctxt = c;
		this.parent = parent;
		mSheetId = sId;
		int i,j;
		try {
			mDbHelper = new DatabaseHelper(ctxt);
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		fillGroups();
		fillItems();
		fillFixs();
		fillTakinArray();

		mItemLoaded = new View[mGroups.size()][];
		for(i = 0; i < mGroups.size(); i++){
			mItemLoaded[i] = new View[mItems[i].length];
			for (j = 0; j < mItems[i].length; j++){
				mItemLoaded[i][j] = null;
			}
		}

		mDb.close();

	}

	//fetch group titles from the DB
	private void fillGroups() {	
		//read all sections into group list
		String[] args = new String[1];
		SectionData group;
		args[0] = new String(mSheetId);
		int i;
		int count;
		//Cursor c = mDb.query(FORM_SECTION_TABLE, null, SHEET_ID+" = '?'", args, null, null, null);
		Cursor c = mDb.query(FORM_SECTION_TABLE, null, SHEET_ID+" = '"+mSheetId+"'", null, null, null, null);
		//Cursor cc = mDb.query(FORM_SECTION_TABLE, null, null, null, null, null, null);
		count = c.getCount();
		mGroups = new ArrayList<SectionData>();

		i = 0;
		if(c.moveToFirst()){
			do{
				group = new SectionData();
				group.sectionId = c.getString(c.getColumnIndex(SECTION_ID));
				group.sectionTitle = c.getString(c.getColumnIndex(SECTION_TITLE));
				group.sheetId = mSheetId;
				group.duplicateId = "0";
				//group.canDuplicate = ! c.getString(c.getColumnIndex(CAN_DUPLICATE)).equals(""); // true if not empty
				group.canDuplicate =  (c.getInt(c.getColumnIndex(CAN_DUPLICATE)) == 1);
				mGroups.add(group);
			} while(c.moveToNext());
		}
	}

	//fetch Item data from the DB
	private void fillItems(){

		int i, j, k;
		int count;
		Cursor c;
		Item item;
		String[] args = new String[1];
		String query = null;
		mItems = new Item[mGroups.size()][];

		for(i = 0; i < mGroups.size(); i++){

			//			/*debug*/		Cursor cc = mDb.query(FORM_ITEM_TABLE, null, null, null, null, null, null);
			//			/*debug*/		Log.i(FORM_ITEM_TABLE , "count = "+cc.getCount());
			//			/*debug*/		Log.i("columns", cc.getColumnName(0)+" <> "+ cc.getColumnName(1)+" <> "+ cc.getColumnName(2)+" <> "+ cc.getColumnName(3)+" <> "+ cc.getColumnName(4)+" <> "+ cc.getColumnName(5)+" <> "+ cc.getColumnName(6));
			//			/*debug*/		cc.moveToFirst();
			//			/*debug*/		do {
			//				/*debug*/			Log.i(FORM_ITEM_TABLE, cc.getString(0) +" <> "+ cc.getString(1)+" <> "+ cc.getString(2)+" <> "+ cc.getString(3)+" <> "+ cc.getString(4)+" <> "+ cc.getString(5)+" <> "+ cc.getString(6));
			//			/*debug*/		} while(cc.moveToNext());

			args[0] = mGroups.get(i).sectionId;
			query = new String( SECTION_ID+" = '"+ mGroups.get(i).sectionId+"'");
			c = mDb.query(FORM_ITEM_TABLE, null, query, null, null, null, null);
			count = c.getCount();
			if (count > 0) {
				c.moveToFirst();
				mItems[i] = new Item[count];
				j = 0;
				do {
					item = new Item();
					item.itemId = c.getString(c.getColumnIndex(ITEM_ID));
					item.itemShortText = c.getString(c.getColumnIndex(SHORT_TEXT));
					item.ItemLongText = c.getString(c.getColumnIndex(LONG_TEXT));
					item.doMeasure = !c.getString(c.getColumnIndex(DO_MEASURE)).equals("");
					item.doPhoto = !c.getString(c.getColumnIndex(DO_PHOTO)).equals("");
					mItems[i][j++] = item;

				} while (c.moveToNext());
			}

		}

	}

	//fetch fix data from the DB
	private void fillFixs(){
		int i, j, k, l;
		int count;
		Cursor c;
		String query;
		String qFix1 = " and "+FIX_LEVEL+" = '"+FORM_FIX_1+"'";
		String qFix2 = " and "+FIX_LEVEL+" = '"+FORM_FIX_2+"'";		

		mFix1s = new String[mGroups.size()][][];
		mFix2s = new String[mGroups.size()][][];
		for(i = 0;i < mGroups.size(); i++){
			mFix1s[i] = new String[mItems[i].length][];
			mFix2s[i] = new String[mItems[i].length][];
			for (j = 0; j < mItems[i].length; j++){
				query = new String(ITEM_ID+" = '"+mItems[i][j].itemId)+"'"; 
				c = mDb.query(FORM_FIX_TABLE, null, query + qFix1, null, null, null, null);
				count = c.getCount();
				if(count > 0){
					mFix1s[i][j] = new String[count];
					k = 0;
					c.moveToFirst();
					do{
						mFix1s[i][j][k] = c.getString(c.getColumnIndex(FIX_TEXT));
						k++;
					} while(c.moveToNext());
				}
				c.close();

				c = mDb.query(FORM_FIX_TABLE, null, query + qFix2, null, null, null, null);
				count = c.getCount();
				if(count > 0){
					mFix2s[i][j] = new String[count];
					k = 0;
					c.moveToFirst();
					do{
						mFix2s[i][j][k] = c.getString(c.getColumnIndex(FIX_TEXT));
						k++;
					} while(c.moveToNext());
				}
				c.close();
			}
		}
	}

	//fill the images for the takin, not takin, not relevant spinner
	void fillTakinArray(){
		mTakinArray = new TakinItem[3];

		mTakinArray[0] = new TakinItem(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.takin_button), "Takin");
		mTakinArray[1] = new TakinItem(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.not_takin_button), "Not Takin");
		mTakinArray[2] = new TakinItem(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.irrelevant_button), "irrelevant");
	}

	/////////////////////////////////////////////////////
	//      implement abstract class methods
	/////////////////////////////////////////////////////

	//////////////////  Children  //////////////////
	public Object getChild(int gid, int iid) {
		return mItems[gid][iid];
	}

	public long getChildId(int gid, int iid) {
		return gid*100+iid;
	}

	public View getChildView(int gid, int cid, boolean arg2, View arg3,
			ViewGroup arg4) {

		if(mItemLoaded[gid][cid] == null){
			final String shortText =  mItems[gid][cid].itemShortText;
			final String longText =  mItems[gid][cid].ItemLongText;

			LayoutInflater inflater = LayoutInflater.from(ctxt);
			View child = (View)inflater.inflate(R.layout.item1, null);

			//child.setmShortTExt(mItems[gid][cid].itemShortText);
			((TextView)child.findViewById(R.id.short_text)).setText(shortText);
			((ImageButton)child.findViewById(R.id.measure)).setVisibility((mItems[gid][cid].doMeasure) ? View.VISIBLE : View.INVISIBLE);
			((ImageButton)child.findViewById(R.id.photo)).setVisibility((mItems[gid][cid].doPhoto) ? View.VISIBLE : View.INVISIBLE);

			//add options to dropdown boxes
			ArrayAdapter<String> fixAdapter1 = new ArrayAdapter<String>(ctxt, R.layout.drop_down_text, mFix1s[gid][cid]);
			((Spinner)child.findViewById(R.id.fix_1)).setAdapter(fixAdapter1);
			ArrayAdapter<String> fixAdapter2 = new ArrayAdapter<String>(ctxt, R.layout.drop_down_text, mFix2s[gid][cid]);
			((Spinner)child.findViewById(R.id.fix_2)).setAdapter(fixAdapter2);
			TakinAdapter<TakinItem> takinAdapter = new TakinAdapter<TakinItem>(ctxt, R.layout.takin_selection, R.id.takin_text, mTakinArray);
			Spinner takin = ((Spinner)child.findViewById(R.id.takin));
			takin.setAdapter(takinAdapter);



			//Add content to the more info [?] button
			child.findViewById(R.id.more_info).setOnClickListener(new OnClickListener() {

				String mMoreInfo = longText;
				public void onClick(View v) {
					new AlertDialog.Builder(ctxt)
					.setTitle(shortText)
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

			//TODO change "takin" from image button to drop-down box.
			//		child.findViewById(R.id.takin).setOnClickListener(new OnClickListener() {
			//
			//			public void onClick(View v) {
			//			}
			//		});

			mItemLoaded[gid][cid] = child;

			return  child;
		}
		else
			return mItemLoaded[gid][cid];
	}

	public int getChildrenCount(int gid) {
		
		return mItems[Integer.valueOf(mGroups.get(gid).sectionId)].length; 
		//return mItems[gid].length;
	}

	//////////////////  Groups   //////////////////
	public Object getGroup(int inx) {
		return mGroups.get(inx);
	}

	public void addGroup(int inx, SectionData group){
		mGroups.add(inx, group);
	}

	public int getGroupCount() {
		return mGroups.size();
	}

	public long getGroupId(int gid) {
		// TODO if the group has other ID then it's index this method should return it.
		return gid;
	}

	public View getGroupView(int gid, boolean isExpanded, View convertView, ViewGroup parent) {

			TextView textView = getGenericView();

			textView.setText(getGroup(gid).toString());
			return textView;

	}

	// a normal expanded list title
	public TextView getGenericView() {

		Resources res = ctxt.getResources();
		Drawable bg = res.getDrawable(R.layout.gradient_titlegroup);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, 50);
		TextView textView = new TextView(ctxt);
		textView.setLayoutParams(lp);
		textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

		textView.setBackgroundDrawable(bg);
		//textView.setBackgroundColor(Color.rgb(221, 221, 221));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_SP ,22.0f);
		textView.setTypeface(Typeface.DEFAULT_BOLD);//,
		//		style.TextAppearance_Widget_Button);

		textView.setTextColor(Color.BLACK);

		textView.setPadding(40, 0, 0, 0);
		return textView;

	}

	// a title for an expanded list group that can be duplicated.
	public View getDuplicateView(String title) {

		Resources res = ctxt.getResources();
		Drawable bg = res.getDrawable(R.layout.gradient_titlegroup);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(ctxt);
		View dupView = (View)inflater.inflate(R.layout.section_with_duplicate, null);

		dupView.setLayoutParams(lp);
		dupView.setBackgroundDrawable(bg);
		TextView tv = (TextView)(dupView.findViewById(R.id.section_title));
		tv.setText(title);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP ,22.0f);
		tv.setTypeface(Typeface.DEFAULT_BOLD);//,
		tv.setTextColor(Color.BLACK);
		dupView.setPadding(40, 0, 0, 0);
		
		Button dup = (Button)dupView.findViewById(R.id.section_duplicate);
		dup.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		return dupView;

	}

	// Yes No Questions
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


}
