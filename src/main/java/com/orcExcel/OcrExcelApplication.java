package com.ocrexcel;

import cn.hutool.core.io.FileUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.ocrexcel.OcrRes;
import com.ocrexcel.OcrUtil;


import java.io.File;
import java.util.Scanner;

@Slf4j
public class OcrExcelApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        OcrController ocrController = new OcrController();

        while(true){
            
            System.out.println("欢迎使用图片转Excel工具");
            System.out.println("请选择操作：");
            System.out.println("1. 单张图片转Excel");
            System.out.println("2. 批量图片转Excel（指定目录下图片）");
            System.out.println("3. 退出");
            System.out.print("请输入选项（1、2或3）：");

            // int choice = scanner.nextInt();
            // scanner.nextLine(); // 清除换行符
            // 输入验证
            int choice = -1;
            while (choice < 1 || choice > 3) {
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // 清除换行符
                    if (choice < 1 || choice > 3) {
                        System.out.println("无效选项，请输入1、2或3。");
                    }
                } else {
                    System.out.println("无效输入，请输入数字1、2或3。");
                    scanner.nextLine(); // 清除无效输入
                }
            }

            switch (choice) {
                case 1:
                    System.out.print("请输入图片文件路径：");
                    String singleImagePath = scanner.nextLine();
                    String result = ocrController.ocrExcel(singleImagePath);
                    System.out.println(result);
                    break;
                case 2:
                    System.out.print("请输入图片目录路径：");
                    String directoryPath = scanner.nextLine();
                    // processBatchImages(directoryPath);
                    processBatchImages(directoryPath, ocrController);
                    break;
                case 3:
                    System.out.println("退出程序。");
                    scanner.close();
                    return; // 退出循环
                default:
                    break;
                    // System.out.println("无效选项，请重新输入。");
            }
        }
       
    }

    private static void processSingleImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (!imageFile.exists()) {
            System.out.println("文件不存在，请检查路径。");
            return;
        }

        // 调用OCR处理逻辑
        OcrRes result = OcrUtil.getOcrResult(FileUtil.readBytes(imageFile));
        if (result != null && result.getCode() == 100) {
            System.out.println("识别成功，结果如下：");
            result.getData().forEach(data -> {
            });
        } else {
            System.out.println("识别失败，请检查图片内容。");
        }
    }

    private static void processBatchImages(String directoryPath,OcrController ocrController) {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("目录不存在，请检查路径。");
            return;
        }

        File[] imageFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || 
                                                               name.toLowerCase().endsWith(".png") || 
                                                               name.toLowerCase().endsWith(".jpeg") || 
                                                               name.toLowerCase().endsWith(".bmp"));
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("目录中没有图片文件。");
            return;
        }

        for (File imageFile : imageFiles) {
            System.out.println("处理文件: " + imageFile.getName());
            String result = ocrController.ocrExcel(imageFile.getAbsolutePath());
            System.out.println(result);
            // OcrRes result = OcrUtil.getOcrResult(FileUtil.readBytes(imageFile));
            // if (result != null && result.getCode() == 100) {
            //     System.out.println("识别成功，结果如下：");
            //     result.getData().forEach(data -> {
            //         System.out.println("文本: " + data.getText());
            //     });
            // } else {
            //     System.out.println("识别失败，文件: " + imageFile.getName());
            // }
        }
    }
}
