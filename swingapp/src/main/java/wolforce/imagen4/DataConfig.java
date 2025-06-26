package wolforce.imagen4;

import java.io.*;
import java.util.Properties;

public class DataConfig {

    public final int width;
    public final int height;
    public final boolean isDebug;
//    public final boolean isList;

    public DataConfig() {
        this.width = 1000;
        this.height = 1000;
        this.isDebug = false;
//        this.isList = true;
    }

    public DataConfig(Properties p) {

        this.width = tryGetProperty(p, "width", x -> Integer.parseInt(x), 1000);
        this.height = tryGetProperty(p, "height", x -> Integer.parseInt(x), 1000);
        this.isDebug = tryGetProperty(p, "isDebug", x -> x.equals("true"), false);
//        this.isList = tryGetProperty(p, "isList", x -> x.equals("true"), true);

    }

    interface Parser<T> {
        T parse(String s);
    }

    private <T> T tryGetProperty(Properties p, String name, Parser<T> parser, T defaultValue) {
        try {
            return parser.parse(p.getProperty(name));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    //
    // STATICS
    //

    private static final DataConfig INITIAL = new DataConfig();

    public static DataConfig load(String path) {
        Properties p = new Properties();
        try (InputStream is = new FileInputStream(new File(path))) {
            p.load(is);
            return new DataConfig(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return INITIAL;
    }

    public static String tryGetOrCreateConfig() {
        File file = new File("config.properties");
        if (!file.exists())
            createNewConfigFile(file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    private static void createNewConfigFile(String path) {
        String str = "width=1000\nheight=1000\noutput=output\nisDebug=false";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path));) {
            writer.write(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
