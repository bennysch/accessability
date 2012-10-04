package com.btp.accessability;
import android.app.ExpandableListActivity;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import com.btp.accessability.data.Item;


public class InstructionList extends ExpandableListActivity {
	
	InstructionsAdapter instructionAdapter ;
	Item[] itemContent =  {};
	
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		
		instructionAdapter = new InstructionsAdapter(this);
		
		setListAdapter(instructionAdapter);
		
		ExpandableListView elv = getExpandableListView();
		openGroups(elv);

		
	}


	public void openGroups(ExpandableListView lv){

		for(int i = 0; i < instructionAdapter.getGroupCount();i++){
			lv.expandGroup(i);
		}
	}
	

}
