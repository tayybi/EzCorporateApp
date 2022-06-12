package com.ezcorporate.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String QUERI_MAIN_MENUE_TABLE="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_OF_MAINMENUE + " (" +
            AllFields.MM_ID + " TEXT," +
            AllFields.MM_NAME + " TEXT," +
            AllFields.MM_ICON + " TEXT," +
            AllFields.MM_LINKS + " TEXT)";
    private static String QUERI_SUB_MENUE_TABLE="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_OF_SUBMENUE + " (" +
            AllFields.MSM_ID + " TEXT," +
            AllFields.SM_ID + " TEXT," +
            AllFields.SM_NAME + " TEXT," +
            AllFields.SM_ICON + " TEXT," +
            AllFields.SM_LINKS + " TEXT)";
    private static String QUERI_DD_GROUP="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_GROUP + " (" +
            AllFields.GROUPNAME + " TEXT," +
            AllFields.GROUPID + " TEXT)";
    private static String QUERI_DD_CAT="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_CAT + " (" +
            AllFields.CATAGRYNAME + " TEXT," +
            AllFields.CATAGRYID + " TEXT)";
    private static String QUERI_DD_SU_CAT="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_SUB_CAT + " (" +
            AllFields.SUBCATNAME + " TEXT," +
            AllFields.SUBCATID + " TEXT)";
    private static String QUERI_DD_SOUCE="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_SOURCE + " (" +
            AllFields.SOURCNAME + " TEXT," +
            AllFields.SOURCEID + " TEXT)";
    private static String QUERI_DD_STKH="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_STKH + " (" +
            AllFields.STKHNAME + " TEXT," +
            AllFields.STKHID + " TEXT)";
    private static String QUERI_DD_TASK="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_TASK + " (" +
            AllFields.TASKNAME + " TEXT," +
            AllFields.TASKID + " TEXT)";
    private static String QUERI_DD_STAGE="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_STAGE + " (" +
            AllFields.STAGENAME + " TEXT," +
            AllFields.STAGEID + " TEXT)";
    private static String QUERI_DD_USERS="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_DD_USERS + " (" +
            AllFields.USERNAME + " TEXT," +
            AllFields.USERID + " TEXT)";

    private static String S_TABLE_QUERI_NEW="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_OF_FINGRECORD_NEW + " (" +
            AllFields.UIDN1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AllFields.FIDN1 + " VARCHAR)";

    private static String QUERI_TABLE_CALL_LOGS="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_CALL_LOGS+ " (" +
            AllFields.CALLER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            AllFields.CALL_ATTEND_OR_UNATTENDED + " TEXT," +
            AllFields.CALLER_NAME_PHONE + " TEXT," +
            AllFields.CALL_DATE + " TEXT," +
            AllFields.CALL_DURATION + " TEXT," +
            AllFields.CALLER_COMMENTS + " TEXT," +
            AllFields.CALLER_MODE + " TEXT," +
            AllFields.CALLER_TYPE + " TEXT)";

    private static String QUERI_TABLE_NEW_INQUIRY="CREATE TABLE IF NOT EXISTS " + AllFields.TABLE_NEW_INQUIRY+ " (" +
                AllFields.INQUIRYID + " INTEGER PRIMARY KEY AUTOINCREMENT," +AllFields.USERIDNEWINQ + " TEXT," + AllFields.LDATE + " TEXT," + AllFields.LTIME + " TEXT," +
                AllFields.FINALGROUP + " TEXT," +AllFields.FINALCAT + " TEXT," +AllFields.FINALSUBCAT + " TEXT," +
                AllFields.FINALSOURCE + " TEXT," +AllFields.FINALCUSTOMERCAT + " TEXT," +AllFields.CNAME + " TEXT," +
                AllFields.CCONTACTPERSON + " TEXT," +AllFields.CMOBILE + " TEXT," +AllFields.CEMAIL + " TEXT," +
                AllFields.CADDRESS + " TEXT," +AllFields.CGEOLOCATION + " TEXT," +AllFields.FINALFALLOWBY + " TEXT," +
                AllFields.FINALMANAGEDBY + " TEXT," +AllFields.FINALSTAGE + " TEXT," +AllFields.IDETAIL + " TEXT," +
                AllFields.ICUSTNO + " TEXT," +AllFields.ISUBJECT + " TEXT," +AllFields.IESTIMATEDBUDGET + " TEXT," +
                AllFields.IDATE + " TEXT," +AllFields.IEXPDATE + " TEXT," +AllFields.FDATE + " TEXT," +
                AllFields.FTIME + " TEXT," +AllFields.FINALTASK + " TEXT," +AllFields.FTODO + " TEXT," +
                AllFields.FINALSTKH + " TEXT," +AllFields.STNAME + " TEXT," +AllFields.STMOBILE + " TEXT," +
                AllFields.STCONTACTPERSON+ " TEXT," +AllFields.STEMAIL + " TEXT)";

    private static String QUERI_DELETE_MAIN_MENUE_TABLE="DROP TABLE IF EXISTS " + AllFields.TABLE_OF_MAINMENUE;
    private static String QUERI_DELETE_SUB_MENUE_TABLE="DROP TABLE IF EXISTS " + AllFields.TABLE_OF_SUBMENUE;
    private static String QUERI_DELETE_DD_GROUP="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_GROUP;
    private static String QUERI_DELETE_DD_CAT="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_CAT;
    private static String QUERI_DELETE_DD_SUBCAT="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_SUB_CAT;
    private static String QUERI_DELETE_DDSTKH="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_STKH;
    private static String QUERI_DELETE_DDTASK="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_TASK;
    private static String QUERI_DELETE_DDSTAGE="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_STAGE;
    private static String QUERI_DELETE_DD_SOURE="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_SOURCE;
    private static String QUERI_DELETE_DD_USERS="DROP TABLE IF EXISTS " + AllFields.TABLE_DD_USERS;
    private static String QUERI_DELETE_CALL_LOGS="DROP TABLE IF EXISTS " + AllFields.TABLE_CALL_LOGS;
    private static String QUERI_DELETE_NEW_INQUIRY="DROP TABLE IF EXISTS " + AllFields.TABLE_NEW_INQUIRY;


    public DataBaseHelper(Context context){
        super(context,AllFields.DATABASE_NAME,null,AllFields.VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QUERI_MAIN_MENUE_TABLE);
        db.execSQL(QUERI_SUB_MENUE_TABLE);
        db.execSQL(S_TABLE_QUERI_NEW);
        db.execSQL(QUERI_DD_GROUP);
        db.execSQL(QUERI_DD_CAT);
        db.execSQL(QUERI_DD_SU_CAT);
        db.execSQL(QUERI_DD_SOUCE);
        db.execSQL(QUERI_DD_STAGE);
        db.execSQL(QUERI_DD_STKH);
        db.execSQL(QUERI_DD_TASK);
        db.execSQL(QUERI_DD_USERS);
        db.execSQL(QUERI_TABLE_CALL_LOGS);
        db.execSQL(QUERI_TABLE_NEW_INQUIRY);
        Log.i("tablecreated","yes");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(QUERI_DELETE_MAIN_MENUE_TABLE);
        db.execSQL(QUERI_DELETE_SUB_MENUE_TABLE);
        db.execSQL(QUERI_DELETE_DD_GROUP);
        db.execSQL(QUERI_DELETE_DD_CAT);
        db.execSQL(QUERI_DELETE_DD_SUBCAT);
        db.execSQL(QUERI_DELETE_DD_SOURE);
        db.execSQL(QUERI_DELETE_DDSTAGE);
        db.execSQL(QUERI_DELETE_DDTASK);
        db.execSQL(QUERI_DELETE_DDSTKH);
        db.execSQL(QUERI_DELETE_DD_USERS);
        db.execSQL(QUERI_DELETE_CALL_LOGS);
        db.execSQL(QUERI_DELETE_NEW_INQUIRY);
        onCreate(db);
    }

    public void insertMainMenueDetail(String mmId,String mmName,String mmIcon,String mmLinks){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.MM_ID,mmId);
        contentValues.put(AllFields.MM_NAME,mmName);
        contentValues.put(AllFields.MM_ICON,mmIcon);
        contentValues.put(AllFields.MM_LINKS,mmLinks);
        database.insert(AllFields.TABLE_OF_MAINMENUE,null,contentValues);
    }

    public void insertSubMenueDetail(String msmId,String smId,String smName,String smIcon,String smLinks){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.MSM_ID,msmId);
        contentValues.put(AllFields.SM_ID,smId);
        contentValues.put(AllFields.SM_NAME,smName);
        contentValues.put(AllFields.SM_ICON,smIcon);
        contentValues.put(AllFields.SM_LINKS,smLinks);
        database.insert(AllFields.TABLE_OF_SUBMENUE,null,contentValues);
    }

    public void insertDDGroup(String groupname,String groupid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.GROUPNAME,groupname);
        contentValues.put(AllFields.GROUPID,groupid);
        database.insert(AllFields.TABLE_DD_GROUP,null,contentValues);
    }

    public void insertDDSource(String sourcename,String sourceid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.SOURCNAME,sourcename);
        contentValues.put(AllFields.SOURCEID,sourceid);
        database.insert(AllFields.TABLE_DD_SOURCE,null,contentValues);
    }

    public void insertDDSTKH(String stkhname,String stkhid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.STKHNAME,stkhname);
        contentValues.put(AllFields.STKHID,stkhid);
        database.insert(AllFields.TABLE_DD_STKH,null,contentValues);
    }

    public void insertDDStage(String stagename,String stageid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.STAGENAME,stagename);
        contentValues.put(AllFields.STAGEID,stageid);
        database.insert(AllFields.TABLE_DD_STAGE,null,contentValues);
    }

    public void insertDDSubCat(String subcatname,String subcatid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.SUBCATNAME,subcatname);
        contentValues.put(AllFields.SUBCATID,subcatid);
        database.insert(AllFields.TABLE_DD_SUB_CAT,null,contentValues);
    }

    public void insertDDCat(String catname,String catid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.CATAGRYNAME,catname);
        contentValues.put(AllFields.CATAGRYID,catid);
        database.insert(AllFields.TABLE_DD_CAT,null,contentValues);
    }

    public void insertDDTask(String taskname,String taskid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.TASKNAME,taskname);
        contentValues.put(AllFields.TASKID,taskid);
        database.insert(AllFields.TABLE_DD_TASK,null,contentValues);
    }

    public void insertDDInqUser(String uname,String uid){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.USERNAME,uname);
        contentValues.put(AllFields.USERID,uid);
        database.insert(AllFields.TABLE_DD_USERS,null,contentValues);
    }

    public void insertFmdInDB(String fid1){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.FIDN1,fid1);
        database.insert(AllFields.TABLE_OF_FINGRECORD_NEW,null,contentValues);

    }

    public void insertCallLogs(String attendorUnattend,String namephone,String date,String duration,String comments,String type,String mode){
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.CALL_ATTEND_OR_UNATTENDED,attendorUnattend);
        contentValues.put(AllFields.CALLER_NAME_PHONE,namephone);
        contentValues.put(AllFields.CALL_DATE,date);
        contentValues.put(AllFields.CALL_DURATION,duration);
        contentValues.put(AllFields.CALLER_COMMENTS,comments);
        contentValues.put(AllFields.CALLER_TYPE,type);
        contentValues.put(AllFields.CALLER_MODE,mode);
        database.insert(AllFields.TABLE_CALL_LOGS,null,contentValues);
        Log.i("dbinserted","calllogs upload yes");
    }
    public void insertNewInquiryDB(String uID,String ldate, String ltime, String lgroup,
                                   String lcat, String lsubcat,String lsource,String cCustCat,
                                   String cname,String ccontctpersn, String cmob,String cemail,
                                   String cadd, String cgeo,String fmanageby,String ffollowby,
                                   String istage,String idetail, String icNo, String isubject,
                                   String iestimatedbudjet, String idate, String iexpdate, String fdate,
                                   String ftime, String ftask,String ftodo,String stktitle,
                                   String stkname,String stkmobile,String stkcntctPerson,String stkemail) {
        SQLiteDatabase database=getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(AllFields.USERIDNEWINQ,uID);        contentValues.put(AllFields.LDATE,ldate); contentValues.put(AllFields.LTIME,ltime);
        contentValues.put(AllFields.FINALGROUP,lgroup);        contentValues.put(AllFields.FINALCAT,lcat);
        contentValues.put(AllFields.FINALSUBCAT,lsubcat);        contentValues.put(AllFields.FINALSOURCE,lsource);
        contentValues.put(AllFields.FINALCUSTOMERCAT,cCustCat);        contentValues.put(AllFields.CNAME,cname);
        contentValues.put(AllFields.CCONTACTPERSON,ccontctpersn);        contentValues.put(AllFields.CMOBILE,cmob);
        contentValues.put(AllFields.CEMAIL,cemail);        contentValues.put(AllFields.CADDRESS,cadd);
        contentValues.put(AllFields.CGEOLOCATION,cgeo);        contentValues.put(AllFields.FINALMANAGEDBY,fmanageby);
        contentValues.put(AllFields.FINALFALLOWBY,ffollowby);        contentValues.put(AllFields.FINALSTAGE,istage);
        contentValues.put(AllFields.IDETAIL,idetail);        contentValues.put(AllFields.ICUSTNO,icNo);
        contentValues.put(AllFields.ISUBJECT,isubject);        contentValues.put(AllFields.IESTIMATEDBUDGET,iestimatedbudjet);
        contentValues.put(AllFields.IDATE,idate);        contentValues.put(AllFields.IEXPDATE,iexpdate);
        contentValues.put(AllFields.FDATE,fdate);        contentValues.put(AllFields.FTIME,ftime);
        contentValues.put(AllFields.FINALTASK,ftask);        contentValues.put(AllFields.FTODO,ftodo);
        contentValues.put(AllFields.FINALSTKH,stktitle);        contentValues.put(AllFields.STNAME,stkname);
        contentValues.put(AllFields.STMOBILE,stkmobile);        contentValues.put(AllFields.STCONTACTPERSON,stkcntctPerson);
        contentValues.put(AllFields.STEMAIL,stkemail);
        database.insert(AllFields.TABLE_NEW_INQUIRY,null,contentValues);
    }


    public Cursor showDataById(String tablename,String tableId,int id){
        String S_SHOW_STUDENT_DATA = "Select * FROM " + tablename +" WHERE " + tableId + " = " + "'" + id + "'";
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery(S_SHOW_STUDENT_DATA,null);
        return cursor;
    }

    public Cursor showTableData(String tablename){
        String MAIN_MENUE_DATA = "Select * FROM " +tablename;
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery(MAIN_MENUE_DATA,null);
        return cursor;
    }

    public void deleteTable(String tablname) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(tablname,null,null );
        onCreate(db);
        db.close();
        Log.i("dbtabledelete","TableDeleted");
    }

    public boolean deleteTableData(String tablename,String tableid, int id) {
       // boolean result = false;
        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor=db.rawQuery("Select * FROM " + AllFields.TABLE_OF_STUDENT,null);
//        cursor.moveToFirst();
//        if (cursor.getString(cursor.getColumnIndex(AllFields.S_ID)).equals(id)) {
           return db.delete(tablename, tableid + "=" + id, null)>0;
            //db.close();
           // result= true;
//        }else {
//             result=false;
//        }
      //  return result;
    }


//    public void updateRecord(int id,String name){
//        SQLiteDatabase db=getWritableDatabase();
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(AllFields.U_ID,id);
//        contentValues.put(AllFields.U_NAME,name);
//        db.update(AllFields.TABLE_OF_USER,contentValues,AllFields.U_ID + "="+id,null);
//
//    }

}
