package com.btp.accessability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.btp.accessability.components.AccessSpinner;
import com.btp.accessability.components.AccessTextView;
import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.Item;
import com.btp.accessability.data.ItemData;
import com.btp.accessability.data.SectionData;
import com.btp.accessability.form.TextPopupWindow;

public class ListItemAdapter extends BaseExpandableListAdapter implements DBConstants{

	final int TAKIN = 0;
	final int NOT_TAKIN = 1;
	final int IRRELEVANT = 2;

	List<SectionData> mGroups;
	Item [][] mItems;
	String[][][] mFix1s;
	String[][][] mFix2s;
	//View[][] mItemLoaded; // to be removed after persistance is in place; 
	public Map<String, ItemData> mSavedItems;
	//	TakinItem[] mTakinArray;
	Bitmap[] mTakinArray;
	Context ctxt =  null;
	ItemList parent = null;
	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;
	int mSheetId = 0;
	int mSurveyId;
	
	String key;
	View child;

	SharedPreferences prefs;

	public ListItemAdapter(){
		this(null, null, 0);
	}

	public ListItemAdapter(Context c, int sId){
		this(c, null, sId);
	}

	public ListItemAdapter(Context c, int sId, Map<String, ItemData> savedItems){
		this(c, null, sId, savedItems);
	}

	public ListItemAdapter(Context c, ItemList parent, int sId){
		this(c, parent, sId, null);
	}

	public ListItemAdapter(Context c, ItemList parent, int sId, Map<String, ItemData> savedItems){
		super();

		ctxt = c;
		prefs = ctxt.getSharedPreferences("com.btp.accessability",  ctxt.MODE_APPEND | ctxt.MODE_WORLD_READABLE);
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


		//initialize  the saved items collection
		if(savedItems == null)
			mSavedItems = new HashMap<String, ItemData>();
		else
			mSavedItems = savedItems;

		loadSavedItems();

		mDb.close();

	}

	//fetch group titles from the DB
	private void fillGroups() {	
		//read all sections into group list
		//String[] args = new String[1];
		SectionData group;
		//args[0] = new String(mSheetId);
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
				group.sectionId = c.getInt(c.getColumnIndex(SECTION_ID));
				group.sectionTitle = c.getString(c.getColumnIndex(SECTION_TITLE));
				group.sheetId = mSheetId;
				group.duplicateId = 0;
				//group.canDuplicate = ! c.getString(c.getColumnIndex(CAN_DUPLICATE)).equals(""); // true if not empty
				group.canDuplicate =  (c.getString(c.getColumnIndex(CAN_DUPLICATE)));
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

			//args[0] = mGroups.get(i).sectionId;

			query = new String( SECTION_ID+" = '"+ mGroups.get(i).sectionId+"'");
			c = mDb.query(FORM_ITEM_TABLE, null, query, null, null, null, null);
			count = c.getCount();
			if (count > 0) {
				c.moveToFirst();
				mItems[i] = new Item[count];
				j = 0;
				do {
					item = new Item();
					item.itemId = c.getInt(c.getColumnIndex(ITEM_ID));
					item.canDuplicate = c.getString(c.getColumnIndex(CAN_DUPLICATE));
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
					mFix1s[i][j] = new String[count + 1];// +1 because we need a place for the first line that says please choos an answer.
					k = 0;
					mFix1s[i][j][k++] = ctxt.getString(R.string.all_well);
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
					mFix2s[i][j] = new String[count + 1];// +1 because we need a place for the first line that says please choos an answer.
					k = 0;
					mFix2s[i][j][k++] = ctxt.getString(R.string.all_well);
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
		//mTakinArray = new TakinItem[3];
		mTakinArray = new Bitmap[3];

		mTakinArray[TAKIN] = BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.takin_button);
		mTakinArray[NOT_TAKIN] = BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.not_takin_button);
		mTakinArray[IRRELEVANT] = BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.irrelevant_button);
	}

	// load the saved items from the DB so when they are displayed the contain saved choices.
	public void loadSavedItems(){
		mSurveyId = prefs.getInt(SURVEY_ID, 1);
		String key; 
		Cursor c;
		int prevDup;
		int prevSect;

		if(! mDb.isOpen()){ //if the DB is not open
			try {
				mDbHelper = new DatabaseHelper(ctxt);
				mDb = mDbHelper.getWritableDatabase();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		c = mDb.query(ITEM_DATA_TABLE, null, SURVEY_ID + " = '" + mSurveyId + "' and "+ SHEET_ID + " = '"+mSheetId+"'", 
					  null, null, null, SECTION_ID+", "+DUPLICATE_ID);
		if(c.getCount() > 0){
			c.moveToFirst();
			prevDup = c.getInt(c.getColumnIndex(DUPLICATE_ID));
			prevSect = c.getInt(c.getColumnIndex(SECTION_ID));
			do {
				ItemData item = new ItemData();
				item.surveyId =  mSurveyId;
				item.sheetId = c.getInt(c.getColumnIndex(SHEET_ID));
				item.sectionId = c.getInt(c.getColumnIndex(SECTION_ID));
				item.duplicateId = c.getInt(c.getColumnIndex(DUPLICATE_ID));
				item.itemId = c.getInt(c.getColumnIndex(ITEM_ID));
				item.takin = c.getInt(c.getColumnIndex(TAKIN_LEVEL)) == 1;
				item.fix1Select = c.getInt(c.getColumnIndex(FIX_1_SELECTION));
				item.fix2Select = c.getInt(c.getColumnIndex(FIX_2_SELECTION));
				item.ItemComment = c.getString(c.getColumnIndex(COMMENT));
				item.measureResult = c.getDouble(c.getColumnIndex(MEASURE_RESULT));
				item.imageLocation = c.getString(c.getColumnIndex(IMAGE_LOCATION));
				item.takin = false;

				//if the item belongs to a duplicate section create the necessary duplicate.
				if(prevSect == item.sectionId && prevDup != item.duplicateId){
					boolean found;
					SectionData data;
					int i;
					found = false;
					for(i = 0; i < this.getGroupCount(); i++){
						data = (SectionData) this.getGroup(i);
						if(! found && data.sheetId == item.sheetId && data.sectionId == item.sectionId){
							found = true;
						}
						else if(found && data.sheetId == item.sheetId &&  data.sectionId != item.sectionId)
							break;
					}
					
					SectionData sect = null;
					try {
						sect = mGroups.get(i-1).cloneMe();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						System.exit(1);
					} 
					sect.duplicateId = item.duplicateId;
					mGroups.add(i, sect);
				}
				key = makeKey(item.sheetId, item.sectionId, item.duplicateId, item.itemId);
				mSavedItems.put(key, item);
				
				prevDup = item.duplicateId;
				prevSect = item.sectionId;

			}while(c.moveToNext());

		}
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
		

		// use trueGid to find the items belonging to a section . for all other purposes use gid 
		int trueGid = (int)getGroupId(gid);

		LayoutInflater inflater = LayoutInflater.from(ctxt);
		key = makeKey(mGroups.get(gid), mItems[trueGid][cid]);

		// if the item is a duplicate identity item.
		if(!  mItems[trueGid][cid].canDuplicate.equals("")){
			child = (View)inflater.inflate(R.layout.identity_item, null);
			AccessTextView identity = (AccessTextView)child.findViewById(R.id.identity_text);
			((TextView)child.findViewById(R.id.identity_label)).setText(" זיהוי " + mGroups.get(gid).canDuplicate);
			
			identity.setIdeces(gid, cid, this);
			//identity.setText("סתם טקסט התחלתי");

			////////////// add action to enable writing to a text view //////////////
			identity.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int X = 0;
					int Y = 1;
					int [] childPos = new int[2];
//					// TODO Auto-generated method stub
//					LayoutInflater inflator = (LayoutInflater)ctxt.getSystemService(ctxt.LAYOUT_INFLATER_SERVICE);
//					View layout = inflator.inflate(R.layout.popup_item, null);
//					
//					PopupWindow popup = new PopupWindow(ctxt);
//					popup.setContentView(layout);
//					popup.setFocusable(true);
//					popup.setWidth(v.getWidth());
//					popup.setHeight(v.getHeight() * 3);
//					v.getLocationInWindow(childPos);
			//		ItemData itemData = getItemObject(key,v.getGid(), myParent.getCid() );
					TextPopupWindow popup = new TextPopupWindow(ctxt);
			//		popup.init((TextView)v, itemData, v.getWidth(), v.getHeight() * 3);
					v.getLocationInWindow(childPos);
					popup.showAtLocation(v, Gravity.TOP, childPos[X] , childPos[Y]);
					
					Log.e("Popup this", this.getClass().getName());
					Log.e("Popup v ", v.getClass().getName());
					Log.e("Popup Button v.parent 1", v.getParent().getClass().getName());
					Log.e("Popup Button v.parent 2", v.getParent().getParent().getClass().getName());
					Log.e("Popup Button v.parent 3", v.getParent().getParent().getParent().getClass().getName());
					Log.e("Popup Button v.parent 4", v.getParent().getParent().getParent().getParent().getClass().getName());
					Log.e("Popup adapter v.parent 0", ((ExpandableListView)(v.getParent().getParent())).getAdapter().getClass().getName());
					Log.e("Popup v. >>>>>> 1", v.getClass().getName());
					
					
				}
			});
			
//			identity.setOnFocusChangeListener(new OnFocusChangeListener() {
//				
//				public void onFocusChange(View v, boolean hasFocus) {
//					Log.e("text field =>", (hasFocus ? "has " : "doesn't have ") +"focus");
//					
//				}
//			});
//			identity.setOnEditorActionListener(new OnEditorActionListener() {
//				
//				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//					// TODO Auto-generated method stub
//					Log.e("text field =>", "Editor Action !");
//					return false;
//				}
//			});
			
		}
		// if the item is a regular item
		else{
			final String shortText =  mItems[trueGid][cid].itemShortText;
			final String longText =  mItems[trueGid][cid].ItemLongText;

			child = (View)inflater.inflate(R.layout.item1, null);

			//child.setmShortTExt(mItems[trueGid][cid].itemShortText);
			((TextView)child.findViewById(R.id.short_text)).setText(shortText);
			ImageButton measureB = (ImageButton)child.findViewById(R.id.measure);
			ImageButton	photoB = (ImageButton)child.findViewById(R.id.photo);
			AccessSpinner fix1Sp = (AccessSpinner)child.findViewById(R.id.fix_1);
			ImageView takin = ((ImageView)child.findViewById(R.id.takin));
			//EditText comment = (EditText)child.findViewById(R.id.comments_field);
			TextView comment = (TextView)child.findViewById(R.id.comments_field);

			measureB.setVisibility((mItems[trueGid][cid].doMeasure) ? View.VISIBLE : View.INVISIBLE);
			photoB.setVisibility((mItems[trueGid][cid].doPhoto) ? View.VISIBLE : View.INVISIBLE);

			//add options to drop-down boxes
			fix1Sp.setIdeces(gid, cid, this);
			Spinner fix2Sp = (Spinner)child.findViewById(R.id.fix_2);

			ArrayAdapter<String> fixAdapter1 = new ArrayAdapter<String>(ctxt, R.layout.drop_down_text, mFix1s[trueGid][cid]);
			ArrayAdapter<String> fixAdapter2 = new ArrayAdapter<String>(ctxt, R.layout.drop_down_text, mFix2s[trueGid][cid]);

			fix1Sp.setAdapter(fixAdapter1);
			fix2Sp.setAdapter(fixAdapter2);

			//set fields according to saved items
			if(mSavedItems.containsKey(key)){
				fix1Sp.setSelection(mSavedItems.get(key).fix1Select);
				if(mSavedItems.get(key).fix1Select != 0){
					takin.setImageBitmap(mTakinArray[TAKIN]);
				}
				else{
					takin.setImageBitmap(mTakinArray[NOT_TAKIN]);					
				}
			}


			///////////// set action for the fix_1 Spinner /////////////
			fix1Sp.setOnItemSelectedListener(new OnItemSelectedListener() {

				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
					// set the takin/not-takin picture
					ImageView takinIv = (ImageView)((RelativeLayout)parent.getParent().getParent()).findViewById(R.id.takin);
					Spinner fix2Sp = (Spinner)((RelativeLayout)parent.getParent().getParent()).findViewById(R.id.fix_2);
					if(pos == 0){ // set pic to takin
						takinIv.setImageBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.takin_button));
						fix2Sp.setEnabled(false);
					}
					else{ // set pic to not takin
						takinIv.setImageBitmap(BitmapFactory.decodeResource(ctxt.getResources(), R.drawable.not_takin_button));
						fix2Sp.setEnabled(true);
					}

					AccessSpinner myParent = (AccessSpinner)parent;
					int trueGid = (int)myParent.getmThis().getGroupId(myParent.getGid());
					//String key = makeKey(mGroups.get(myParent.getGid()), mItems[trueGid][myParent.getCid()] );
					//Log.d("Insert Key", "***** "+key +" *****");
					ItemData itemData = getItemObject(key, myParent.getGid(), myParent.getCid() );
					itemData.fix1Select = myParent.getSelectedItemPosition();
					itemData.takin = itemData.fix1Select != 0; 
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO nothing to do here

				}

			});




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
			
			//add action to the comment field
			comment.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					int X = 0;
					int Y = 1;
					int [] childPos = new int[2];
					// TODO Auto-generated method stub
					LayoutInflater inflator = (LayoutInflater)ctxt.getSystemService(ctxt.LAYOUT_INFLATER_SERVICE);
					View layout = inflator.inflate(R.layout.popup_item, null);
					
					PopupWindow popup = new PopupWindow(ctxt);
					popup.setContentView(layout);
					popup.setFocusable(true);
					popup.setWidth(v.getWidth());
					popup.setHeight(v.getHeight() * 3);
					v.getLocationInWindow(childPos);
					popup.showAtLocation(v, Gravity.TOP, childPos[X] - 150, childPos[Y]);
					
				}
			});
		}
//		child.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				int X = 0;
//				int Y = 1;
//				int [] childPos = new int[2];
//				// TODO Auto-generated method stub
//				LayoutInflater inflator = (LayoutInflater)ctxt.getSystemService(ctxt.LAYOUT_INFLATER_SERVICE);
//				View layout = inflator.inflate(R.layout.popup_item, null);
//				
//				PopupWindow popup = new PopupWindow(ctxt);
//				popup.setContentView(layout);
//				popup.setFocusable(true);
//				popup.setWidth(child.getWidth());
//				popup.setHeight(child.getHeight() * 2);
//				child.getLocationOnScreen(childPos);
//				popup.showAtLocation(v, Gravity.TOP, childPos[X], childPos[Y]);
//				
//			}
//		});
		
		return  child;
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
		//return gid;
		return mGroups.get(gid).sectionId;
	}

	public View getGroupView(int gid, boolean isExpanded, View convertView, ViewGroup parent) {

		TextView textView = getGenericView();
		SectionData group = (SectionData)getGroup(gid);
		if(! group.canDuplicate.equals("")){
			textView.setText(group.toString() + "  #"+group.duplicateId);
		}
		else
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

	// Yes No Questions
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}


	/////////// store changes in data ////////////
	//store all the keys that identify a single item.
	private ItemData getItemObject(String key,int gid, int cid){
		ItemData itemData;
		SectionData section;
		Item item;
		int surveyId = prefs.getInt(SURVEY_ID, 1);
		//String key = makeKey(section.sheetId, section.sectionId, section.duplicateId, item.itemId,  surveyId);
		// if the key exists update it
		if (mSavedItems.containsKey(key)){
			itemData = mSavedItems.get(key);
			itemData.hasChanged = true;
			//mSavedItems.remove(key);
		}
		// if the key doesn't exist create new
		else{
			section = mGroups.get(gid);
			item = mItems[gid][cid];
			itemData = new ItemData();
			itemData.surveyId = surveyId;
			itemData.sheetId = section.sheetId;
			itemData.sectionId = section.sectionId;
			itemData.SectionDuplicateId = section.duplicateId;
			itemData.itemId = item.itemId;
			itemData.hasChanged = true;
			mSavedItems.put(key, itemData);
		}

		return itemData;

	}

	// make key for itemDdata 
	private String makeKey(SectionData section, Item item){
		return makeKey(section.sheetId, section.sectionId, section.duplicateId, item.itemId);
	}


	private String makeKey(int sheet, int section, int duplicate, int item){
		StringBuilder key = new StringBuilder();
		key.append(sheet);
		key.append("X");
		key.append(section);
		key.append("X");
		key.append(duplicate);
		key.append("X");
		key.append(item);
		return key.toString();

	}
	//find if item already exiItst
	//
	//	public Map<String, ItemData> getSavedItems(){
	//		return mSavedItems;
	//	}

}
