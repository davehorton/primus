package com.beachdog.primusCore;

public interface PactolusPOSConstants {
	//transaction code
	int TRANS_CODE_ACTIVATE = 20;
	int TRANS_CODE_RECHARGE = 21;
	int TRANS_CODE_STATUS_REQ = 23;
	int TRANS_CODE_DEACTIVATE = 24;
	
	
	//response codes
	String RESP_CODE_SUCCESS = "00";
	String RESP_CODE_SN_NOTFOUND = "10";
	String RESP_CODE_CARD_ALREADY_ACTIVE = "20";
	String RESP_CODE_INVALID_DATA = "30";
	String RESP_CODE_CARD_NOT_ACTIVE = "40";
	String RESP_CODE_DEACTIVATED = "41";
	String RESP_CODE_EXCEEDS_MAX_BAL = "50";
	String RESP_CODE_SUSPENDED = "70";
	String RESP_CODE_IN_USE = "80";
	String RESP_CODE_ERROR = "99";
	
//	response codes
	int RET_CODE_SUCCESS = 0;
	int RET_CODE_SN_NOTFOUND = -6;
	int RET_CODE_CARD_ALREADY_ACTIVE = -2;
	int RET_CODE_CARD_NOT_ACTIVE = -7;
	int RET_CODE_DEACTIVATED = -1;
	int RET_CODE_SUSPENDED = -8;
	int RET_CODE_IN_USE = -9;
	int RET_CODE_ERROR = -99;
}
