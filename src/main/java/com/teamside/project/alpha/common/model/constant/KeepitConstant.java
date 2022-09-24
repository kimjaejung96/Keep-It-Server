package com.teamside.project.alpha.common.model.constant;

public class KeepitConstant {
    private KeepitConstant(){}
    public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";



    public static final String REGEXP_MEMBER_NAME = "^([-_A-Za-z0-9ㄱ-ㅎ가-힣[^\\\\.\\\\.]]){2,20}$";
    public static final String REGEXP_EMAIL = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
    public static final String REGEXP_PHONE = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";
    public static final String REGEXP_AUTH_NUM = "^([0-9]{6})$";
    public static final String REGEXP_GROUP_NAME = "^\\S.*\\S$";
    public static final String REGEXP_GROUP_PASSWORD = "^[a-zA-Z0-9]{4,8}$";

    public static final String REGEXP_EMOJI = "[^\\p{L}\\p{N}\\p{P}\\p{Z}]";

}
