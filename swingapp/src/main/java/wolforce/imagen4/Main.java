package wolforce.imagen4;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Main {

    private static class WrappedCombo extends JPanel {
        private JComboBox<String> comboBox = new JComboBox<>();

        WrappedCombo(String[] data) {
            this.remake(data);
        }

        void remake(String[] data) {

            String prevSelectedValue = null;
            int prevSelectedIndex = -1;
            if (comboBox != null) {
                prevSelectedValue = (String) comboBox.getSelectedItem();
                prevSelectedIndex = comboBox.getSelectedIndex();
            }

            comboBox = new JComboBox<>(data);
            comboBox.addItem("");
            comboBox.setSelectedItem("");

            this.removeAll();
            this.add(comboBox, BorderLayout.CENTER);
            this.validate();

            if (prevSelectedIndex >= 0)
                comboBox.setSelectedIndex(prevSelectedIndex);
            if (prevSelectedValue != null)
                comboBox.setSelectedItem(prevSelectedValue);
        }

        int getSelectedIndex() {
            return comboBox.getSelectedIndex();
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            comboBox.setEnabled(enabled);
        }

        public void addActionListener(Consumer<Integer> consumer) {
            comboBox.addActionListener(x -> consumer.accept(comboBox.getSelectedIndex()));
        }

        public void setSelected(String path) {
            comboBox.setSelectedItem(path);
        }
    }

    private static final Font monoFont = new Font("Courier New", Font.BOLD, 12);

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
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Main();
    }

    final JFrame frame;
    final WrappedCombo sortCombo;
    final Container contentPane;
    final JPanel statusBar;
    final JCheckBox shouldRenderPagesCheckbox;
    final JProgressBar progressBar;
    final JMenu gridsMenu;

    final ImageReader imageReader;
    final ImageWriter imageWriter;
    final Watcher watcher;
    final Displayer displayer;
    private final List<Integer> indexesToRender = new ArrayList<>();

    final String configPath, scriptPath, inputsPath, outputsPath, gridsPath;
    // final String[] xlsPaths;

    private final DataConfig dataConfig;
    private DataGrid selectedDataGrid;

    private Script scriptData;

    private Main() {

        inputsPath = ImageReader.tryGetOrCreateInputFolder();
        outputsPath = ImageWriter.tryGetOrCreateOutputFolder();
        configPath = DataConfig.tryGetOrCreateConfig();
        scriptPath = Script.tryGetOrCreateScript();
        gridsPath = Grids.tryGetOrCreateGridsFolder();
        // xlsPaths = DataGrid.getAllXls();

        dataConfig = DataConfig.load(configPath);
        scriptData = new Script(scriptPath);

        imageReader = new ImageReader(inputsPath);
        imageWriter = new ImageWriter(outputsPath);

        watcher = new Watcher(scriptPath, gridsPath, this);
        displayer = new Displayer();

        frame = new JFrame();
        updateFrameTitle(null);
        Image image = getLogoImage();
        if (image != null)
            frame.setIconImage(image);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                watcher.stop();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        {
            //menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));

            JMenu fileMenu = new JMenu("File");
            JMenuItem openFolderMenuItem = new JMenuItem("Open Project Folder");
            openFolderMenuItem.addActionListener(ev -> {
                try {
                    Desktop.getDesktop().open(new File(new File("").getAbsolutePath()));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            fileMenu.add(openFolderMenuItem);
            menuBar.add(fileMenu);

            gridsMenu = new JMenu("Grids");
            menuBar.add(gridsMenu);

            JToolBar toolBar = new JToolBar();
//        menuBar.add(toolBar);


//        buttonsPanel = new JPanel();

            menuBar.add(Box.createHorizontalGlue());
            progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
            menuBar.add(progressBar);

            sortCombo = new WrappedCombo(scriptData.paramsNames);

            menuBar.add(Box.createHorizontalGlue());

            menuBar.add(makeBarButton(ev -> fullRender(), getRenderImage()));
            menuBar.add(makeBarButton(ev -> subRender(), getSubrenderImage()));
            menuBar.add(makeBarButton(ev -> reRender(), getRerenderImage()));
            menuBar.add(makeBarButton(ev -> reloadImagesAndRerender(), getReloadImagesImage()));

            shouldRenderPagesCheckbox = new JCheckBox();
            menuBar.add(shouldRenderPagesCheckbox);
        }
        frame.setJMenuBar(menuBar);

        statusBar = new JPanel();

        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(displayer, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        gridsChanged();
    }

    private JButton makeBarButton(ActionListener actionListener, Image image) {
        JButton button = new JButton();
        button.setBorderPainted(false);
//        renderMenuButton.setFocusPainted(false);
        button.setContentAreaFilled(false);
//        renderMenuButton.setBorderPainted(false);
        button.setBorder(null);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setIcon(new ImageIcon(image));
        button.addActionListener(actionListener);
//        renderMenuButton.setIconTextGap(0);
        return button;
    }

    private void resizeMenuItem(JMenuItem item) {
        var dim = item.getPreferredSize();
        item.setPreferredSize(new Dimension(50, dim.height));
    }

    private void updateFrameTitle(String subtitle) {
        frame.setTitle("Imagen4" + (subtitle != null ? (" - " + subtitle) : ""));
    }

    private Image getLogoImage() {
        return getImage("logo64.png");
    }

    private Image getRenderImage() {
        return getImage("play16.png");
    }

    private Image getRerenderImage() {
        return getImage("halfplay16.png");
    }

    private Image getSubrenderImage() {
        return getImage("replay16.png");
    }

    private Image getReloadImagesImage() {
        return getImage("reload16.png");
    }

    private Image getImage(String path) {
        try {
            var logoStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            if (logoStream == null)
                throw new NullPointerException("logo stream is null");
            return ImageIO.read(logoStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateStatusBar(String statusText, boolean writeParams, Integer nRows, Integer nErrors) {
        updateStatusBar(statusText, writeParams, nRows, nErrors, null);
    }

    private void updateStatusBar(String statusText, boolean writeParams, Integer nRows, Integer nErrors, String error) {
        statusBar.removeAll();
        var boxLayout = new BoxLayout(statusBar, BoxLayout.LINE_AXIS);
        statusBar.setLayout(boxLayout);

        if (dataConfig == null) {
            statusBar.add(statusLabel("config problem"));
        } else if (scriptData == null) {
            statusBar.add(statusLabel("script problem"));
        }

        statusBar.add(statusLabel(statusText));

        if (writeParams) {
            statusBar.add(statusLabel("Params: " + scriptData.getParamsString()));
        }

        if (nRows != null) {
            statusBar.add(statusLabel("Rows: " + nRows));
        }

        if (nErrors != null) {
            statusBar.add(statusLabel("Errors: " + nErrors, new Color(150, 0, 0)));
        }

        if (error != null) {
            statusBar.add(statusLabel(error, new Color(150, 0, 0)));
        }

        statusBar.repaint();
        statusBar.revalidate();
        statusBar.getParent().revalidate();
        statusBar.getParent().repaint();
    }

    private JLabel statusLabel(String text) {
        return statusLabel(text, Color.WHITE);
    }

    private JLabel statusLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 25));
        label.setFont(monoFont);
        return label;
    }

    private Set<Integer> showIndexesToRenderOptionPane(String[] paramTypes, DataGrid dataGrid) {
        var modal = new ModalGrid(paramTypes, dataGrid, indexesToRender, scriptData.paramsNumber);
        modal.setLocationRelativeTo(frame);
        modal.setVisible(true);
        return modal.showAndGetIndexes();
    }

    //
    // RENDER
    //

    private boolean canStartRender() {
        if (selectedDataGrid == null) {
            updateStatusBar("Could not start render.", false, null, null, "No Grid selected.");
            return false;
        }
        return true;
    }

    void fullRender() {
        if (!canStartRender()) return;
        indexesToRender.clear();
        for (int i = 0; i < selectedDataGrid.rowsNumber; i++)
            indexesToRender.add(i);
        startRender("full-render", selectedDataGrid);
    }

    void subRender() {
        if (!canStartRender()) return;
        Set<Integer> newIndexes = showIndexesToRenderOptionPane(scriptData.paramsNames, selectedDataGrid);
        if (newIndexes != null) {
            indexesToRender.clear();
            indexesToRender.addAll(newIndexes);
            startRender("sub-render", selectedDataGrid);
        }
    }

    void reloadImagesAndRerender() {
        imageReader.clear();
        reRender();
    }

    void reRender() {
        if (!canStartRender()) return;
        startRender("re-render", selectedDataGrid);
    }

    void startRender(String typeOfRender, DataGrid dataGrid) {
        updateStatusBar("Starting " + typeOfRender + "... ", false, indexesToRender.size(), null);
        setButtonsEnabled(false);
        new Thread(() -> asyncRender(dataGrid)).start();
    }

    private void setButtonsEnabled(boolean isEnabled) {
        Arrays.stream(frame.getJMenuBar().getComponents()).forEach(x -> x.setEnabled(isEnabled));
    }

    void asyncRender(DataGrid dataGrid) {

        try {
            int nErrors = 0;
            progressBar.setValue(0);
            UIManager.put("nimbusOrange", new Color(120, 198, 255));

            int nRows = indexesToRender.size();
            if (nRows == 0) {
                progressBar.setValue(0);
                UIManager.put("nimbusOrange", new Color(100, 100, 100));
                updateStatusBar("Nothing to render. ", false, indexesToRender.size(), null);
                setButtonsEnabled(true);
                return;
            }
            String sortParamValue = scriptData.getParamName(sortCombo.getSelectedIndex());

            List<List<String>> errors = new LinkedList<>();
            HashMap<String, LinkedList<BufferedImage>> images = new HashMap<>();
            {
                int i = 0;
                for (int imageIndex : indexesToRender) {
                    RendereredImage img = new RendereredImage(dataConfig, imageReader);
                    List<String> localErrors = scriptData.run(new RendererWrapper(img), dataGrid, imageIndex);
                    if (!localErrors.isEmpty())
                        nErrors++;
                    errors.add(localErrors);

                    final int progressBarValue = 100 * i / nRows;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progressBarValue));

                    imageWriter.outputImage(dataGrid.name + "_" + imageIndex, img);
                    if (!images.containsKey(sortParamValue))
                        images.put(sortParamValue, new LinkedList<>());
                    images.get(sortParamValue).add(img);
                    i++;
                }
            }

            int imageIndex = 0;
            for (List<String> localErrors : errors) {
                int finalImageIndex = imageIndex;
                localErrors.forEach(x -> System.err.println("[IMAGEN NR " + finalImageIndex + "] " + x));
                imageIndex++;
            }

            var tabsToStay = images.keySet().stream().map(tab -> tab.equals("") ? " - default - " : tab)
                    .collect(Collectors.toSet());
            for (var tabName : displayer.getTabNames()) {
                if (!tabsToStay.contains(tabName))
                    displayer.clearTab(tabName);
            }

            for (String tab : images.keySet()) {
                {
                    String name = tab.equals("") ? " - default - " : tab;
                    BufferedImage finalImage = generateCompoundImage(images.get(tab));
                    displayer.render(finalImage, name);
                    imageWriter.outputImage(dataGrid.name + "_" + tab, finalImage);
                }

                if (shouldRenderPagesCheckbox.isSelected()) {
                    var imgsToSplit = images.get(tab);
                    int pageIndex = 0;
                    do {
                        var imgs = new LinkedList<BufferedImage>();
                        for (int i = 0; i < 9; i++)
                            imgs.add(imgsToSplit.pop());
                        String name = tab.equals("") ? " - default - " : tab;
                        name += "_page" + pageIndex;
                        BufferedImage finalImage = generateCompoundImage(imgs);
                        displayer.render(finalImage, name);
                        imageWriter.outputImage(dataGrid.name + "_" + tab, finalImage);
                        pageIndex++;
                    } while (!imgsToSplit.isEmpty());
                }

            }

            if (nErrors > 0)
                UIManager.put("nimbusOrange", new Color(191, 98, 4));
            else
                UIManager.put("nimbusOrange", new Color(39, 175, 13));
            progressBar.setValue(100);

            updateStatusBar("Finished render. ", false, indexesToRender.size(), nErrors);

        } catch (Exception e) {
            updateStatusBar("Unable to start render. ", false, null, null, e.getMessage());
        }
        setButtonsEnabled(true);
    }

    public BufferedImage generateCompoundImage(List<BufferedImage> images) {
        int width = images.get(0).getWidth();
        int height = images.get(0).getHeight();
        int totalN = images.size();
        int nLines = 1;
        int nCols = 1;
        boolean increasingCols = true;
        while (nLines * nCols < totalN) {
            if (increasingCols)
                nCols++;
            else if (nLines < 7)
                nLines++;
            increasingCols = !increasingCols;
        }
        System.out.println("[Main] Writing composite image: " + nCols + " x " + nLines);
        BufferedImage compImg = new BufferedImage(width * nCols, height * nLines, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = compImg.createGraphics();
        int x = 0;
        int y = 0;
        for (BufferedImage subImg : images) {
            graphics.drawImage(subImg, x, y, null);
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

    public void gridsChanged() {

        gridsMenu.removeAll();

        for (File file : Grids.getGridsFiles(gridsPath)) {

            String fileName = file.getName();
            final String name = fileName.endsWith(".csv") ? fileName.substring(0, fileName.length() - 4) : fileName;
            JMenuItem menu = new JMenuItem(name);
            final String absolutePath = file.getAbsolutePath();
            menu.addActionListener(ev -> {
                selectedDataGrid = new DataGrid(absolutePath, scriptData);
                updateFrameTitle(name);
                updateStatusBar("Script loaded. XLS loaded.", true, selectedDataGrid.rowsNumber, null);
            });
            gridsMenu.add(menu);

        }

        JMenuItem addNewGridMenu = new JMenuItem("+");
        addNewGridMenu.addActionListener(ev -> {
            Grids.createNewGridFile(gridsPath);
            gridsChanged();
        });
        gridsMenu.add(addNewGridMenu);

    }

    public void scriptChanged() {
        System.out.println("[Main] Script changed");
        scriptData = new Script(scriptPath);
        reRender();
    }

}