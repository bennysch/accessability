package com.btp.accessability.data;

public interface DBConstants {

	//instruction fields
	public static final String DATABASE_NAME = "accessability_data";
	public static final String FORM_INSTRUCTIONS_TABLE = "form_instructions";
	public static final String INST_ID = "id";
	public static final String INST_DATA = "text";
	public static final String FORM_DATA_TABLE = "form_data";
	public static final int DATABASE_VERSION = 2;
	
	//sheet record - table name and field names
	public static final String FORM_SHEET_TABLE = "form_sheet";
	public static final String SHEET_ID = "s_id";
	public static final String SHEET_NAME = "s_name";
	
	//section record - table name and field names
	public static final String FORM_SECTION_TABLE = "form_section";
	public static final String SECTION_ID = "sec_id";
	public static final String DUPLICATE_ID = "duplicate_id";
	public static final String SECTION_TITLE = "sec_title";
	public static final String CAN_DUPLICATE = "can_duplicate";
	
	//item record - table name and field  names
	public static final String FORM_ITEM_TABLE = "form_item";
	public static final String ITEM_ID = "i_id";
	public static final String SHORT_TEXT = "short_text";
	public static final String LONG_TEXT = "long_text";
	public static final String DO_MEASURE = "measure";
	public static final String DO_PHOTO = "photo";
	
	//fix options record - table name and field  names
	public static final String FORM_FIX_TABLE = "form_fix";
	public static final String FIX_ID = "fix_id";
	public static final String FIX_LEVEL = "fix_level";
	public static final String FIX_TEXT = "fix_text";
	public static final int FORM_FIX_1 = 4;
	public static final int FORM_FIX_2 = 5;
		
	//Owner record - table name and field names.
	public static final String OWNER_TABLE = "owner_data";
	public static final String OWNER_ID = "owner_id";
	public static final String OWNER_NAME = "owner_name";
	public static final String OWNER_ADDRESS = "owner_address";
	public static final String OWNER_PHONE = "owner_phone";
	public static final String OWNER_EMAIL = "owner_email";
	
	//Surveyor record - table name and field names.
	public static final String SURVEYOR_TABLE = "surveyor_data";
	public static final String SURVEYOR_ID = "surveyor_id";
	public static final String SURVEYOR_NAME = "surveyor_name";
	public static final String SURVEYOR_ADDRESS = "surveyor_address";
	public static final String SURVEYOR_PHONE = "surveyor_phone";
	public static final String SURVEYOR_EMAIL = "surveyor_email";

	//survey record - table name and field names.
	public static final String SURVEY_TABLE = "survey_data";
	public static final String SURVEY_ID = "survey_id";
	public static final String SURVEY_START_DATE = "survey_start";
	public static final String SURVEY_END_DATE = "survey_end";
	
	//building record
	public static final String BUILDING_STREET = "building_Street";
	public static final String BUILDING_NUM     = "building_number";
	public static final String BUILDING_NAME   = "building_name";
	public static final String BUILDING_TOWN   =  "building_town";
	public static final String BUILDING_GUSH   = "building_gush";
	public static final String BUILDING_HELKA  = "building_jelka";
	public static final String BUILDING_SUB_HELKA = "building_sub_helka";
	
	
	
	//data record - table name and field names.
	public static final String ITEM_DATA_TABLE = "item_data";
	public static final String TAKIN_LEVEL = "takin_level";
	public static final String FIX_1_SELECTION = "fix_1_select";
	public static final String FIX_2_SELECTION = "fix_2_select";
	public static final String COMMENT = "comment";
	public static final String IMAGE_LOCATION = "image_location";
	public static final String MEASURE_RESULT = "measure_result";
	
	// structure statements
	
	// form tables create after removing the old ones. 
	public static final String CREATE_INSTRUCTIONS = "CREATE TABLE "+FORM_INSTRUCTIONS_TABLE+
																   "('"+INST_ID+"' VARCHAR, '"+
																   INST_DATA+"' VARCHAR, "+
																   "PRIMARY KEY ('"+INST_ID+"'));";
 
	public static final String CREATE_SHEETS = "CREATE TABLE "+FORM_SHEET_TABLE+
															 "('"+SHEET_ID+"' INTEGER, '"+
															 SHEET_NAME+"' VARCHAR, "+
															 "PRIMARY KEY ('"+SHEET_ID+"'));";
 
	public static final String CREATE_SECTIONS = "CREATE TABLE "+FORM_SECTION_TABLE+
															   "('"+SECTION_ID+"' INTEGER, '"+
															   SHEET_ID+"' INTEGER, '"+
															   SECTION_TITLE+"' VARCHAR, '"+
															   DUPLICATE_ID+"' INTEGER, '"+
															   CAN_DUPLICATE+"' VARCHAR, "+
															   "PRIMARY KEY ('"+SHEET_ID+"','"+SECTION_ID+"'));";
 
	public static final String CREATE_ITEMS = "CREATE TABLE "+FORM_ITEM_TABLE+
															"('"+ITEM_ID+"' INTEGER, '"+
															SECTION_ID+"' INTEGER, '"+
															SHEET_ID+"' INTEGER, '"+
															SHORT_TEXT+"' VARCHAR, '"+
															LONG_TEXT+"' VARCHAR, '"+
															DUPLICATE_ID+"' INTEGER, '"+
															CAN_DUPLICATE+"' VARCHAR, '"+
															DO_MEASURE+"' INTEGER, '"+
															DO_PHOTO+"' INTEGER, "+
															"PRIMARY KEY ('"+SHEET_ID+"','"+SECTION_ID+"','"+ITEM_ID+"'));";
 
	public static final String CREATE_FIXES = "CREATE TABLE "+FORM_FIX_TABLE+
			                                               "('"+ITEM_ID+"' INTEGER, '"+
			                                               FIX_ID+"' VARCHAR, '"+
			                                               FIX_LEVEL+"' VARCHAR, '"+
			                                               FIX_TEXT+"' VARCHAR, "+
			                                               "PRIMARY KEY ('"+FIX_ID+"'));";
	
	//data tables create only if they don't already exist
	public static final String CREATE_DATA = "CREATE TABLE IF NOT EXISTS "+ITEM_DATA_TABLE+
														   "('"+SHEET_ID+"' INTEGER, '"+
														   SECTION_ID+"' INTEGER, '"+
														   DUPLICATE_ID+"' INTEGER, '"+
														   ITEM_ID+"' INTEGER, '"+
														   SURVEY_ID+"' INTEGER, '"+
														   TAKIN_LEVEL+"' VARCHAR, '"+
														   FIX_1_SELECTION+"' VARCHAR, '"+
														   FIX_2_SELECTION+"' VARCHAR, '"+
														   COMMENT+"' VARCHAR, '"+
														   MEASURE_RESULT+"' VARCHAR, '"+
														   IMAGE_LOCATION+"' VARCHAR, "+
														   "PRIMARY KEY ('"+SURVEY_ID+"','"+SHEET_ID+"','"+SECTION_ID+"','"+DUPLICATE_ID+"','"+ITEM_ID+"'));";
 
	public static final String CREATE_OWNER = "CREATE TABLE IF NOT EXISTS " +OWNER_TABLE +
															"('"+OWNER_ID+"' VARCHAR, '"+
															OWNER_NAME+"' VARCHAR, '"+
															OWNER_ADDRESS+"' VARCHAR, '"+
															OWNER_EMAIL+"' VARCHAR, '"+
															OWNER_PHONE+"' VARCHAR, "+
															" PRIMARY KEY ('"+OWNER_ID+"'));";

	public static final String CREATE_SURVEY = "CREATE TABLE IF NOT EXISTS "+SURVEY_TABLE+
															"('"+SURVEY_ID+"' INTEGER NOT NULL  UNIQUE, '"+
															OWNER_ID+"' VARCHAR, '"+
															SURVEY_START_DATE+"' VARCHAR, '"+
															SURVEY_END_DATE+"' VARCHAR, '"+
															BUILDING_NAME+"' VARCHAR, '"+
															BUILDING_NUM+"' VARCHAR, '"+
															BUILDING_STREET+"' VARCHAR, '"+
															BUILDING_TOWN+"' VARCHAR, '"+
															BUILDING_GUSH+"' VARCHAR, '"+
															BUILDING_HELKA+"' VARCHAR, '"+
															BUILDING_SUB_HELKA+"' VARCHAR, "+
															"PRIMARY KEY ('"+OWNER_ID+"','"+SURVEY_ID+"'));";
	
}
