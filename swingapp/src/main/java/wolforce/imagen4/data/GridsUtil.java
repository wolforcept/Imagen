package wolforce.imagen4.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;

public class GridsUtil {

    public static void createNewGridFile(String gridsPath, String[] paramNames) {
        try {
            int i = 0;
            File file;
            do {
                file = Paths.get(gridsPath, "grid" + i + ".csv").toFile();
                i++;
            } while (file.exists());

            Writer writer = new FileWriter(file);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            if (paramNames != null)
                printer.printRecord((Object[]) paramNames);
            else
                printer.printRecords(makeTestFile());
            printer.flush();
            printer.close();
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("Could not create new Grid file");
        }
    }

    public static LinkedList<String[]> readGridFile(String path) {
        LinkedList<String[]> list = new LinkedList<>();
        try {

            Reader reader = new FileReader(path);
            CSVParser csvParser = CSVParser.builder()
                    .setFormat(CSVFormat.DEFAULT)
                    .setReader(reader)
                    .get();

            for (CSVRecord record : csvParser) {
                String[] arr = record.stream().toArray(String[]::new);
                list.add(arr);
            }

            csvParser.close();
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException("Could not read Grid file");
        }

        return list;
    }

    private static LinkedList<String[]> makeTestFile() {
        var list = new LinkedList<String[]>();
        list.add(new String[]{"test name 1", "1", "0.5", "TRUE", "Saturday"});
        list.add(new String[]{"test name 2", "2", "-5.7", "FALSE", "Tuesday"});
        list.add(new String[]{"name", "errorInt", "errorFloat", "errorBool", "errorEnum"});
        return list;
    }

    //

    public static String tryGetOrCreateGridsFolder(String projectPath) {
        File file = Paths.get(projectPath, "grids").toFile();
        if (!file.exists()) if (!file.mkdir()) throw new RuntimeException("could not create Grids folder");
        return file.getAbsolutePath();
    }

    public static LinkedList<File> getGridsFiles(String gridsPath) {

        LinkedList<File> list = new LinkedList<>();

        File folder = new File(gridsPath);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) return list;

        for (File f : listOfFiles) {
            if (f.isFile()) list.add(f);
        }

        return list;
    }

    public static boolean exists(LinkedList<File> gridFiles, String path) {
        return gridFiles.stream().anyMatch(
                x -> x.getAbsolutePath().equals(path)
        );
    }
}
