package wolforce.imagen4.data;

import wolforce.imagen4.scripting.Script;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Grid {

    private final List<String[]> rows;
    public final int rowsNumber;
    public final int hash;
    public final String name;

    public Grid(Set<String> paths, Script script) {
        this.hash = String.join("", paths).hashCode();

        rows = new LinkedList<>();

        String pathName = "";
        for (String path : paths) {
            var pathRows = loadFromCsvFile(path, script);
            pathName = new File(path).getName();
            pathName = pathName.substring(0, pathName.lastIndexOf("."));
            rows.addAll(pathRows);
        }
        this.name = paths.size() == 1 ? pathName : "combined";

//        this.path = path;
        rowsNumber = rows.size();
        System.out.println(name);
    }

    public String get(int rowIndex, int colIndex) {
        if (rowIndex >= rowsNumber) return "";
        String[] row = rows.get(rowIndex);
        if (colIndex > row.length - 1) return "";
        return row[colIndex];
    }

    public String[] get(int rowIndex) {
        if (rowIndex >= rowsNumber) return new String[]{};
        return rows.get(rowIndex);
    }

    public int getMaxCol() {
        int max = 0;
        for (var row : rows) {
            int n = row.length;
            if (n > max) max = n;
        }
        return max;
    }

    private boolean isValidLine(String[] line, Set<String> headers) {
        var isAllHeaders = true;
        var isAllBlank = true;
        for (String cell : line) {
            if (!headers.contains(cell.strip())) isAllHeaders = false;
            if (cell.strip().length() > 0) isAllBlank = false;
        }
        return !isAllHeaders && !isAllBlank;
    }

    private List<String[]> loadFromCsvFile(String path, Script script) {
        var headers = Arrays.stream(script.paramsNames).collect(Collectors.toSet());
        LinkedList<String[]> rows = GridsUtil.readGridFile(path);
        return rows.stream().filter(line -> this.isValidLine(line, headers)).toList();
    }

//    private List<String[]> loadFromXlsFile(String path, Script script) {
//
//        LinkedList<String[]> rows = new LinkedList<>();
//
//        try {
//            Workbook workbook = WorkbookFactory.create(new FileInputStream(path));
//            Sheet sheet = workbook.getSheetAt(0);
//            int nCols = script.paramsNumber;
//
//            Iterator<Row> rowIt = sheet.rowIterator();
//            while (rowIt.hasNext()) {
//                Row element = rowIt.next();
//
//                String[] rowContent = new String[nCols];
//                for (int col = 0; col < rowContent.length; col++) {
//                    try {
//                        rowContent[col] = element.getCell(col).getStringCellValue();
//                    } catch (Exception e) {
//                        rowContent[col] = "";
//                    }
//                }
//                rows.add(rowContent);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Set<String> headers = Arrays.stream(script.paramsNames).collect(Collectors.toSet());
//        return Collections.unmodifiableList(
//                rows.stream()
//                        .filter(line -> this.isValidLine(line, headers))
//                        .toList()
//        );
//    }
//
//    public static void save(String path, List<String[]> records) {
//
//        if (records.size() == 0) return;
//
//        System.out.println("[Data Xls] saving file");
//
//        try {
//
//            Workbook workbook = WorkbookFactory.create(true);
//            Sheet sheet = workbook.createSheet();
//
//            int rowIndex = 0;
//            for (String[] cells : records) {
//                Row row = sheet.createRow(rowIndex);
//                for (int col = 0; col < cells.length; col++) {
//                    row.createCell(col).setCellValue(cells[col]);
//                }
//                rowIndex++;
//            }
//
//            workbook.write(new FileOutputStream(path));
//            workbook.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    public static String[] getAllXls() {
//        File[] files = Objects.requireNonNull(new File(System.getProperty("user.dir")).listFiles());
//        File[] xlsFiles = Arrays.stream(files).filter(x -> x.getAbsolutePath().endsWith(".xls")).toArray(File[]::new);
//        if (xlsFiles.length == 0)
//            xlsFiles = new File[]{DataGrid.tryGetOrCreateXls()};
//        return Arrays.stream(xlsFiles)
//                .filter(x -> x.isFile() && x.getAbsolutePath().endsWith(".xls"))
//                .map(File::getAbsolutePath)
//                .toArray(String[]::new);
//    }
//
//    private static File tryGetOrCreateXls() {
//        File file = new File("data.xls");
//        if (!file.exists()) save(file.getAbsolutePath(), makeTestFile());
//        return file;
//    }
//
//    private static List<String[]> makeTestFile() {
//        var list = new LinkedList<String[]>();
//        list.add(new String[]{"test name 1", "1", "0.5", "TRUE", "Saturday"});
//        list.add(new String[]{"test name 2", "2", "-5.7", "FALSE", "Tuesday"});
//        list.add(new String[]{"name", "errorInt", "errorFloat", "errorBool", "errorEnum"});
//        return list;
//    }

}
