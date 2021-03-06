package io.robe.convert.excel.parsers;

import io.robe.convert.excel.CellGenerateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hasanmumin on 27/10/2016.
 */
public class ParseBoolTest {

    private ParseBool parseBool = new ParseBool();

    @Test
    public void parse() throws Exception {

        Boolean actual = parseBool.parse(null, null);
        assertEquals(null, actual);

        actual = parseBool.parse("true", null);

        assertEquals(true, actual);

        actual = parseBool.parse("false", null);

        assertEquals(false, actual);
    }

    @Test
    public void setCell() throws Exception {

        Cell cell = CellGenerateUtil.create();

        parseBool.setCell(null, cell, null);
        assertEquals("", cell.getStringCellValue());

        parseBool.setCell(true, cell, null);
        assertEquals(true, cell.getBooleanCellValue());
    }

}