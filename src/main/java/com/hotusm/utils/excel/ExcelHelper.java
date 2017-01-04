package com.hotusm.utils.excel;

import com.hotusm.utils.ReflectionUtil;
import com.hotusm.utils.excel.annotation.Sheet;
import com.hotusm.utils.excel.annotation.Row;

import java.lang.reflect.Field;
import java.util.*;


public class ExcelHelper {

    /**
     * 类对应的字段 进行缓存
     */
	private static ThreadLocal<Map<Class,Field[]>> classFields=new ThreadLocal<Map<Class,Field[]>>(){

        @Override
        protected Map<Class, Field[]> initialValue() {
            return new HashMap<Class, Field[]>();
        }
    };

	public static <T> ExcelNode node(Class<T> clazz){
		
		Sheet sheet = clazz.getAnnotation(Sheet.class);
		if(sheet==null){
		    throw new RuntimeException("实体没有发现注解 @Sheet 不能够构造Excel 请查看相关文档进行操作");
        }
		ExcelNode node=new ExcelNode();
		String sheetName = sheet.value();
		node.setSheetName(sheetName);
		Field[] fecLds = ReflectionUtil.getAllDeclaredFields(clazz);
		
		for(Field fecLd:fecLds){
			
			Row row = fecLd.getAnnotation(Row.class);
			if(row==null)continue;
			int index=row.index();
			if(index==-1){
				if(node.getTitles().size()==0){
					index=0;
				}else{
					index=node.getTitles().size();
				}
			}
			String columnName=row.columnName()==null?"":row.columnName();
			node.putTitle(index, columnName);
			node.putField(index, fecLd);
		}

		return node;
	}

	/**
	 *  将实体转为map  map储存的是索引和值
	 * @param obj  对象
	 * @param <T>
	 * @return
	 */
	public static <T> Map<Integer,Object> transObj2Row(T obj){

		Field[] fecLds=null;
		if(classFields.get().get(obj.getClass())!=null){
			fecLds=classFields.get().get(obj.getClass());
		}else{
			fecLds = ReflectionUtil.getAllDeclaredFields(obj.getClass());
			classFields.get().put(obj.getClass(),fecLds);
		}

		Map<Integer,Object> rows=new TreeMap<Integer, Object>();
		for(Field fecLd:fecLds){
			Row row = fecLd.getAnnotation(Row.class);
			if(row==null){
				continue;
			}
			int index=row.index();
			if(index==-1){
				if(rows.size()==0){
					index=0;
				}else{
					index=rows.keySet().size();
				}
			}
			
			try {
				rows.put(index, ReflectionUtil.invokeGetter(fecLd, obj));
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return rows;
	}
	
	public static <T> List<Map<Integer,Object>> transObjs2Rows(Collection<T> objs){
		List<Map<Integer,Object>> rows=new ArrayList<Map<Integer,Object>>();
		
		for(Object obj:objs){
			rows.add(transObj2Row(obj));
		}
		return rows;
	}
	
}
