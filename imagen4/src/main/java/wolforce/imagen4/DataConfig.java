package wolforce.imagen4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public record DataConfig(int width, int height) {

    private static final DataConfig INITIAL = new DataConfig(1000, 1000);

    public static DataConfig load(String path) {
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(new File(path))) {
            p.load(is);
            return new DataConfig(
                    Integer.parseInt(p.getProperty("width")),
                    Integer.parseInt(p.getProperty("height"))//
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return INITIAL;
    }

    public static String tryCreateConfig() {
        File file = new File("config.properties");
        if (!file.exists())
            createNewConfigFile(file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    private static void createNewConfigFile(String path) {
        String str = "width=1000\nheight=1000\noutput=output";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path));) {
            writer.write(str);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
