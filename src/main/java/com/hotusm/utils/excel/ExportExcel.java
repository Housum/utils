package com.hotusm.utils.excel;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;

public class ExportExcel{

    private static final List<String> FILE_EXT= Arrays.asList("xls");

	/**
	 * 导出一个sheet
	 * @param vos       实体的数据
	 * @param clazz     导入的实体类型
	 * @param filePath  导出的路径
	 * @param <T>
	 * @return
	 */
	public <T> boolean export(Collection<T> vos,Class<T> clazz,String filePath){

	    if(filePath.lastIndexOf(".")==-1||
                !FILE_EXT.contains(filePath.substring(filePath.lastIndexOf(".")+1,
                        filePath.length()))){

            throw new RuntimeException("excel 格式错误,请检查您的输出格式,目前支持"+FILE_EXT);
        }
		Map<Class<?>,Collection<?>> maps =new HashMap<Class<?>, Collection<?>>();
		maps.put(clazz, vos);
		return export(maps, filePath);
	}

	/**
	 * 导出多个sheet
	 * @param maps  key->类型  value->实体的集合
	 * @param filePath  导入的路径
	 * @return
	 */
	public boolean export(Map<Class<?>,Collection<?>> maps,String filePath){
		HSSFWorkbook workbook = new HSSFWorkbook();
		for(Entry<Class<?>,Collection<?>> entry:maps.entrySet()){
			List<Map<Integer, Object>> transObjs2Rows = ExcelHelper.transObjs2Rows(entry.getValue());
			ExcelNode node = ExcelHelper.node(entry.getKey());
			export(transObjs2Rows, node, workbook);
		}
		try {
			FileOutputStream fOut = new FileOutputStream(filePath);
			workbook.write(fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

    /**
     *
     * @param rows  行数据
     * @param node  单个sheet对象
     * @return
     */
	protected HSSFSheet export(List<Map<Integer, Object>> rows,ExcelNode node,HSSFWorkbook workbook){
        HSSFSheet sheet = workbook.createSheet(node.getSheetName());
        int rowCount=rows.size();
        int columnCount=node.getTitles().size();
        HSSFRow headerRow = sheet.createRow(0);
        for(int i=0;i<columnCount;i++){
        	 HSSFCell cell = headerRow.createCell(i);
        	 cell.setCellValue(node.getTitles().get(i));
        }
        
        for(int i=0;i<rowCount;i++){
            //上面有一行被标题占用了
        	HSSFRow contentRow = sheet.createRow(i+1);
        	for(int j=0;j<columnCount;j++){
        		HSSFCell cell = contentRow.createCell(j);
        		cell.setCellValue(rows.get(i).get(j).toString());
        	}
        }
        
        return sheet;
        
	}

}
