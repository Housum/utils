package com.hotusm.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import org.apache.commons.lang3.StringUtils;

/**
 * 汉字转拼音工具类
 *
 * @author Deng, Yuan
 * @create 2016/12/2 15:13
 */
public class PinyinUtil {

    private static HanyuPinyinOutputFormat defaultFormat;

    static {
        defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param chinese
     * @return
     */
    public static String getFullSpell(String chinese) {

        char[] inputs = chinese.trim().toCharArray();
        StringBuffer output = new StringBuffer();

        for (char input : inputs) {
            output.append(getFullSpellOfWord(input));
        }

        return output.toString();
    }

    /**
     * 获取汉字串拼音首字母，英文字符不变
     *
     * @param chinese 汉字串
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese) {

        char[] inputs = chinese.trim().toCharArray();
        StringBuffer output = new StringBuffer();

        for (char input : inputs) {
            String temp = getFullSpellOfWord(input);
            if (StringUtils.isNotBlank(temp)) {
                output.append(temp.charAt(0));
            }
        }
        return output.toString();
    }

    /**
     * 获取一个汉字全拼音，其他字符忽略
     *
     * @param word
     * @return
     */
    private static String getFullSpellOfWord(char word) {
        String fullSpell = "";
        try {
            if (Character.toString(word).matches("[\\u4E00-\\u9FA5]+")) {
                String[] temp = PinyinHelper.toHanyuPinyinStringArray(word, defaultFormat);
                if (temp != null && temp.length > 0) {
                    fullSpell = temp[0];
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return fullSpell;
    }


    public static void main(String[] args) {
        String str = "拼音工具";
        System.out.println(str);
        System.out.println(PinyinUtil.getFullSpell(str));
        System.out.println(PinyinUtil.getFirstSpell(str));
    }

}
