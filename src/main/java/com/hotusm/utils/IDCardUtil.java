package com.hotusm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 检验身份证工具类
 *
 * @author Deng, Yuan
 * @create 2016/12/15 16:05
 */
public class IDCardUtil {
    //身份证全数字正则表达式
    private final static String REGEX_ID_CARD_NUMERIC = "[0-9]{15,18}";
    private final static String REGEX_ID_CARD_NUMERIC_17 = "[0-9]{17}";
    //身份证最后一位数组
    private final static char[] checkHou = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    //权重
    private final static int[] checkGu = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};


    public static boolean checkIdentity(String num) {
        if (StringUtils.isEmpty(num)) {
            return false;
        }
        // 不是15位或不是18位都是无效身份证号
        int len = num.length();
        if (len != 15 && len != 18) {
            return false;
        }

        String areaNum;         //省市县（6位）
        String dateNum;         //出生年月（6/8位）
        String sexNum;         // 性别（3位）
        char endNum = ' ';          // 校验码（0/1位）

        //数值
        boolean isNumeric = validByRegex(num, REGEX_ID_CARD_NUMERIC, true);
        if (isNumeric) {
            if (len == 15) {
                areaNum = num.substring(0, 6);
                dateNum = num.substring(6, 12);
                sexNum = num.substring(12, 15);
            } else {
                areaNum = num.substring(0, 6);
                dateNum = num.substring(6, 14);
                sexNum = num.substring(14, 17);
                endNum = num.charAt(17);
            }
        } else {
            if (len == 15) {
                return false;
            } else {
                if (!validByRegex(num, REGEX_ID_CARD_NUMERIC_17, true)) {
                    return false;
                }
                areaNum = num.substring(0, 6);
                dateNum = num.substring(6, 14);
                sexNum = num.substring(14, 17);
                endNum = num.charAt(17);
                if (endNum != 'x' && endNum != 'X') {
                    return false;
                }
            }

        }
        //检验区域、日期、最后一位
        if (!checkArea(areaNum) || !checkDate(dateNum)||!checkEnd(endNum, num)) {
            return false;
        }
        return true;
    }


    /**
     * 验证地区
     *
     * @param area
     * @return
     */
    private static boolean checkArea(String area) {
        int provinceNum = Integer.parseInt(area.substring(0, 2));
        // 根据GB/T2260—999，省份代码11到65
        if (provinceNum > 10 && provinceNum < 66) {
            return true;
        }
        return false;
    }

    /**
     * 验证出生日期
     *
     * @param date
     * @return
     */
    private static boolean checkDate(String date) {
        int year;
        int month;
        int day;
        if (date.length() == 6) {
            year = Integer.parseInt(date.substring(0, 2)) + 1900;
            month = Integer.parseInt(date.substring(2, 4));
            day = Integer.parseInt(date.substring(4, 6));
        } else {
            year = Integer.parseInt(date.substring(0, 4));
            month = Integer.parseInt(date.substring(4, 6));
            day = Integer.parseInt(date.substring(6, 8));
        }
        //判断年
        int nowYear = Integer.parseInt(DateUtil.getDateNowYYYY());
        int nowMonth = Integer.parseInt(DateUtil.getDateNowMM());
        int nowDay = Integer.parseInt(DateUtil.getDateNowDD());
        if (year < 1900 || year > nowYear) {
            return false;
        }
        //判断月
        if (month < 1 || month > 12 || (year == nowYear && month > nowMonth)) {
            return false;
        }
        //判断日
        if (day < 1 || day > getDayNumOfMonth(year, month) || (year == nowYear && month == nowMonth && day > nowDay)) {
            return false;
        }
        return true;
    }


    /**
     * 判断是否是闰年，true是，false否
     *
     * @param year
     * @return
     */
    private static boolean checkLeapYear(int year) {
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                return true;
            } else {
                return false;
            }
        } else if (year % 4 == 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取当月天数
     *
     * @param year
     * @param month
     * @return
     */
    private static int getDayNumOfMonth(int year, int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 2) {
            if (checkLeapYear(year)) {
                return 29;
            }
            return 28;
        } else {
            return 30;
        }
    }

    /**
     * 校验18位身份证最后1位
     *
     * @param end
     * @param num
     * @return
     */
    private static boolean checkEnd(char end, String num) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += checkGu[i] * (num.charAt(i) - '0');
        }
        int loc = sum % 11;
        if (checkHou[loc] == end) {
            return true;
        }
        return false;
    }

    /**
     * 检查字符串是否满足正则表达式，true满足，false不满足
     *
     * @param str
     * @param regex
     * @param isSensitive 是否大小写敏感，true是，false否
     * @return
     */
    private static boolean validByRegex(String str, String regex, boolean isSensitive) {
        Pattern pattern;
        if (isSensitive) {
            pattern = Pattern.compile(regex);
        } else {
            pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    public static void main(String[] args){
        System.out.println(checkIdentity("990822198910311472"));
        System.out.println(checkIdentity("510822198910311471"));
        System.out.println(checkIdentity("510822201810311472"));
        System.out.println(checkIdentity("510822200102291472"));
        System.out.println(checkIdentity("510822200102291472"));
    }


}
