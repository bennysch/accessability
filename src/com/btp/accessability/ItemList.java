package com.btp.accessability;
import com.btp.accessability.data.Item;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;


public class ItemList extends ExpandableListActivity {
	
	ListItemAdapter itemAdapter ;
	Item[] itemContent =  {};
	
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		String sheetId = new String(getIntent().getCharArrayExtra("com.btp.accessability.sheetId"));
		
		itemAdapter = new ListItemAdapter(this, sheetId);
		
		setListAdapter(itemAdapter);
		
		ExpandableListView elv = getExpandableListView();
		//openGroups(elv);

		
	}


	public void openGroups(ExpandableListView lv){

		for(int i = 0; i < itemAdapter.getGroupCount();i++){
			lv.expandGroup(i);
		}
	}
	

}
