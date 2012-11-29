package com.btp.accessability;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.Item;
import com.btp.accessability.data.ItemData;
import com.btp.accessability.data.SectionData;


public class ItemList extends Activity implements DBConstants{
	
	final int FIRST_ID = 10000;
	ListItemAdapter mItemAdapter ;
	ExpandableListView mList;
	LinearLayout mDuplicateBar;
	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;
	SharedPreferences mPrefs;
	Map<Integer, int[]> mDupButtonData = new HashMap<Integer, int[]>();
	Context mCtxt = this;
	Map<String, ItemData> mSavedItems;
	
	Item[] itemContent =  {};
	Cursor c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.kayam_general);
		mList = (ExpandableListView)this.findViewById(R.id.main_list);
		mDuplicateBar = (LinearLayout) this.findViewById(R.id.duplicate_bar);
		mPrefs = this.getSharedPreferences("com.btp.accessability", Activity.MODE_APPEND | Activity.MODE_WORLD_READABLE);
		Map<String, ?> m = mPrefs.getAll();
		int sid = mPrefs.getInt(SURVEY_ID, 0);
		
		//int sheetId = Integer.valueOf(getIntent().getCharArrayExtra("com.btp.accessability.sheetId").toString());
		int sheetId = getIntent().getIntExtra("com.btp.accessability.sheetId", -1);
		int sectionId;
		String canDup;
		
		// get the list adapter and attach it to mlist.
		
		mSavedItems = new HashMap<String, ItemData>();
//		mItemAdapter = new ListItemAdapter(this, sheetId);
		mItemAdapter = new ListItemAdapter(this, sheetId, mSavedItems);
		mList.setAdapter(mItemAdapter);

		
		//open db
		try {
			mDbHelper = new DatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		//set the first button Id
		int bId  = FIRST_ID;
		//find all the duplicate buttons you need
		c = mDb.query(FORM_SECTION_TABLE, null, SHEET_ID+" = '"+sheetId+"' and "+CAN_DUPLICATE+" != ''" , null, null, null, null);
		c.moveToFirst();
		Button dupButton;
		do{
		//create the buttons  and add them to the bar.
			sectionId = c.getInt(c.getColumnIndex(SECTION_ID));
			canDup = c.getString(c.getColumnIndex(CAN_DUPLICATE));
			dupButton = new Button(this);
			//TODO add the sheetId and sectionId to the button
			dupButton.setText(this.getString(R.string.duplicate) +" "+ canDup );
			dupButton.setPadding(4, 4, 4, 4);
			dupButton.setId(bId++);
			mDupButtonData.put(dupButton.getId(), new int[] {sheetId, sectionId});
			
			//set the button's On Click Listener
			dupButton.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO 
					int i;
					SectionData data;
					boolean found;
					int dupId = 0;
					//find the section to duplicate in the list of groups
					//List<SectionData> groups = mItemAdapter.mGroups;
					int buttonId = v.getId();
					int sheetId = mDupButtonData.get(buttonId)[0];
					int sectId = mDupButtonData.get(buttonId)[1];
					
					found = false;
					for(i = 0; i < mItemAdapter.getGroupCount(); i++){
						data = (SectionData) mItemAdapter.getGroup(i);
						if(! found && data.sheetId == sheetId && data.sectionId == sectId){
							found = true;
						}
						else if(found && data.sheetId == sheetId &&  data.sectionId != sectId)
							break;
					}

					data = (SectionData) mItemAdapter.getGroup(i - 1);
					dupId = data.duplicateId + 1;
					//create a mew section
					SectionData newSect = null;
					try {
						newSect = data.cloneMe();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						System.exit(1);
					}
					newSect.duplicateId++;
					mItemAdapter.addGroup(i, newSect);
					mItemAdapter.notifyDataSetChanged();
					mList.setAdapter(mItemAdapter);
						
					//for all items in section
					//create new item
					
				}
			}); 
			mDuplicateBar.addView(dupButton);

		}while(c.moveToNext());
		//close db
		mDb.close();
		// get the list adapter and attach it to mlist.
		mList.setAdapter(mItemAdapter);
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onDestroy() {
		storeInDB();
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		storeInDB();
		super.onPause();
	}
	@Override
	protected void onRestart() {
		mItemAdapter.loadSavedItems();
		super.onRestart();
	}
	@Override
	protected void onResume() {
		//mItemAdapter.loadSavedItems();
		super.onResume();
	}

	private void storeInDB(){
		//open DB
		try {
			mDbHelper = new DatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ContentValues values = new ContentValues();

		//get survey ID
		//String surveyId = prefs.getString(SURVEY_ID, "");
		int surveyId = mPrefs.getInt(SURVEY_ID, 0);
		//get saved Items.
		//Map<String, ItemData> savedItems = mItemAdapter.getSavedItems();
		//for each saved item that has changed 
		//for(ItemData item : savedItems.values()){
		for(ItemData item : mSavedItems.values()){
		//for(ItemData item : mItemAdapter.mSavedItems.values()){
		  //TODO sore in Db using insert-or-update command
			values.clear();
			values.put(SURVEY_ID, surveyId);
			values.put(SHEET_ID, item.sheetId);
			values.put(SECTION_ID, item.sectionId);
			values.put(DUPLICATE_ID, item.SectionDuplicateId);
			values.put(ITEM_ID, item.itemId);
			values.put(TAKIN_LEVEL, item.takin ? "1" : "0");
			values.put(FIX_1_SELECTION, item.fix1Select);
			values.put(FIX_2_SELECTION, item.fix2Select);
			values.put(COMMENT, item.ItemComment);
			values.put(MEASURE_RESULT, item.measureResult);
			values.put(IMAGE_LOCATION, item.imageLocation);
			if(item.hasChanged){
				mDb.insertWithOnConflict(ITEM_DATA_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
				item.hasChanged = false;
			}
			else
				mDb.insertWithOnConflict(ITEM_DATA_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
		//
		//close DB
	}
	
	
	
	
//	public void onCreate(Bundle bundle){
//		super.onCreate(bundle);
//		String sheetId = new String(getIntent().getCharArrayExtra("com.btp.accessability.sheetId"));
//		
//		itemAdapter = new ListItemAdapter(this, sheetId);
//		
//		setListAdapter(itemAdapter);
//		
//		ExpandableListView elv = getExpandableListView();
//
//		
//	}
//
//
//	

}
