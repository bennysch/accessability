package com.btp.accessability;

import com.btp.accessability.form.UploadActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

public class AccessabilityActivity extends Activity {
	
	public static final int UPLOAD_ID = Menu.NONE;


	ImageButton hadash;
	ImageButton kayam;
	Context ctxt;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		ctxt = this;

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

}