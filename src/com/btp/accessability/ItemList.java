package com.btp.accessability;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
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
import com.btp.accessability.data.SectionData;


public class ItemList extends Activity implements DBConstants{
	
	final int FIRST_ID = 10000;
	ListItemAdapter mItemAdapter ;
	ExpandableListView mList;
	LinearLayout mDuplicateBar;
	DatabaseHelper dbHelper;
	SQLiteDatabase db = null;
	Map<Integer, int[]> mDupButtonData = new HashMap<Integer, int[]>();
	Context mCtxt = this;
	
	Item[] itemContent =  {};
	Cursor c;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setContentView(R.layout.kayam_general);
		mList = (ExpandableListView)this.findViewById(R.id.main_list);
		mDuplicateBar = (LinearLayout) this.findViewById(R.id.duplicate_bar);
		
		//int sheetId = Integer.valueOf(getIntent().getCharArrayExtra("com.btp.accessability.sheetId").toString());
		int sheetId = getIntent().getIntExtra("com.btp.accessability.sheetId", -1);
		int sectionId;
		String canDup;
		
		// get the list adapter and attach it to mlist.
		mItemAdapter = new ListItemAdapter(this, sheetId);
		mList.setAdapter(mItemAdapter);

		
		//open db
		try {
			dbHelper = new DatabaseHelper(this);
			db = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}


		//set the first button Id
		int bId  = FIRST_ID;
		//find all the duplicate buttons you need
		c = db.query(FORM_SECTION_TABLE, null, SHEET_ID+" = '"+sheetId+"' and "+CAN_DUPLICATE+" != ''" , null, null, null, null);
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
					SectionData newSect = new SectionData();
					newSect.canDuplicate = data.canDuplicate;
					newSect.duplicateId = dupId;
					newSect.sheetId = sheetId;
					newSect.sectionId = sectId;
					newSect.sectionTitle = data.sectionTitle;
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
		db.close();
		// get the list adapter and attach it to mlist.
		mList.setAdapter(new ListItemAdapter(this, sheetId));
		super.onCreate(savedInstanceState);
	}
//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		super.onPause();
//	}
//	@Override
//	protected void onRestart() {
//		// TODO Auto-generated method stub
//		super.onRestart();
//	}
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//	}
	
	
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
