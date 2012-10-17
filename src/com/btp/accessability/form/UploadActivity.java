package com.btp.accessability.form;

import java.io.File;
import java.io.IOException;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.btp.accessability.R;
import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.data.FormInstructions;

public class UploadActivity extends Activity implements DBConstants {

	// excell column names
	final int INST_ID_COL = 1;
	final int INST_TEXT_COL = 2;
	final int FORM_LINE_NUM = 0;
	final int FORM_SECTION = 1;
	final int FORM_SHORT_TEXT = 2;
	final int FORM_LONG_TEXT = 3;
//	final int FORM_FIX_1 = 4;
//	final int FORM_FIX_2 = 5;
	final int FORM_MEASURE = 6;
	final int FORM_CAM = 7;
	final int FORM_CAN_DUPLICATE = 8;

	final int FIRST_DATA_ROW = 1;



	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Open DB
		try {
			mDbHelper = new DatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/**/Log.i("SOL" , CREATE_INSTRUCTIONS);
		/**/Log.i("SOL" , CREATE_SHEETS);
		/**/Log.i("SOL" , CREATE_SECTIONS);
		/**/Log.i("SOL" , CREATE_ITEMS);
		/**/Log.i("SOL" , CREATE_FIXES);
		/**/Log.i("SOL" , CREATE_OWNER);
		/**/Log.i("SOL" , CREATE_SURVEY);
		/**/Log.i("SOL" , CREATE_DATA);
		
		//remove old tables if  exist (form only) 

		mDb.execSQL("drop table if exists "+FORM_INSTRUCTIONS_TABLE+";");
		mDb.execSQL("drop table if exists "+FORM_SHEET_TABLE+";");
		mDb.execSQL("drop table if exists "+FORM_SECTION_TABLE+";");
		mDb.execSQL("drop table if exists "+FORM_ITEM_TABLE+";");
		mDb.execSQL("drop table if exists "+FORM_FIX_TABLE+";");
		//create new tables (form only)
		mDb.execSQL(CREATE_INSTRUCTIONS);
		mDb.execSQL(CREATE_SHEETS);
		mDb.execSQL(CREATE_SECTIONS);
		mDb.execSQL(CREATE_ITEMS);
		mDb.execSQL(CREATE_FIXES);
		
		//create the data tables (only if the don't already exist)
		/**/String s = CREATE_OWNER;
		mDb.execSQL(CREATE_OWNER);
		mDb.execSQL(CREATE_SURVEY);
		mDb.execSQL(CREATE_DATA);
		//close DB
		mDb.close();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO 
		int row;
		int numValues;
		//Cell[] cells;
		ContentValues values;
		FormInstructions fInstruct;
		Workbook workbook = null;
		int sheetId = 0;
		int sectionId = 0;
		int itemId = 0;
		int fixId = 0;

		super.onStart();
		//open the .xls file and create a workbook
		File sdDir = Environment.getExternalStorageDirectory();
		String sdPath = sdDir.getAbsolutePath();
		try {
			File f = new File(sdPath+"/loadForm.xls");			
			workbook = Workbook.getWorkbook(f);
		} catch (BiffException e) {
			// TODO Auto-generated catch bloc
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Open DB
		try {
			mDbHelper = new DatabaseHelper(this);
			mDb = mDbHelper.getWritableDatabase();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//for each sheet
		for(Sheet sheet : workbook.getSheets()){
			//open sheet
			numValues = 2;
			String s;
			values = new ContentValues();
			//if sheet = instruction sheet
			//write down sheet info and assign sheet id to the rest of the information
			values.clear();
			values.put(SHEET_ID, String.valueOf(++sheetId));
			values.put(SHEET_NAME, sheet.getName());
			// write instruction to DB
//			if(mDb.insert(FORM_SHEET_TABLE, null, values) == -1){
//			}

			if(mDb.insert(FORM_SHEET_TABLE, null, values) != -1){
				//if the sheet is that of operation instructions.
				if(sheet.getName().indexOf(getString(R.string.instructionTitle)) != -1){
					// get instructions
					for(row = FIRST_DATA_ROW; row < sheet.getRows() ; row++){
						if(! sheet.getCell(INST_TEXT_COL, row).getContents().equals("")){//not empty line
							values.clear();
							values.put(INST_ID, ((s = sheet.getCell(INST_ID_COL, row).getContents()).equals("")) ? "-" : s);
							values.put(INST_DATA, sheet.getCell(INST_TEXT_COL, row).getContents());
							// write instruction to DB

							if(mDb.insert(FORM_INSTRUCTIONS_TABLE, null, values) == -1){

								//TODO set error code

							}
						}
					}
				}
				//if the sheet is of "real" questions
				else {
					//for each row
					for (row = 0; row < sheet.getRows(); row++){
						//if the row is the header row  (there is somthing in both the section and short text columns) - ignore it
						if((!sheet.getCell(FORM_SECTION, row).getContents().equals("")) &&
						   (!sheet.getCell(FORM_SHORT_TEXT, row).getContents().equals(""))	){
							// do nothing
						}
						//else if row is section  (there is something in the section column) - insert section
						else if(!sheet.getCell(FORM_SECTION, row).getContents().equals("")){
							values.clear();
							values.put(SHEET_ID, String.valueOf(sheetId));
							values.put(SECTION_ID, String.valueOf(++sectionId));
							values.put(DUPLICATE_ID, "0");
							values.put(SECTION_TITLE, sheet.getCell(FORM_SECTION, row).getContents());
							values.put(CAN_DUPLICATE, sheet.getCell(FORM_CAN_DUPLICATE, row).getContents());

							if(mDb.insert(FORM_SECTION_TABLE, null, values) == -1){
							}

						}
						//else if row is item  (there is something in the short text column) - insert item
						else if (!sheet.getCell(FORM_SHORT_TEXT, row).getContents().equals("")){
							values.clear();
							values.put(SHEET_ID, String.valueOf(sheetId));
							values.put(SECTION_ID, String.valueOf(sectionId));
							values.put(ITEM_ID, String.valueOf(++itemId));
							values.put(DUPLICATE_ID, "0");
							values.put(SHORT_TEXT, sheet.getCell(FORM_SHORT_TEXT, row).getContents());
							values.put(LONG_TEXT, sheet.getCell(FORM_LONG_TEXT, row).getContents());
							values.put(DO_MEASURE, sheet.getCell(FORM_MEASURE, row).getContents());
							values.put(DO_PHOTO, sheet.getCell(FORM_CAM, row).getContents());
							values.put(CAN_DUPLICATE, sheet.getCell(FORM_CAN_DUPLICATE, row).getContents().equals("") ? 0 : 1);

							if(mDb.insert(FORM_ITEM_TABLE, null, values) == -1){
							}
							fixId = insertFix(sheet, row, itemId, fixId);
						}
						//else if row is fixes
						else if((!sheet.getCell(FORM_FIX_1, row).getContents().equals("")) || (!sheet.getCell(FORM_FIX_2, row).getContents().equals(""))){
							fixId = insertFix(sheet, row, itemId, fixId);
						}

					}

				}
			}
		}
///*debug*/		Cursor c = mDb.query(FORM_SHEET_TABLE, null, null, null, null, null, null);
///*debug*/		
///*debug*/		c = mDb.query(FORM_INSTRUCTIONS_TABLE, null, null, null, null, null, null);
///*debug*/		c = mDb.query(FORM_SECTION_TABLE, null, null, null, null, null, null);
///*debug*/		c.moveToFirst();
///*debug*/		String table = FORM_SECTION_TABLE;
///*debug*/		Log.i(table , "count = "+c.getCount());
///*debug*/		Log.i("columns", c.getColumnName(0)+" <> "+ c.getColumnName(1)+" <> "+ c.getColumnName(2));
///*debug*/		do {
///*debug*/			Log.i(table, c.getString(0) +" <> "+ c.getString(1)+" <> "+ c.getString(2));
///*debug*/		} while(c.moveToNext());
///*debug*/		
///*debug*/		c = mDb.query(FORM_ITEM_TABLE, null, null, null, null, null, null);
///*debug*/		c.moveToFirst();
///*debug*/		table = FORM_ITEM_TABLE;
///*debug*/		Log.i(table , "count = "+c.getCount());
///*debug*/		Log.i("columns", c.getColumnName(0)+" <> "+ c.getColumnName(1)+" <> "+ c.getColumnName(2)+" <> "+ c.getColumnName(3)+
///*debug*/				" <> "+ c.getColumnName(4)+" <> "+ c.getColumnName(5)+" <> "+ c.getColumnName(6));
///*debug*/		do {
///*debug*/			Log.i(table, c.getString(0) +" <> "+ c.getString(1) +" <> "+ c.getString(2) +" <> "+ c.getString(3) +" <> "+
///*debug*/						 c.getString(4)+" <> "+ c.getString(5)+" <> "+ c.getString(6));
///*debug*/		} while(c.moveToNext());
///*debug*/				
///*debug*/		c = mDb.query(FORM_FIX_TABLE, null, null, null, null, null, null);
///*debug*/		c.moveToFirst();
///*debug*/		 table = FORM_FIX_TABLE;
///*debug*/		Log.i(table , "count = "+c.getCount());
///*debug*/		Log.i("columns", c.getColumnName(0)+" <> "+ c.getColumnName(1)+" <> "+ c.getColumnName(2)+" <> "+ c.getColumnName(3));
///*debug*/		do {
///*debug*/			Log.i(table, c.getString(0) +" <> "+ c.getString(1) +" <> "+ c.getString(2) +" <> "+ c.getString(3));
///*debug*/		} while(c.moveToNext());
		
		//closeD DB
		mDb.close();
		finish();
	}

	private int insertFix (Sheet sheet, int row, int itemId, int fixId){
		ContentValues values = new ContentValues();
		String fix;
		
		if(!(fix = sheet.getCell(FORM_FIX_1, row).getContents()).equals("")){
			values.clear();
			values.put(ITEM_ID, itemId);
			values.put(FIX_ID, ++fixId);
			values.put(FIX_LEVEL, FORM_FIX_1);
			values.put(FIX_TEXT, fix);
			if(mDb.insert(FORM_FIX_TABLE, null, values) == -1){
			}
		}
		
		if(!(fix = sheet.getCell(FORM_FIX_2, row).getContents()).equals("")){
			values.clear();
			values.put(ITEM_ID, itemId);
			values.put(FIX_ID, ++fixId);
			values.put(FIX_LEVEL, FORM_FIX_2);
			values.put(FIX_TEXT, fix);
			if(mDb.insert(FORM_FIX_TABLE, null, values) == -1){
			}
		}

		return fixId;
	}

	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}



}
