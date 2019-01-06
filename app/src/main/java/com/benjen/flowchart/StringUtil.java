package com.benjen.flowchart;


public class StringUtil {
    /**
     * 字符串转换long
     * @param str
     * @return
     */
    public static long string2Long(String str){
        long data;
        try {
            data = Long.valueOf(str);
        }catch (Exception e){
            data = 0L;
        }
        return data;
    }


    public static int string2Integer(String str){
        int data;
        try {
            data = Integer.valueOf(str);
        }catch (Exception e){
            data = 0;
        }
        return data;
    }


    public static double string2Double(String str){
        double data;
        try {
            data = Double.valueOf(str);
        }catch (Exception e){
            data = 0D;
        }
        return data;
    }


    public static float string2float(String str){
        float data;
        try {
            data = Float.valueOf(str);
        }catch (Exception e){
            data = 0.0f;
        }
        return data;
    }
}
