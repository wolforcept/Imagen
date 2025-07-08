package wolforce.imagen4.data;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Properties;

public class Config {

    public abstract class ConfigValue<T> {

        final String key;
        final T initialValue;

        T value;

        ConfigValue(String key, T initialValue) {
            this.key = key;
            this.initialValue = initialValue;
            this.value = initialValue;
            allConfigValues.add(this);
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
            Config.this.save();
        }

        public void save(Properties props) {
            props.put(key, String.valueOf(value));
        }

        public void load(Properties props) {
            String obj = props.getProperty(key);
            this.value = parse(obj);
        }

        abstract T parse(String value);

    }

    public abstract class MenuConfigValue<T> extends ConfigValue<T> {

        MenuConfigValue(String key, T initialValue) {
            super(key, initialValue);
        }

        public abstract JMenuItem makeMenuItem();
    }

    public class ConfigValueString extends ConfigValue<String> {

        ConfigValueString(String key, String initialValue) {
            super(key, initialValue);
        }

        @Override
        String parse(String value) {
            return value;
        }

    }

    public class ConfigValueBool extends MenuConfigValue<Boolean> {

        ConfigValueBool(String key, Boolean initialValue) {
            super(key, initialValue);
        }

        @Override
        Boolean parse(String value) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
                System.err.println("could not read boolean config value: " + value);
            }
            return initialValue;
        }

        @Override
        public JMenuItem makeMenuItem() {
            JMenuItem menuItem = new JMenuItem(getText());
            menuItem.addActionListener(ev -> {
                this.value = !this.value;
                menuItem.setText(getText());
                Config.this.save();
            });
            return menuItem;
        }

        String getText() {
            return key.replace("_", " ") + (value ? " ✓" : "");
        }
    }

    public class ConfigValueInt extends MenuConfigValue<Integer> {

        ConfigValueInt(String key, Integer initialValue) {
            super(key, initialValue);
        }

        @Override
        Integer parse(String value) {
            try {
                return Integer.parseInt(value);
            } catch (Exception e) {
                System.err.println("could not read integer config value: " + value);
            }
            return initialValue;
        }

        @Override
        public JMenuItem makeMenuItem() {
            JMenuItem menuItem = new JMenuItem(getText());
            menuItem.addActionListener(ev -> {
                var value = JOptionPane.showInputDialog("new value:");
                try {
                    this.value = Integer.parseInt(value);
                    menuItem.setText(getText());
                    Config.this.save();
                } catch (Exception ignored) {
                }
            });
            return menuItem;
        }

        String getText() {
            return key.replace("_", " ") + ": " + value;
        }
    }

    public LinkedList<ConfigValue<?>> allConfigValues = new LinkedList<>();
    public ConfigValueInt width = new ConfigValueInt("Width", 1000);
    public ConfigValueInt height = new ConfigValueInt("Height", 1000);
    public ConfigValueBool isDebug = new ConfigValueBool("Is_Debug", false);
    public ConfigValueBool outputIndividual = new ConfigValueBool("Output_Individual", false);
    public ConfigValueBool outputCombined = new ConfigValueBool("Output_Combined", false);
    public ConfigValueBool outputPages = new ConfigValueBool("Output_Pages", false);
    public ConfigValueInt outputCombinedWidth = new ConfigValueInt("Output_Combined_Width", 0);
    public ConfigValueBool rerenderOnScriptChange = new ConfigValueBool("Rerender on Script Change", false);
    public ConfigValueBool rerenderOnGridChange = new ConfigValueBool("Rerender on Grid Change", false);
    public ConfigValueString selectedGrid = new ConfigValueString("selectedGrid", "");
    public ConfigValueString selectedSubrenders = new ConfigValueString("selectedSubrenders", "");

    public final MenuConfigValue<?>[] menuConfigValues = new MenuConfigValue[]{ //
            width, //
            height, //
            outputIndividual, //
            outputCombined, //
            outputPages, //
            outputCombinedWidth, //
            rerenderOnScriptChange, //
            rerenderOnGridChange, //
            isDebug //
    };

    private final File file;

    public Config(String projectPath) {
        file = Paths.get(projectPath, "settings.config").toFile();
        if (!file.exists()) save();
        load();
        save();
    }

    private void load() {

        try {
            var reader = new FileReader(file);
            Properties props = new Properties();
            props.load(reader);
            for (ConfigValue<?> configValue : allConfigValues)
                configValue.load(props);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void save() {

        try {
            var writer = new FileWriter(file);
            Properties props = new Properties();
            for (ConfigValue<?> configValue : allConfigValues)
                configValue.save(props);
            props.store(writer, "Imagen config");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
