package wolforce.imagen4;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Grids {

    public static void createNewGridFile(String gridsPath) {
        try {

//            Reader reader = new FileReader("example.csv");
//            CSVParser csvParser =  CSVParser.builder()
//                    .setFormat(CSVFormat.DEFAULT)
//                    .setReader(reader)
//                    .get();
////            (reader, CSVFormat.DEFAULT
////                    .withFirstRecordAsHeader()
////                    .withIgnoreHeaderCase()
////                    .withTrim());
//
//            for (CSVRecord record : csvParser) {
//                String name = record.get("Name");
//                String email = record.get("Email");
//                System.out.println("Name: " + name + ", Email: " + email);
//            }
//
//            csvParser.close();
//            reader.close();

            int i = 0;
            File file;
            do {
                file = Paths.get(gridsPath, "grid" + i + ".csv").toFile();
                i++;
            } while (file.exists());

            Writer writer = new FileWriter(file);
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
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

//    static List<String[]> load(String path) {
//        try {
//            List<String[]> records = new ArrayList<String[]>();
//            try (CSVReader csvReader = new CSVReader(new FileReader(path));) {
//                String[] values = null;
//                while ((values = csvReader.readNext()) != null) {
//                    records.add(values);
//                }
//            }
//            return records;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new LinkedList<>();
//        }
//    }
//
//    public static void save(String path, List<String[]> records) {
//        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(path));) {
//            csvWriter.writeAll(records);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String tryCreateCsv() {
//        File file = new File("data.csv");
//        if (!file.exists())
//            save(file.getAbsolutePath(), new ArrayList<String[]>());
//        return file.getAbsolutePath();
//    }


    //

    public static String tryGetOrCreateGridsFolder() {
        File file = new File("grids");
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
}
