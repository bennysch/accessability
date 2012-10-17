package com.btp.accessability;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.SheetData;

public class KayamMain extends Activity implements DBConstants{

	Context ctxt;
	ImageButton instruction;
	ImageButton general;
	ImageButton storage;
	ImageButton shower;
	ImageButton parking;
	ImageButton groups;

	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;

	public void onCreate(Bundle bundle){
        super.onCreate(bundle);
	}

	@Override
	protected void onStart() {
		super.onStart();
        setContentView(R.layout.kayam_main);
        ctxt = this;
          ///////////////////////////////////////
         // replace with a more dynamic build //
        ///////////////////////////////////////
        
        instruction = (ImageButton)findViewById(R.id.instructions);
        general = (ImageButton)findViewById(R.id.kayam_claly);
        
        

        //instruction button
        instruction.setOnClickListener(new View.OnClickListener() {
        	Intent intent;
        	SheetData data;
//        	String sheetId = new String("0"); 
//        	String title = new String("הנחיות למלוי הטפסים");

			public void onClick(View v) {
	        	data = getSheetId("הנחיות");
	        	intent = new Intent(ctxt,  InstructionList.class);
	        	intent.putExtra("com.btp.accessability.sheetTitle", data.sheetTitle.toCharArray());
	        	intent.putExtra("com.btp.accessability.sheetId", data.sheetId.toCharArray());
				startActivity(intent);
			}
		});
        
        
        //general building data button
        general.setOnClickListener(new View.OnClickListener() {
        	Intent intent;
        	SheetData data;
//        	String sheetId = new String("1"); 
//        	String title = new String("טופס - בנין קיים כללי");
       	
			public void onClick(View v) {
	        	data = getSheetId("כללי");
	        	intent = new Intent(ctxt,  ItemList.class);
	        	intent.putExtra("com.btp.accessability.sheetTitle", data.sheetTitle.toCharArray());
	        	intent.putExtra("com.btp.accessability.sheetId", data.sheetId.toCharArray());
				startActivity(intent);
			}
		});

//        storage.setOnClickListener(new View.OnClickListener() {
//        	
//			public void onClick(View v) {
//				startActivity(new Intent(ctxt,  KayamMain.class));
//			}
//		});
//
//        shower.setOnClickListener(new View.OnClickListener() {
//        	
//			public void onClick(View v) {
//				startActivity(new Intent(ctxt,  KayamMain.class));
//			}
//		});
//
//        parking.setOnClickListener(new View.OnClickListener() {
//        	
//			public void onClick(View v) {
//				startActivity(new Intent(ctxt,  KayamMain.class));
//			}
//		});
//
//        groups.setOnClickListener(new View.OnClickListener() {
//        	
//			public void onClick(View v) {
//				startActivity(new Intent(ctxt,  KayamMain.class));
//			}
//		});
//

	}

	private SheetData getSheetId(String fragment) {
		//String[] args = {fragment};
		String[] args = new String[1];
		String[] columns = {SHEET_ID,SHEET_NAME};
		SheetData res = new SheetData();
		Cursor cursor;
		
		args[0] = new String(fragment);
		dbOpen();
		//find sheet by string frasgment
//		/*debug*/		Cursor c = mDb.query(FORM_SHEET_TABLE, null, null, null, null, null, null);
//		/*debug*/		Log.i(FORM_SHEET_TABLE , "count = "+c.getCount());
//		/*debug*/		Log.i("columns", c.getColumnName(0)+" <> "+ c.getColumnName(1));
//		/*debug*/		c.moveToFirst();
//		/*debug*/		do {
//		/*debug*/			Log.i(FORM_SHEET_TABLE, c.getString(0) +" <> "+ c.getString(1));
//		/*debug*/		} while(c.moveToNext());

		//cursor = mDb.query(FORM_SHEET_TABLE, columns, SHEET_NAME+" LIKE '%?%' ", args, null, null, null);		
		//Cursor cc = mDb.query(FORM_SHEET_TABLE, null, null, null, null, null, null);		//fill sheet structure
		cursor = mDb.query(FORM_SHEET_TABLE, columns, SHEET_NAME+" LIKE '%"+fragment+"%' ", null, null, null, null);		//fill sheet structure
		cursor.moveToFirst();
//		/*debug*/			Log.i(FORM_SHEET_TABLE, cursor.getString(0) +" <> "+ cursor.getString(1));
		res.sheetId = cursor.getString(cursor.getColumnIndex(SHEET_ID));
		res.sheetTitle = cursor.getString(cursor.getColumnIndex(SHEET_NAME));
		//return data;
		
		mDb.close();
		
		return res;
	}
	
	private void dbOpen(){
		try {
			mDbHelper = new DatabaseHelper(this);
			mDb = mDbHelper.getReadableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
