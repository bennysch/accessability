package com.btp.accessability.data;

/**
 *  Hold the form instructions read from the DB
 * @author Benny
 *
 */
public class FormInstructions {
	
	
	private String mId;
	private String mText;
	
	
	public FormInstructions(String mId, String mText) {
		super();
		this.mId = mId;
		this.mText = mText;
	}
	
	public String getmId() {
		return mId;
	}
	public void setmId(String mId) {
		this.mId = mId;
	}
	public String getmText() {
		return mText;
	}
	public void setmText(String mText) {
		this.mText = mText;
	}
}
