package com.ezcorporate.Database;

import android.provider.BaseColumns;

public class AllFields implements BaseColumns{
    public static String DATABASE_NAME="ezcorporate.db";
    public static int VERSION=5;

    //////TABLES
    public static String TABLE_OF_MAINMENUE="mainmnue";
    public static String TABLE_OF_SUBMENUE="submnue";
    public static String TABLE_OF_FINGRECORD_NEW="fingrecordnew1";
    public static String TABLE_DD_GROUP="ddgroup";
    public static String TABLE_DD_CAT="ddcat";
    public static String TABLE_DD_SUB_CAT="ddsubcat";
    public static String TABLE_DD_SOURCE="ddsource";
    public static String TABLE_DD_STKH="ddstkh";
    public static String TABLE_DD_STAGE="ddstage";
    public static String TABLE_DD_TASK="ddtask";
    public static String TABLE_DD_USERS="ddusers";
    public static String TABLE_CALL_LOGS="calllogs";
    public static String TABLE_NEW_INQUIRY="newinquiry";

    ////////////  FIELDS

    /////////// main menue fiel
    public static String MM_ID="mmid";
    public static String MM_NAME="mmname";
    public static String MM_ICON="mmicon";
    public static String MM_LINKS="mmlinks";
    /////////// sub menue fiel
    public static String MSM_ID="msmid";
    public static String SM_ID="smid";
    public static String SM_NAME="smname";
    public static String SM_ICON="smicon";
    public static String SM_LINKS="smlinks";
    ////////figer feilds
    public static String UIDN1="uidn1";
    public static String FIDN1="fidn1";
    ///// drop down list
    public static String GROUPNAME="groupname";
    public static String GROUPID="groupid";
    public static String CATAGRYNAME="catagryname";
    public static String CATAGRYID="catagryid";
    public static String SUBCATNAME="sbcatagryname";
    public static String SUBCATID="sbcatagryid";
    public static String SOURCNAME="sourcename";
    public static String SOURCEID="sourceid";
    public static String STKHNAME="stkholderttlname";
    public static String STKHID="stkholderttlid";
    public static String STAGENAME="stagename";
    public static String STAGEID="stageid";
    public static String TASKNAME="taskname";
    public static String TASKID="taskid";
    public static String USERNAME="username";
    public static String USERID="userid";
    ///// call logs
    public static String CALLER_ID="callerid";
    public static String CALL_ATTEND_OR_UNATTENDED="unattendedorattended";
    public static String CALLER_NAME_PHONE="callerphoneno";
    public static String CALL_DURATION="callduration";
    public static String CALL_DATE="calldate";
    public static String CALLER_COMMENTS="callercomments";
    public static String CALLER_TYPE="callertype";
    public static String CALLER_MODE="callermode";
    ///// new Inquiry
    public static String INQUIRYID="inquiryid";
    public static String USERIDNEWINQ="userid";
    public static String LDATE="ldate";
    public static String LTIME="ltime";
    public static String FINALGROUP="finalgroup";
    public static String FINALCAT="finalcat";
    public static String FINALSUBCAT="finalsubcat";
    public static String FINALSOURCE="finalsource";
    public static String FINALCUSTOMERCAT="finalcustcat";
    public static String CNAME="cname";
    public static String CCONTACTPERSON="ccontactperson";
    public static String CMOBILE="callerphoneno";
    public static String CEMAIL="cmobile";
    public static String CADDRESS="caddress";
    public static String CGEOLOCATION="cgeolocation";
    public static String FINALFALLOWBY="finalfallowby";
    public static String FINALMANAGEDBY="finalmanagmentby";
    public static String FINALSTAGE="finalstage";
    public static String IDETAIL="idetail";
    public static String ICUSTNO="icustno";
    public static String ISUBJECT="isubject";
    public static String IESTIMATEDBUDGET="iestimatedbudget";
    public static String IDATE="idate";
    public static String IEXPDATE="iexpdate";
    public static String FDATE="fdate";
    public static String FTIME="ftime";
    public static String FINALTASK="finaltask";
    public static String FTODO="ftodo";
    public static String FINALSTKH="finalstkh";
    public static String STNAME="stname";
    public static String STMOBILE="stmobile";
    public static String STCONTACTPERSON="stcontactpersons";
    public static String STEMAIL="stemail";

}
