package core.base.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {

    public static boolean isPhoneNumber(String mobiles) {
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[^4,\\D]|8[0-9]|7[013678])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmailAddr(String emailAddr) {
        // 推演了一个例子，发现可以匹配，差推演多几个反例，强制多几个释放。
        // (?:xxx)是非捕获型括号，不要把它当元字符
        Pattern p = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        Matcher m = p.matcher(emailAddr);
        return m.matches();
    }

    public static boolean isMatchThePasswd(String passwd) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9-_!$#@]{6,20}$");
        Matcher m = p.matcher(passwd);
        return m.matches();
    }

    public static boolean isMatchAlipayCheckBankResp(String response) {
        Pattern p = Pattern.compile(".*\"key\":\"\\d{19}\".*");
        Matcher m = p.matcher(response);
        return m.matches();
    }

}
