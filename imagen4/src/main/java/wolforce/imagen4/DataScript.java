package wolforce.imagen4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DataScript {

    public static String load(String path) {
        try (Scanner scanner = new Scanner(new File(path));) {
            return scanner.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String loadFirstLine(String path) {
        try (Scanner scanner = new Scanner(new File(path));) {
            return scanner.nextLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void writeNewScript(String path) throws IOException {
        String str = "// String type, String name";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path));) {
            writer.write(str);
            writer.close();
        }

    }

    public static String tryCreateScript() {
        File file = new File("script.groovy");
        try {
            if (!file.exists())
                writeNewScript(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
