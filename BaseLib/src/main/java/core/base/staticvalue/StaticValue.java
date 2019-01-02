package core.base.staticvalue;

/**
 * @Author 陈俊彬
 * @Date 2017/8/3
 */

public class StaticValue {

    /**
     * 默认下载文件地址.
     */
    public static String DOWNLOAD_ROOT_DIR = "download";
    /**
     * 默认下载图片文件地址.
     */
    public static String IMAGE_PATH_DIR = "/images";
    /**
     * 默认缓存地址
     */
    public static String CACHE_IMAGE_DIR = "/cache";

    public static final String MEIDIDI = "meididi";
    public static final String SCENERY_APK_TYPE = ".apk";

    /**
     * 根据UI设计稿来填写尺寸
     */
    public static final int UI_WIDTH = 750;
    public static final int UI_HEIGHT = 1334;

    //这个大小要包括通知栏/导航栏
    //积分模块的headerView,在设计稿上的大小:
    private static final int INTEGRAL_RECORD_HEIGHT = 700;
    public static final float PLACE_HOLDER_HEIGHT_INTEGRAL_RECORD =
            getPlaceHolderHeightScale(INTEGRAL_RECORD_HEIGHT);
    //D币中心的headerView,在设计稿上的大小:
    private static final int DCOIN_CENTER_HEIGHT = 810;
    public static final float PLACE_HOLDER_HEIGHT_DEECOIN_CENTER =
            getPlaceHolderHeightScale(DCOIN_CENTER_HEIGHT);
    //商家店铺收益的headerView,在设计稿上的大小:
    private static final int MERCHANT_FLOW_HEIGHT = 550;
    public static final float PLACE_HOLDER_HEIGHT_MERCHANT_FLOW =
            getPlaceHolderHeightScale(MERCHANT_FLOW_HEIGHT);
    //流水明细的headerView在设计稿上的大小
    private static final int CASH_FLOW_HEIGHT = 440;
    public static final float PLACE_HOLDER_HEIGHT_CASH_FLOW =
            getPlaceHolderHeightScale(CASH_FLOW_HEIGHT);
    //我的步数的headerview在设计稿上的大小
    public static final int MY_STEP_HEIGHT = 590;
    //步步偷的headerview在设计稿上的大小
    public static final int STEAL_STEPS_HEIGHT = 650;
    //步步偷个人首页的headerview在设计稿上的大小
    public static final int STEAL_STEPS_HOME_PAGE_HEADER_HEIGHT = 720;
    //待返**页面的headerview在设计稿上的大小
    public static final int FROZEN_HEADER_VIEW_HEIGHT = 648;
    //补贴金页面的headview在设计稿上的大小
    public static final int REPLACE_FEE_HEADER_VIEW_HEIGHT = 512;


    public static float getPlaceHolderHeightScale(int height) {
        return Float.intBitsToFloat(UI_HEIGHT - height) / Float.intBitsToFloat(UI_HEIGHT);
    }

    public static final int THE_SECONDS_OF_AN_HOUR = 60 * 60;
    public static final int THE_MILLISECONDS_OF_A_MINTUE = 60 * 1000;
    public static final int THE_MILLISECONDS_OF_AN_HOUR = 60 * 60 * 1000;
    public static final int WITHOUT_ORDER_BILL = -10010;
    public static final int TOTAL_SHOW_DAYS = 30;


    public static final String DEFAULT_DAY_FORMAT = "yyyy-MM-dd   HH:mm";
    public static final String DEFAULT_DAY_WITHOUT_HOUR_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DAY_WITH_DOT = "yyyy.MM.dd";
    public static final String DEFAULT_CHINESE_YEAR_MONTH_DAY = "yyyy年MM月dd日";
    public static final int PROJECT_START_MONTH = 2017;
    public static final int BEAUTY_START_TIME = 1900;

    public static final String CHECK_UPDATE_FORM_AUTO = "auto";
    public static final String CHECK_UPDATE_FORM_CLICK = "manual";

    public static final int NETWORK_TIMEOUT_MILLISECONDS = 3 * 1000;

    public static final int DEFAULT_COUNT = 10;
    public static final int MASTER_COUNT = 100;

    public static final int DEFAULT_TITLE_ROW = 0;//表头行
    public static final int COMMON_USER = 1;//普通用户
    public static final int OUR_MEMBER = 2;//平台会员
    public static final int SPECAIL_MEMBER = 3;//平台粉丝
    public static final int LOYAL_USER = 4;//平台铁粉
    public static final int SPECAIL_LOYAL_USER = 5;//平台钢粉

    public static final int DEFAULT_SCALE_TYPE = 0;//默认方式

    public static final String USE_ALIPAY_INTERFACE_TO_JUDGE_BANK_TYPE = "https://ccdcapi.alipay.com/";
    public static final String USE_ALIPAY_INTERFACE_TO_JUDGE_BANK_CARD_QUERY = "cardNo";
    public static final String USE_ALIPAY_INTERFACE_TO_JUDGE_BANK_TYPE_PATH = "validateAndCacheCardInfo.json"
            + "?_input_charset=utf-8&cardBinCheck=true";

    /**
     * 默认响应码，用以判断返回的数据格式与后台数据格式是否符合，如果不符合，则为第三方接口，如：验证银行卡信息
     */
    public static final int DEFAULT_RESPONSE_CODE = -1;

    public static final String BANK_NAME_JSON_FILE_NAME = "bank_name.json";

    /**
     * 平台会员ID
     */
    public static final String DEFAULT_SERVICE_ID = "478";

    /**
     * 经纪人客户上传项目必备图片(除了列表图片)
     */
    public static final int BEAUTYHUI_UPLOAD_PROJECT_EXCEPT_LIST_PHOTO = 3;


}
