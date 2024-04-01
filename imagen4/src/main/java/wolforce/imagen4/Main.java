package wolforce.imagen4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        UIManager.put("control", new Color(128, 128, 128));
        UIManager.put("info", new Color(128, 128, 128));
        UIManager.put("nimbusBase", new Color(18, 30, 49));
        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
        UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusGreen", new Color(176, 179, 50));
        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
        UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
        UIManager.put("nimbusOrange", new Color(191, 98, 4));
        UIManager.put("nimbusRed", new Color(169, 46, 34));
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
        UIManager.put("text", new Color(230, 230, 230));
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Main();
    }

    final JFrame frame;
    final JPanel contentPane, tableWrapper;

    final ImageReader imageReader = new ImageReader();
    final ImageWriter imageWriter = new ImageWriter();
    final Watcher watcher;
    final Displayer displayer;
    final String configPath, scriptPath, csvPath, inputsPath, outputsPath;
    private long paramTypesHash = 0;
    private String[] paramNames;
    private String[] paramTypes;
    private ParamTable table;

    private Main() {

        inputsPath = imageReader.tryCreateInputFolder();
        outputsPath = ImageWriter.tryCreateOutputFolder();
        configPath = DataConfig.tryCreateConfig();
        scriptPath = DataScript.tryCreateScript();
        csvPath = DataXls.tryCreateCsv();

        watcher = new Watcher(scriptPath, this);
        displayer = new Displayer();

        reloadDataTypes();

        frame = new JFrame("Imagen4");
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                watcher.stop();
            }
        });

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(new JButton(new AbstractAction("Add Row") {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.addNewRow(true);
            }
        }));
        buttonsPanel.add(new JButton(new AbstractAction("Render") {
            @Override
            public void actionPerformed(ActionEvent e) {
                rerender(true);
            }

        }));

        tableWrapper = new JPanel();
        tableWrapper.setLayout(new BorderLayout());
        remakeTable();

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(tableWrapper, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    private boolean reloadDataTypes() {
        String loadedDataTypes = DataScript.loadFirstLine(scriptPath);
        long newHash = loadedDataTypes.hashCode();
        if (newHash == paramTypesHash)
            return false;
        paramTypesHash = newHash;
        String[] typesRaw = loadedDataTypes.substring(2).strip().split(",");
        paramNames = new String[typesRaw.length];
        paramTypes = new String[typesRaw.length];
        for (int i = 0; i < typesRaw.length; i++) {
            String[] parts = typesRaw[i].strip().split(" ");
            paramTypes[i] = parts[0].strip();
            paramNames[i] = parts[1].strip();
        }
        return true;
    }

    private void remakeTable() {
        table = new ParamTable(this, paramNames, paramTypes, DataXls.load(csvPath));

        tableWrapper.removeAll();
        tableWrapper.add(table.get(), BorderLayout.CENTER);
        tableWrapper.setPreferredSize(table.get().getPreferredSize());
        tableWrapper.validate();
    }

    void rerender(boolean isNewRender) {
        List<String[]> rows = table.getSelectedRows(isNewRender);
        if (rows.size() == 0)
            return;
        render(rows, true);
    }

    void render(List<String[]> rows, boolean isNewRender) {

        if (rows.size() == 0)
            return;

        if (!isNewRender && !displayer.isVisible())
            return;

        String scriptString = DataScript.load(scriptPath);
        DataConfig dataConfig = DataConfig.load(configPath);

        LinkedList<BufferedImage> images = new LinkedList<>();
        int i = 0;
        for (String[] params : rows) {
            RendereredImage img = new RendereredImage(i, paramNames, params, scriptString, dataConfig, imageReader,
                    imageWriter);
            i++;

            imageWriter.outputImage("image_" + i, img);
            images.add(img);
        }
        String name = "images";
        BufferedImage finalImage = generateCompoundImage(name, images);

        displayer.render(finalImage, name);
        if (isNewRender)
            displayer.show();
    }

    public BufferedImage generateCompoundImage(String name, List<BufferedImage> images) {
        int width = images.get(0).getWidth();
        int height = images.get(0).getHeight();
        int totalN = images.size();
        int nLines = 1;
        int nCols = 1;
        boolean increasingCols = true;
        while (nLines * nCols < totalN) {
            if (increasingCols)
                nCols++;
            else {
                if (nLines < 7)
                    nLines++;
            }
            increasingCols = !increasingCols;
        }
        System.out.println("|  > Size: " + nCols + " x " + nLines);
        BufferedImage compImg = new BufferedImage(width * nCols, height * nLines, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = compImg.createGraphics();
        int x = 0;
        int y = 0;
        for (BufferedImage subimg : images) {
            graphics.drawImage(subimg, x, y, null);
            x += width;
            if (x >= width * nCols) {
                x = 0;
                y += height;
            }
        }
        // if (Config.OUTPUT_COMPOUND_IMAGES)
        // imageWriter.outputImage(runName, compImg);
        // if (Config.DISPLAY_COMPOUND_IMAGES)
        return compImg;
    }

    public void scriptChanged() {
        try {
            System.out.println("script changed");
            boolean hasNewTypes = reloadDataTypes();
            if (hasNewTypes)
                remakeTable();
            String script = DataScript.load(scriptPath);
            System.out.println(script);

            rerender(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public void save(List<String[]> data) {
        DataXls.save(csvPath, data);
        this.render(data, false);
    }

}