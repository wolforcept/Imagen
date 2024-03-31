package wolforce.imagen4;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class DataCsv {

    public static List<String[]> load(String path) {
        try {
            List<String[]> records = new ArrayList<String[]>();
            try (CSVReader csvReader = new CSVReader(new FileReader(path));) {
                String[] values = null;
                while ((values = csvReader.readNext()) != null) {
                    records.add(values);
                }
            }
            return records;
        } catch (Exception e) {
            e.printStackTrace();
            return new LinkedList<>();
        }
    }

    public static void save(String path, List<String[]> records) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(path));) {
            csvWriter.writeAll(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String tryCreateCsv() {
        File file = new File("data.csv");
        if (!file.exists())
            save(file.getAbsolutePath(), new ArrayList<String[]>());
        return file.getAbsolutePath();
    }

}
