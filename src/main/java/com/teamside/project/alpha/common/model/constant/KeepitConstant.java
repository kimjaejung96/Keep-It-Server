package com.teamside.project.alpha.common.model.constant;

public class KeepitConstant {
    private KeepitConstant(){}
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";


    public static final String REGEXP_NAME = "([-_A-Za-z0-9ㄱ-ㅎ가-힣\\\\.]){2,20}";
    public static final String REGEXP_EMAIL = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    public static final String REGEXP_PHONE = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    public static final String REGEXP_AUTH_NUM = "^([0-9]{6})$";


}
