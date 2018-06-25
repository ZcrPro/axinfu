package com.zhihuianxin.xyaxf.app;

import com.axinfu.modellib.thrift.base.SysName;
import com.zhihuianxin.xyaxf.BuildConfig;

/**
 * Created by Vincent on 2016/11/1.
 */

public class AppConstant {
    // axinfu-v2-devel.zhihuianxin.com
    // https://appserver-v2-test.zhihuianxin.com 银联二维码测试环境
    // https://axinfu-v2-online.zhihuianxin.com/app-service/2.0.3/
    // https://appserver-preview.axinfu.com/app-service/2.0.4/
    public static final String URL = BuildConfig.API_HOST;
    public static final int DEFAULT_COUNT = 10000;

    public static final SysName SYSTEM_NAME = SysName.Android;
    public static final int PAGE_SIZE = 20;

    public static final int SIGNATURE_OR_JSON_NOT_FOUND = 9001;
    public static final int ILLEGAL_FORMAT_RESPONSE = 9002;
    public static final int CAN_DECODE_VALUE_UTF8 = 9003;
    public static final int SIGNATURE_ERROR = 9004;
    public static final int INIT_JSON_ERROR = 9005;
    public static final int RELOGIN = 9006;
    public static final int OTHER_SERVER_ERROR = 9007;

    public static final String SUCCESS = "AS0000";
    public static final String SESSION_OUT_OF_TIME = "AS0001";
    public static final String LOGIN_IN_OTHER_DEVICE = "AS0002";
    public static final String INVALID_SESSION = "AS0003";
    public static final String SESSION_ERROR_ID_NULL = "AS0011";
    public static final String SESSION_SETTING_ERROR = "AS0106";
    public static final String INVALID_TIMESTAMP = "AS0004";
    public static final String LOGIN_REQUIRED = "AS0004";
    public static final String SCHOOL_REQUIRED = "AS0005";
    public static final String STUDENT_REQUIRED = "AS0006";
    public static final String ECARD_REQUIRED = "AS0007";
    public static final String FEE_REQUIRED = "AS0008";
    public static final String INVALID_REQUEST_VALUE = "AS0009";
    public static final String UNKNOWN_ERROR = "AS0098";
    public static final String SYSTEM_ERROR = "AS0099";


    // customer
    public static final String GENERAL_CUSTOMER_ERROR = "AS0100";
    public static final String SESSION_ERROR = "AS0101";
    public static final String PASSWORD_NOT_MATCH = "AS0102";
    public static final String ACCOUNT_ALREADY_EXIST = "AS0103";
    public static final String ACCOUNT_NOT_EXIST = "AS0104";
    public static final String SWITCH_SCHOOL_NOT_ALLOWED = "AS0105";

    // app
    public static final String GENERAL_APP_ERROR = "AS0200";

    // business
    public static final String GENERAL_BUSINESS_ERROR = "AS0300";
    public static final String BUSINESS_NOT_ENABLED = "AS0301";

    // student
    public static final String GENERAL_STUDENT_ERROR = "AS0400";
    public static final String GENERAL_NAME_NOT_MATCH = "AS0401";

    // ecard
    public static final String GENERAL_ECARD_ERROR = "AS0501";
    public static final String ECARD_ACCOUNT_ALREADY_EXIST = "AS0501";
    public static final String NEED_ECARD_PASSWORD = "AS0502";

    // fee
    public static final String GENERAL_FEE_ERROR = "AS05001";
    public static final String FEE_ACCOUNT_ALREADY_EXIST = "AS0601";

    // payment
    public static final String GENERAL_PAYMENT_ERROR = "AS0700";
    public static final String INVALID_PAY_TYPE_ERROR = "AS0701";

    // resource
    public static final String GENERAL_RESOURCE_ERROR = "AS0800";
    public static final String INVALID_FILE_TYPE = "AS0801";

    // secure code
    public static final String GENERAL_SECURE_CODE_ERROR = "AS0900";
    public static final String INVALID_SECURE_CODE_PURPOSE = "AS0801";
    public static final String SECURE_CODE_NOT_MATCH = "AS0802";
    public static final String SEND_SMS_FAILED = "AS0803";

    // message
    public static final String GENERAL_MESSAGE_ERROR = "AS1000";

    // UPQR
    public static final String UPQR_PAY_PWD_ERROR = "UP0002";  // 银行卡余额不足
    public static final String UPQR_PAYPWD_THTIMES_ERROR = "AS0719";
    public static final String NEED_TYPES = "AS1608";
    public static final String FREE = "AS0722";

    // modify pwd
    public static final String MODIFY_PWD_FIELD_ERROR_TIME = "AS0119";
    // modify pwd by code
    public static final String MODIFY_PWD_FIELD_ERROR_TIME_BYCODE = "AS0120";

}
