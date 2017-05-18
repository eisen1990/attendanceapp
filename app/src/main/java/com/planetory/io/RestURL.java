package com.planetory.io;

class RestURL {
    static final String SERVER_URL = "http://203.255.92.139:8080/io/app/";
    static final String CHECK_URL = SERVER_URL + "checkUser/";
    static final String ADDUSER_URL = SERVER_URL + "addUser/";
    static final String LOGINUSER_URL = SERVER_URL + "loginUser/";
    static final String CHECKINTOWORK_URL = SERVER_URL + "checkintowork/";
    static final String CHECKOUTOFWORK_URL = SERVER_URL + "checkoutofwork/";
    static final String CHECKWORKON_URL = SERVER_URL + "workon/";
    static final String GETMEMBERS_URL = SERVER_URL + "getMembers";
    static final String SPECMEMBER_URL = SERVER_URL + "specMember/";
    static final String EDITPUNCH_URL = SERVER_URL + "editPunchTime/";
    static final String REQUESTWIFI_URL = SERVER_URL + "requestWifi/";
    static final String ACCEPTWIFI_URL = SERVER_URL + "acceptWifi/";
    static final String ACCEPTOKWIFI_URL = SERVER_URL + "acceptWifi/OK/";

    static final String NULL_STRING = "serverError";

    static final String REGISTER_NO_ERROR = "checkUser";
    static final String REGISTER_EXIST = "checkUserExisted";
    static final String REGISTER_ID_ERROR = "checkUserNoRegistration";
    static final String REGISTER_SUCCESS = "addUser";

    static final String LOGIN_SUCCESS = "loginUser";
    static final String LOGIN_WRONG_PASSWORD = "loginUserPasswordFail";
    static final String LOGIN_WRONG_ID = "loginUserUnregistered";

    static final String CHECK_PARAM_IN = "in";
    static final String CHECK_PARAM_OUT = "out";
    static final String CHECK_PARAM_WORKON = "workon";
    static final String PUNCHIN_SUCCESS = "INOK";
    static final String PUNCHOUT_SUCCESS = "OUTOK";
    //TODO : 이 경우들이 존재하는지?
    static final String PUNCHIN_FAIL = "INOK";
    static final String PUNCHOUT_FAIL = "OUTOK";

    static final String EDIT_ERROR = "EDITERROR";
    static final String EDIT_OK = "EDITPUNCHOK";
    static final String EDIT_EXCEPTION = "EDITPUNCHEXCEPTION";
}
