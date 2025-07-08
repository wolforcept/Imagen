package wolforce.imagen4.ui;

import wolforce.imagen4.data.Config;

import javax.swing.JMenu;

public class SettingsJMenu extends JMenu {

    public SettingsJMenu(Config config, String text) {
        super(text);

        for (var val : config.menuConfigValues)
            add(val.makeMenuItem());

    }


}
