package me.about.widget.excel.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 单元格合并配置
 *
 * @author: hugo.zxh
 * @date: 2022/01/16 15:21
 * @description:
 */
@Setter
@Getter
public class ExcelCellMergeParams {

    private int startRow;

    private int endRow;

    private int startCol;

    private int endCol;

    public ExcelCellMergeParams(int startRow, int endRow, int startCol, int endCol){
        this.startRow = startRow;
        this.endRow = endRow;
        this.startCol = startCol;
        this.endCol = endCol;
    }

}
