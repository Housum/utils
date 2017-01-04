package com.hotusm.utils;

import com.hotusm.utils.excel.annotation.Row;
import com.hotusm.utils.excel.annotation.Sheet;

/**
 * Created by luqibao on 2016/12/27.
 */
@Sheet("测试1")
public class Entity1 {

    @Row(index = 0,columnName = "索引")
    private String index;
    @Row(index = 1,columnName = "备注")
    private String memo;

    @Row(index = 2,columnName = "备注1")
    private String memo1;
    @Row(index = 3,columnName = "备注2")
    private String memo2;
    @Row(index = 4,columnName = "备注3")
    private String memo3;
    @Row(index = 5,columnName = "备注4")
    private String memo4;



    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getMemo1() {
        return memo1;
    }

    public void setMemo1(String memo1) {
        this.memo1 = memo1;
    }

    public String getMemo2() {
        return memo2;
    }

    public void setMemo2(String memo2) {
        this.memo2 = memo2;
    }

    public String getMemo3() {
        return memo3;
    }

    public void setMemo3(String memo3) {
        this.memo3 = memo3;
    }

    public String getMemo4() {
        return memo4;
    }

    public void setMemo4(String memo4) {
        this.memo4 = memo4;
    }
}
