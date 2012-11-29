package com.btp.accessability;

import com.btp.accessability.data.DBConstants;
import com.btp.accessability.data.DatabaseHelper;
import com.btp.accessability.form.UploadActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class AccessabilityActivity extends Activity implements DBConstants{

	public static final int UPLOAD_ID = Menu.NONE;


	ImageButton hadash;
	ImageButton kayam;
	Context ctxt;

	DatabaseHelper mDbHelper;
	SQLiteDatabase mDb = null;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		String[] surveys;
		int count;
		int id;
		int i;
		String name;
		Spinner sp;
		final SharedPreferences prefs = this.getSharedPreferences("com.btp.accessability", this.MODE_APPEND | this.MODE_WORLD_READABLE);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ctxt = this;

		// open DB
		dbOpen();

		//create the data tables (only if the don't already exist)
		//**/String s = CREATE_OWNER;
		mDb.execSQL(CREATE_OWNER);
		mDb.execSQL(CREATE_SURVEY);
		mDb.execSQL(CREATE_DATA);

		// read all survay_id's & building names
		//        Cursor c = mDb.query(SURVEYOR_TABLE, new String[] {SURVEY_ID, BUILDING_NAME}, "", null, null, null, null);
		Cursor c = mDb.query(SURVEY_TABLE, null, null, null, null, null, null);
		count = c.getCount();
		surveys = new String[count + 1];  // + 1 to leave room for the "new survey" entry at the end of the list
		i = 0;
		if(c.moveToFirst()){
			do {
				id = c.getInt(c.getColumnIndex(SURVEY_ID));
				name = c.getString(c.getColumnIndex(BUILDING_NAME));
				surveys[i++] = id + " - " + name;
			}while (c.moveToNext());
		}
		// Add item "סקר חדש" at end of list
		surveys[i] = getString(R.string.new_survey);
		// close DB
		mDb.close();
		// create SPinner adaptor.
		ArrayAdapter<String> surveyAdaptor = new ArrayAdapter<String>(ctxt, R.layout.drop_down_text, surveys);
		// add survey adaptor to survey Spinner
		sp = (Spinner)findViewById(R.id.survey_id_S);
		sp.setAdapter(surveyAdaptor);
		// add listener to spinner @+id/survey_id_S
		sp.setOnItemSelectedListener(new OnItemSelectedListener() {

			@TargetApi(8)
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// TODO replace with real code

				if (((String)parent.getAdapter().getItem(pos)).equals("סקר חדש")){
					dbOpen();
					ContentValues values = new ContentValues();
					values.put(SURVEY_ID, 1);
					values.put(BUILDING_NAME, "סקר דמה");
					values.put(SURVEY_START_DATE, "1/1/1111");
					
					mDb.insertWithOnConflict(SURVEY_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
					mDb.close();
					SharedPreferences.Editor prefEdit = prefs.edit();
					prefEdit.putInt(SURVEY_ID, 1);
					prefEdit.commit();
					
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing

			}

		});
		// where on clicking on the "סקר חדש" option creates a new survey record in the DB
		// later replace that with opening the popup





		kayam = (ImageButton)findViewById(R.id.kayamB);
		kayam.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(ctxt,  KayamMain.class));
			}
		});

		hadash = (ImageButton)findViewById(R.id.hadashB);
		hadash.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				new AlertDialog.Builder(ctxt)
				.setTitle(ctxt.getString(R.string.not_yet_title))
				.setMessage(ctxt.getString(R.string.not_yet))
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
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		menu.add(0, Menu.NONE, 0, R.string.upload);

		MenuItem mS = (MenuItem)menu.getItem(UPLOAD_ID);
		mS.setIcon(R.drawable.upload);

		return result;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case UPLOAD_ID:

			startActivityForResult(new Intent(this, UploadActivity.class), 1);
			return true;
		}
		return super.onOptionsItemSelected(item);
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