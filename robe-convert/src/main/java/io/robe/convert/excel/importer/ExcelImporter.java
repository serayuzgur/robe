package io.robe.convert.excel.importer;

import io.robe.convert.IsImporter;
import io.robe.convert.OnItemHandler;
import io.robe.convert.excel.parsers.Parsers;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

public abstract class ExcelImporter extends IsImporter {

    public <T> List<T> importStream(Class clazz, InputStream inputStream, boolean isFirstRowHeader, Workbook workbook) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        final List<T> entries = new LinkedList<T>();

        OnItemHandler<T> handler = new OnItemHandler<T>() {
            @Override
            public void onItem(T entry) {
                entries.add((T) entry);
            }
        };

        this.importStream(clazz, inputStream, isFirstRowHeader, workbook, handler);

        return entries;
    }

    public <T> void importStream(Class clazz, InputStream inputStream, boolean isFirstRowHeader, Workbook workbook, OnItemHandler handler) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        Collection<Field> fields = getFields(clazz);

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        if (isFirstRowHeader && rowIterator.hasNext())
            rowIterator.next();

        while (rowIterator.hasNext()) {
            T entry = (T) clazz.newInstance();
            fields.iterator().next();
            Row row = rowIterator.next();

            int cellCount = 0;
            for (Field field : fields) {
                Cell cell = row.getCell(cellCount++);
                try {
                    if (cell != null || !cell.toString().trim().equals("")) {
                        Object cellData = Parsers.valueOf(field.getType().getSimpleName().toUpperCase(Locale.ENGLISH)).getParser().parse(cell.toString(), field);
                        boolean acc = field.isAccessible();
                        field.setAccessible(true);
                        field.set(entry, cellData);
                        field.setAccessible(acc);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            handler.onItem(entry);
        }

    }

}
