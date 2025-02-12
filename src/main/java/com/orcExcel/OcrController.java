package com.ocrexcel;

import com.ocrexcel.OcrRes;
import com.ocrexcel.OcrUtil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class OcrController {

    public static final String workPath=System.getProperty("user.dir");


    public String ocrExcel(String filePath) {
        try{
                File imageFile = new File(filePath);
                if (!imageFile.exists()) {
                    return "文件不存在，请检查路径。";
                }

                // 调用OCR处理逻辑
                OcrRes ocrResult = OcrUtil.getOcrResult(FileUtil.readBytes(imageFile));
                if (ocrResult != null && ocrResult.getCode() == 100) {
                    
                    //计算行高平均值,用作判定是否换行.如果大于1/3行高即换行
                    double[] doubles = ocrResult.getData().stream().map(x -> (double) x.getBox().get(2).get(1) - x.getBox().get(0).get(1)).mapToDouble(x ->x).toArray();
                    double averageY = StatUtils.mean(doubles);

                    Comparator<OcrRes.DataDTO> comparator = (x, y) -> {
                        Integer y1 = x.getBox().get(0).get(1);
                        Integer y2 = y.getBox().get(0).get(1);
                        return Math.abs(y1 - y2) < averageY/3 ? 0 : y1 - y2;
                    };
                    Comparator<OcrRes.DataDTO> comparator1=comparator.thenComparing((x,y)->{
                        Integer x1 = x.getBox().get(0).get(0);
                        Integer x2 = y.getBox().get(0).get(0);
                        return x1-x2;
                    });
                    ocrResult.setData(ocrResult.getData().stream().sorted(comparator1).collect(Collectors.toList()));

                    List<List<OcrRes.DataDTO>> rows =new ArrayList<>();
                    List<OcrRes.DataDTO> row=CollUtil.newArrayList();
                    Integer lastY=0;
                    Integer lastX=0;
                    for (OcrRes.DataDTO x : ocrResult.getData()) {
                        if (lastY==0) {
                            lastY=x.getBox().get(0).get(1);
                            lastX=x.getBox().get(0).get(0);
                        }
                        //判断是否换行.
                        Integer currY = x.getBox().get(0).get(1);
                        Integer currX = x.getBox().get(0).get(0);
                        if (currX-lastX<0||currY-lastY>averageY/3) {
                            rows.add(new ArrayList<>(row));
                            row=CollUtil.newArrayList();
                            lastY=currY;
                        }
                        lastX=currX;
                        row.add(x);
                    }
                    rows.add(new ArrayList<>(row));
                    //列对齐
                    List<OcrRes.DataDTO> dataDTOS = getStandRow(rows);
                    List<List<String>> rowStrs =new ArrayList<>();
                    for (List<OcrRes.DataDTO> r : rows) {
                        List<String> rowStr=CollUtil.newArrayList();
                        for (int i = 0; i < dataDTOS.size(); i++) {
                            int finalI = i;
                            Predicate<OcrRes.DataDTO>  include= x->{
                                Integer i1 = x.getBox().get(0).get(0);
                                Integer i2 = x.getBox().get(1).get(0);
                                Integer s1 = dataDTOS.get(finalI).getBox().get(0).get(0);
                                Integer s2 = dataDTOS.get(finalI).getBox().get(1).get(0);
                                return (i1>=s1&&i1<=s2)||(i2>=s1&&i2<=s2)||(s1>=i1&&s1<=i2)||(s2>=i1&&s2<=i2);
                            };
                            r.stream().filter(include).findFirst().ifPresentOrElse(x->rowStr.add(x.getText()),()->rowStr.add(""));
                        }
                        rowStrs.add(rowStr);
                    }
                    
                    // 将OCR结果写入Excel
                    // List<List<String>> rowStrs = convertOcrResultToExcelData(ocrResult);
                    // String excelFilePath = workPath+"/output.xlsx";
                    String excelFilePath = changeFileExtension(imageFile.getAbsolutePath(),".xlsx");
                    writeToExcel(rowStrs, excelFilePath);
                    return "Excel文件已生成: " + excelFilePath;
                } else {
                    return "识别失败，请检查图片内容。";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "处理过程中发生错误: " + e.getMessage();
            }
    }
    // 辅助方法：更改文件扩展名
    private String changeFileExtension(String filePath, String newExtension) {
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex == -1) {
            return filePath + newExtension; // 如果没有扩展名，直接添加
        }
        return filePath.substring(0, dotIndex) + newExtension; // 替换扩展名
    }
    private static List<OcrRes.DataDTO> getStandRow(List<List<OcrRes.DataDTO>> rows) {
        ArrayList<Map.Entry<Integer, List<List<OcrRes.DataDTO>>>> rowsGroup = new ArrayList<>(rows.stream().collect(Collectors.groupingBy(List::size)).entrySet());
        rowsGroup.sort(((x,y)->y.getKey()-x.getKey()));
        List<List<OcrRes.DataDTO>> rowList = rowsGroup.get(0).getValue();
        rowList.sort((x,y)->{
            Integer x1 = CollUtil.getFirst(x).getBox().get(0).get(0);
            Integer x2 = CollUtil.getLast(x).getBox().get(1).get(0);
            Integer y1 = CollUtil.getFirst(y).getBox().get(0).get(0);
            Integer y2 = CollUtil.getLast(y).getBox().get(1).get(0);
            return y2-y1-(x2-x1);
        });
        return rowList.get(0);
    }

    // 添加转换方法：将DataDTO列表转换为String列表
    private List<List<String>> convertToStringList(List<OcrRes.DataDTO> dataDTOList) {
        List<List<String>> result = new ArrayList<>();
        for (OcrRes.DataDTO dataDTO : dataDTOList) {
            List<String> row = new ArrayList<>();
            // 根据实际的DataDTO结构添加需要的字段
            row.add(dataDTO.getText());  // 假设DataDTO有getText()方法
            // 如果还有其他字段需要添加到Excel中，继续添加
            result.add(row);
        }
        return result;
    }

    // 添加辅助方法：将OCR结果写入Excel
    private void writeToExcel(List<List<String>> data, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i);
                List<String> rowData = data.get(i);
                
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }
            
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }
}
