# 清理项目并打包，生成的.jar文件（或其他格式的包）会存放在target目录下
mvn clean package
# 复制生成的jar包到ocrExcelUtil目录下
copy "target/ocrExcel-1.0.jar" "ocrExcelUtil/"
