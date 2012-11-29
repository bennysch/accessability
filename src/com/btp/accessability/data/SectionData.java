package com.btp.accessability.data;

public class SectionData implements Cloneable{

	public int sheetId;
	public int sectionId;
	public int duplicateId;
	public String sectionTitle;
	//public boolean canDuplicate;
	public String canDuplicate;
	


	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		SectionData sect = (SectionData)super.clone();
		sect.sheetId = this.sheetId;
		sect.sectionId = this.sectionId;
		sect.duplicateId = this.duplicateId;
		sect.sectionTitle = new String(this.sectionTitle);
		sect.canDuplicate = new String(this.canDuplicate);
		return sect;
	}

	public SectionData cloneMe() throws CloneNotSupportedException {
		return (SectionData)this.clone();
	}


	@Override
	public String toString() {
		return sectionTitle;
	}

	
	
}
