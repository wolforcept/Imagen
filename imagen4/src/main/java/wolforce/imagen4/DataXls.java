package wolforce.imagen4;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class DataXls {

    public static List<String[]> load(String path) {

        List<String[]> rows = new LinkedList<>();
        try {
            Workbook workbook = Workbook.getWorkbook(new File(path));
            Sheet sheet = workbook.getSheet(0);

            for (int row = 0; row < sheet.getRows(); row++) {
                String[] rowContent = new String[sheet.getColumns()];
                for (int col = 0; col < rowContent.length; col++)
                    rowContent[col] = sheet.getCell(col, row).getContents();

                // String[] rowContent = (String[]) Arrays.stream(sheet.getRow(0)).map(cell ->
                // cell.getContents())
                // .toArray();
                rows.add(rowContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rows;
    }

    public static void save(String path, List<String[]> records) {

        if (records.size() == 0)
            return;

        System.out.println("-- SAVING --");

        try {
            WritableWorkbook workbook = Workbook.createWorkbook(new File(path));
            WritableSheet sheet = workbook.createSheet("data", 0);

            for (int col = 0; col < records.get(0).length; col++)
                sheet.insertColumn(col);

            int row = 0;
            for (String[] cells : records) {
                sheet.insertRow(row);
                for (int col = 0; col < cells.length; col++)
                    sheet.addCell(new Label(col, row, cells[col]));
                row++;
            }

            workbook.write();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // try (CSVWriter csvWriter = new CSVWriter(new FileWriter(path));) {
        // csvWriter.writeAll(records);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }

    public static String tryCreateCsv() {
        File file = new File("data.xls");
        if (!file.exists())
            save(file.getAbsolutePath(), new ArrayList<String[]>());
        return file.getAbsolutePath();
    }

}
