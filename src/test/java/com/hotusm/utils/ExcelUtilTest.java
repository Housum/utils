package com.hotusm.utils;

import com.hotusm.utils.excel.ExportExcel;
import org.junit.Test;

import java.util.*;

/**
 * Created by luqibao on 2016/12/27.
 */
public class ExcelUtilTest {

    @Test
    public void testExport(){

        List<Entity> lists= new ArrayList<Entity>();
        for(int i=0;i<10000;i++){
           // lists.add(new Entity(""+i,""+i+i+i));
        }
        List<Entity1> lists1= new ArrayList<Entity1>();
        for(int i=0;i<10000;i++){
            //lists1.add(new Entity1(""+i,""+i+i+i));
        }
        List<Entity2> lists2= new ArrayList<Entity2>();
        for(int i=0;i<10000;i++){
            lists2.add(new Entity2(""+i,""+i+i+i));
        }
        Map<Class<?>,Collection<?>> maps=new HashMap<Class<?>, Collection<?>>();
        maps.put(Entity.class,lists);
        maps.put(Entity1.class,lists);
        maps.put(Entity2.class,lists);
        long startTime=System.currentTimeMillis();
        new ExportExcel().export(maps,"C://111.xls");
        long endTime=System.currentTimeMillis()-startTime;
        System.out.print(endTime);
    }

    @Test
    public void testFileExt(){

        String filePath="111.xls";
      filePath=  filePath.substring(filePath.lastIndexOf(".")+1,
                filePath.length());

      System.out.print(filePath);
    }
}
