## Java 导出 Execl 表格（多级合并）

> 项目中经常会使用到 `导出` 功能，有导出 Word，有导出 Excel的，本文主要记录导出稍微复杂点的Excel表，涉及表头中三级目录、框中画对角线、列分级合并、合并后单元格增加边框等。

先看下我们要导出的表格：

![](C:\Users\Administrator\Desktop\poi-excel\模板.png)

### 项目环境

```xml
<!-- 操作Excel主要的依赖 -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>3.8</version>
</dependency>
<!-- Spring boot环境 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.2.RELEASE</version>
</parent>
```

### 主方法代码

```java
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import tk.mybatis.mapper.entity.Condition;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Resource
private TjMapper tjMapper;

@Override
public String exportExcel(String path) {
    // 第一步，创建一个webbook，对应一个Excel文件
    HSSFWorkbook wb = new HSSFWorkbook();
    // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
    HSSFSheet sheet = wb.createSheet("各市累计数");
    // 第三步，设置样式以及字体样式
    HSSFCellStyle headerStyle = createHeaderCellStyle(wb);
    HSSFCellStyle titleStyle = createTitleCellStyle(wb);
    HSSFCellStyle contentStyle = createContentCellStyle(wb);
    HSSFCellStyle endStyle = createEndCellStyle(wb);

    // 第四步，创建标题，合并标题单元格
    int rowNum = 0; // 行号
    // 创建第一页的第一行，索引从0开始
    HSSFRow row0 = sheet.createRow(rowNum++);
    row0.setHeightInPoints(25);
    String title = "使用Java导出Excel表格（POI）";
    HSSFCell hc0 = row0.createCell(0);
    hc0.setCellValue(title);
    hc0.setCellStyle(headerStyle);
    // 合并单元格，参数依次为起始行，结束行，起始列，结束列（索引0开始）
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

    // 第二行
    HSSFRow row1 = sheet.createRow(rowNum++);
    row1.setHeightInPoints(15);
    String date = "统计时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分"));
    String[] row_first = {"", "", "", "", "", "", "", "", "", date, "", "", ""};
    for (int r = 0; r < row_first.length; r++) {
        HSSFCell hc1 = row1.createCell(r);
        hc1.setCellStyle(titleStyle);
        hc1.setCellValue(row_first[r]);
    }
    // 合并单元格
    sheet.addMergedRegion(new CellRangeAddress(1, 1, 9, 13));

    // 第三行
    HSSFRow row2 = sheet.createRow(rowNum++);
    row2.setHeightInPoints(37);
    String[] row_second = {"项目\n\n\n\n       单位", "区域", "时间", "车", "", "", "", "行车情况","", "人", "", "", "", "",};
    for (int i = 0; i < row_second.length; i++) {
        HSSFCell tempCell = row2.createCell(i);
        tempCell.setCellValue(row_second[i]);
        tempCell.setCellStyle(titleStyle);
        // 在第一个框中画对角线
        if (i == 0) {
            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            HSSFClientAnchor clientAnchor = new HSSFClientAnchor();
            // 参数分别为第一个单元格的列,第一个单元格的行,第一个单元格的x坐标,第一个单元格的y坐标,第二个单元格的列,第二个单元格的行,第二个单元格的x坐标,第二个单元格的y坐标
            clientAnchor.setAnchor((short) 0, 2, 0, 2, (short) 1, 5, 1, 5);
            HSSFSimpleShape simpleShape = patriarch.createSimpleShape(clientAnchor);
            simpleShape.setShapeType(HSSFSimpleShape.OBJECT_TYPE_LINE);
            simpleShape.setLineStyle(HSSFShape.LINESTYLE_SOLID);
        }
    }

    // 合并
    sheet.addMergedRegion(new CellRangeAddress(2, 4, 0, 0));//项目、单位
    sheet.addMergedRegion(new CellRangeAddress(2, 4, 1, 1));//区域
    sheet.addMergedRegion(new CellRangeAddress(2, 4, 2, 2));//时间
    sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, 6));//车
    sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 8));//行车情况
    sheet.addMergedRegion(new CellRangeAddress(2, 2, 9, 13));//人

    // 第四行
    HSSFRow row3 = sheet.createRow(rowNum++);
    row3.setHeightInPoints(37);
    String[] row_third = {"", "", "", "合计", "浙牌车", "", "其他车", "高速", "非高速", "汉族", "", "", "", "其他 民族"};
    for (int i = 0; i < row_third.length; i++) {
        HSSFCell tempCell = row3.createCell(i);
        tempCell.setCellValue(row_third[i]);
        tempCell.setCellStyle(titleStyle);
    }

    // 合并
    sheet.addMergedRegion(new CellRangeAddress(3, 4, 3, 3));//合计
    sheet.addMergedRegion(new CellRangeAddress(3, 3, 4, 5));//浙牌车
    sheet.addMergedRegion(new CellRangeAddress(3, 4, 6, 6));//其他车
    sheet.addMergedRegion(new CellRangeAddress(3, 4, 7, 7));//高速
    sheet.addMergedRegion(new CellRangeAddress(3, 4, 8, 8));//非高速
    sheet.addMergedRegion(new CellRangeAddress(3, 3, 9, 12));//汉族
    sheet.addMergedRegion(new CellRangeAddress(3, 4, 13, 13));//其他民族

    // 第五行
    HSSFRow row4 = sheet.createRow(rowNum++);
    row4.setHeightInPoints(37);
    String[] row_four = {"", "", "", "", "总数", "浙A车", "", "", "", "合计", "男", "女", "其他", ""};
    for (int i = 0; i < row_four.length; i++) {
        HSSFCell tempCell = row4.createCell(i);
        tempCell.setCellValue(row_four[i]);
        tempCell.setCellStyle(titleStyle);
    }

    // 合并
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 4));//总数
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 5, 5));//浙A车
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 9, 9));//合计
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 10, 10));//男
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 11, 11));//女
    sheet.addMergedRegion(new CellRangeAddress(4, 4, 12, 12));//其他

    // 查询统计数据列表[此处查询数据库数据--线上环境是专门为统计做了一张表，导出时只要按照数据库中表插入数据即可]
    Condition tjCondition = new Condition(Tj.class);
    List<Tj> tjs = tjMapper.selectByCondition(tjCondition);

    // 第五步，开始将数据填充
    if (tjs != null && !tjs.isEmpty()) {
        for (int j = 0; j < tjs.size(); j++) {
            HSSFRow hssfRow = sheet.createRow(rowNum + j);
            hssfRow.setHeightInPoints(15);
            tj tj = tjs.get(j);
            // 单位
            if (j % 4 == 0) {
                // 合并单元格
                HSSFCell cell0 = hssfRow.createCell(0);
                cell0.setCellStyle(contentStyle);
                cell0.setCellValue(tj.getDw());
                CellRangeAddress cell0Cra = new CellRangeAddress(j + 5, j + 8, 0, 0);
                sheet.addMergedRegion(cell0Cra);
                setBorderStyle(HSSFCellStyle.BORDER_THIN, cell0Cra, sheet, wb);
            }
            // 区域【线上区域是有两个的，这边考虑表格内容过长，做一个区域处理】
            if (j % 2 == 0) {
                HSSFCell cell1 = hssfRow.createCell(1);
                cell1.setCellStyle(contentStyle);
                cell1.setCellValue(tj.getQy());
                // 合并单元格
                CellRangeAddress rangeAddress = new CellRangeAddress(j + 5, j + 6, 1, 1);
                sheet.addMergedRegion(rangeAddress);
                setBorderStyle(HSSFCellStyle.BORDER_THIN, rangeAddress, sheet, wb);
            }
            // 时间
            HSSFCell cell2 = hssfRow.createCell(2);
            cell2.setCellStyle(contentStyle);
            cell2.setCellValue(tj.getTjsj());
            // 合计
            HSSFCell cell3 = hssfRow.createCell(3);
            cell3.setCellStyle(contentStyle);
            cell3.setCellValue(tj.getClzs());
            // 总数
            HSSFCell cell4 = hssfRow.createCell(4);
            cell4.setCellStyle(contentStyle);
            cell4.setCellValue(tj.getClzs());
            // 浙A车辆数
            HSSFCell cell5 = hssfRow.createCell(5);
            cell5.setCellStyle(contentStyle);
            cell5.setCellValue(tj.getZacls());
            // 其他车
            HSSFCell cell6 = hssfRow.createCell(6);
            cell6.setCellStyle(contentStyle);
            cell6.setCellValue(tj.getQtc);
            // 车辆移交数
            HSSFCell cell7 = hssfRow.createCell(7);
            cell7.setCellStyle(contentStyle);
            cell7.setCellValue(tj.getGss());
            // 车辆放行数
            HSSFCell cell8 = hssfRow.createCell(8);
            cell8.setCellStyle(contentStyle);
            cell8.setCellValue(tj.getFgss());
            // 人员总数
            HSSFCell cell9 = hssfRow.createCell(9);
            cell9.setCellStyle(contentStyle);
            cell9.setCellValue(tj.getRyzs());
            // 男
            HSSFCell cell10 = hssfRow.createCell(10);
            cell10.setCellStyle(contentStyle);
            cell10.setCellValue(tj.getNans());
            // 女
            HSSFCell cell11 = hssfRow.createCell(11);
            cell11.setCellStyle(contentStyle);
            cell11.setCellValue(tj.getNvs());
            // 其他数
            HSSFCell cell12 = hssfRow.createCell(12);
            cell12.setCellStyle(contentStyle);
            cell12.setCellValue(tj.getQts());
            // 其他民族
            HSSFCell cell13 = hssfRow.createCell(13);
            cell13.setCellStyle(contentStyle);
            cell13.setCellValue(tj.getQtmzs());
        }
    }
    // 插入最后说明
    int rowCount = rowNum + tjs.size();
    HSSFRow rowEnd = sheet.createRow(rowCount);
    String sm = "说明：此表为模拟表。";
    HSSFCell hcsm = rowEnd.createCell(0);
    hcsm.setCellStyle(endStyle);
    hcsm.setCellValue(sm);
    // 合并单元格
    CellRangeAddress cellRangeAddress = new CellRangeAddress(rowCount, rowCount + 1, 0, 13);
    sheet.addMergedRegion(cellRangeAddress);
    setBorderStyle(HSSFCellStyle.BORDER_THIN, cellRangeAddress, sheet, wb);

    // 第六步，将文件存到指定位置
    try {
        FileOutputStream fout = new FileOutputStream(path);
        wb.write(fout);
        fout.close();
    } catch (Exception e) {
        e.printStackTrace();
    }

    return path;
 }
```

### 私有方法

```java
/**
  * 增加边框，用于合并后单元格
  *
  * @param border
  * @param region
  * @param sheet
  * @param wb
  */
private void setBorderStyle(short border, CellRangeAddress region, HSSFSheet sheet, HSSFWorkbook wb) {
    RegionUtil.setBorderBottom(border, region, sheet, wb);
    RegionUtil.setBorderTop(border, region, sheet, wb);
    RegionUtil.setBorderLeft(border, region, sheet, wb);
    RegionUtil.setBorderRight(border, region, sheet, wb);
}

/**
  * 创建表头样式
  *
  * @param wb
  * @return
  */
private HSSFCellStyle createHeaderCellStyle(HSSFWorkbook wb) {
    HSSFCellStyle cellStyle = wb.createCellStyle();
    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
    // 创建字体样式
    HSSFFont headerFont = wb.createFont();
    headerFont.setFontName("宋体");
    headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
    headerFont.setFontHeightInPoints((short) 18);
    cellStyle.setFont(headerFont);

    return cellStyle;
}

/**
  * 标题样式
  *
  * @param wb
  * @return
  */
private HSSFCellStyle createTitleCellStyle(HSSFWorkbook wb) {
    HSSFCellStyle titleStyle = wb.createCellStyle();
    titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
    titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
    titleStyle.setWrapText(true);
    // 创建字体样式
    HSSFFont titleFont = wb.createFont();
    titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
    titleFont.setFontName("宋体");
    titleFont.setFontHeightInPoints((short) 12);
    titleStyle.setFont(titleFont);

    return titleStyle;
}

/**
  * 内容样式
  *
  * @param wb
  * @return
  */
private HSSFCellStyle createContentCellStyle(HSSFWorkbook wb) {
    HSSFCellStyle cellStyle = wb.createCellStyle();
    cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
    cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
    cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
    cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
    cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
    cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
    // 创建字体样式
    HSSFFont headerFont = wb.createFont();
    headerFont.setFontName("宋体");
    headerFont.setFontHeightInPoints((short) 12);
    cellStyle.setFont(headerFont);

    return cellStyle;
}

/**
  * 标注样式
  *
  * @param wb
  * @return
  */
private HSSFCellStyle createEndCellStyle(HSSFWorkbook wb) {
    HSSFCellStyle endStyle = wb.createCellStyle();
    endStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);// 下边框
    endStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
    endStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//上下居中
    endStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
    endStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
    endStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
    // 创建字体样式
    HSSFFont endFont = wb.createFont();
    endFont.setColor(HSSFColor.RED.index);// 字体颜色
    endFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗
    endFont.setFontName("宋体");
    endFont.setFontHeightInPoints((short) 12);
    endStyle.setFont(endFont);

    return endStyle;
}   
```



### 参考

https://blog.csdn.net/weixin_43570367/article/details/103880612

