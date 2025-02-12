# OCR to Excel Tool

## 项目简介

该项目是一个将图片中的文本识别并转换为Excel文件的工具。它使用OCR（光学字符识别）技术来处理图片，并将识别到的文本以结构化的方式写入Excel文件中。

## 目录结构
```
/src
    /main
        /java
            /com
                /ocrexcel
                    Ocr.java
                    OcrController.java
                    OcrExcelApplication.java
                    OcrRes.java
                    OcrUtil.java
        /resources
    pom.xml
```

## 依赖
该项目使用Maven进行构建，主要依赖如下：
- Spring Boot
- Apache POI
- Hutool
- Lombok

## 环境要求
- JDK 17 或更高版本
- Maven 3.6 或更高版本

## 安装与运行

### 1. 克隆项目
首先，克隆该项目到本地：
```bash
git clone <项目的Git仓库地址>
cd <项目目录>
```

### 2. 构建项目
使用Maven构建项目：
方法一：
```bash
mvn clean package
```
手动将target目录下生成orcExcel-1.0.jar复制到orcExcelUtil目录

方法二：
双击运行：package.ps1

### 3. 运行项目
运行主应用程序：
进入orcExcelUtil目录，启动start.bat
```

### 4. 使用说明
程序启动后，您将看到如下菜单：
```
欢迎使用图片转Excel工具
请选择操作：
1. 单张图片转Excel
2. 批量图片转Excel（指定目录下图片）
3. 退出
请输入选项（1、2或3）：
```

#### 单张图片转Excel
选择选项1，输入图片文件的完整路径，程序将识别该图片中的文本并生成Excel文件。

#### 批量图片转Excel
选择选项2，输入包含图片的目录路径，程序将处理该目录下所有支持的图片格式（.jpg, .png, .jpeg, .bmp），并生成相应的Excel文件。

#### 退出程序
选择选项3，程序将退出。

## 注意事项
- 确保图片文件的路径正确，且文件存在。
- 该工具依赖于PaddleOCR的可执行文件，请确保其路径正确并可访问。

## 贡献
本项目复刻改自：
https://github.com/Antenbabby/ocrExcel
https://github.com/hiroi-sora/PaddleOCR-json
欢迎任何形式的贡献！请提交问题或拉取请求。
---

如有任何问题或建议，请随时联系项目维护者。希望您能享受使用该工具的过程！