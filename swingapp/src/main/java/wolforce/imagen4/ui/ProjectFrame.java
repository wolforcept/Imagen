package wolforce.imagen4.ui;

import wolforce.imagen4.data.Config;
import wolforce.imagen4.data.Grid;
import wolforce.imagen4.data.GridsUtil;
import wolforce.imagen4.data.Watcher;
import wolforce.imagen4.io.ImageReader;
import wolforce.imagen4.io.ImageWriter;
import wolforce.imagen4.renderer.RendererWrapper;
import wolforce.imagen4.renderer.RendereredImage;
import wolforce.imagen4.scripting.ErrorTrace;
import wolforce.imagen4.scripting.Script;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProjectFrame {

    private static final Font monoFont = new Font("Courier New", Font.BOLD, 12);

    private final JFrame frame;
    private final JPanel statusBar;
    private final JProgressBar progressBar;
    private final JMenu gridsMenu;

    private final ImageReader imageReader;
    private final ImageWriter imageWriter;
    private final Watcher watcher;
    private final Displayer displayer;
    private final Config config;

    private final String scriptPath, gridsPath;
    private final List<Integer> subrenderIndexes = new ArrayList<>();
    private final List<Integer> rerenderIndexes = new ArrayList<>();
    private final File projectFile;
    private final Set<String> selectedGrids = new HashSet<>();

    //    private Grid currentGrid;
    private ErrorTrace lastTrace;
    private Script scriptData;
    private boolean isRendering;

    public ProjectFrame(String projectPath) {

        this.projectFile = new File(projectPath);

        String inputsPath = ImageReader.tryGetOrCreateInputFolder(projectPath);
        String outputsPath = ImageWriter.tryGetOrCreateOutputFolder(projectPath);
        scriptPath = Script.tryGetOrCreateScript(projectPath);
        gridsPath = GridsUtil.tryGetOrCreateGridsFolder(projectPath);

        config = new Config(projectPath);
        scriptData = new Script(scriptPath);

        imageReader = new ImageReader(inputsPath);
        imageWriter = new ImageWriter(outputsPath);

        watcher = new Watcher(projectPath, scriptPath, gridsPath, this);
        displayer = new Displayer();

        frame = new JFrame();
        updateFrameTitle();
        Image image = getLogoImage();
        if (image != null) frame.setIconImage(image);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                watcher.stop();
            }
        });

        JMenuBar menuBar = new JMenuBar();
        {
            {
                JMenu fileMenu = new JMenu("File");
                menuBar.add(fileMenu);

                {
                    JMenuItem item = new JMenuItem("Open Project Folder");
                    item.addActionListener(ev -> {
                        try {
                            Desktop.getDesktop().open(new File(projectPath));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    fileMenu.add(item);
                }

                {
                    JMenuItem item = new JMenuItem("Close Project");
                    item.addActionListener(ev -> {
                        frame.dispose();
                        new StartupFrame(false);
                    });
                    fileMenu.add(item);
                }

            }

            JMenu settingsMenu = new SettingsJMenu(config, "Settings");
            menuBar.add(settingsMenu);

            gridsMenu = new JMenu("Grids");
            menuBar.add(gridsMenu);

            menuBar.add(Box.createHorizontalGlue());

            var spacing = new Dimension(15, 15);
            menuBar.add(makeBarButton("Full Render", this::fullRender, getRenderImage()));
            menuBar.add(Box.createRigidArea(spacing));
            menuBar.add(makeBarButton("Sub Render", this::subRender, getSubrenderImage()));
            menuBar.add(Box.createRigidArea(spacing));
            menuBar.add(makeBarButton("Re-Render", this::reRender, getRerenderImage()));
            menuBar.add(Box.createRigidArea(spacing));
            menuBar.add(makeBarButton("Reload Images", this::reloadImagesAndRerender, getReloadImagesImage()));

            menuBar.add(Box.createHorizontalGlue());
            progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100);
            progressBar.setPreferredSize(new Dimension(75, progressBar.getPreferredSize().height));
            progressBar.setMaximumSize(new Dimension(75, progressBar.getMaximumSize().height));
            menuBar.add(progressBar);
        }
        frame.setJMenuBar(menuBar);

        statusBar = new JPanel();
        statusBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && lastTrace != null) {
                    lastTrace.showFrame(frame);
                }
            }
        });

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(displayer, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);

        frame.setContentPane(contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1000, 800));
        frame.pack();
        frame.setMinimumSize(new Dimension(50, 50));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateGridsMenu();
        var gridFiles = GridsUtil.getGridsFiles(gridsPath);
        if (!gridFiles.isEmpty()) {
            String path = gridFiles.get(0).getAbsolutePath();
            if (GridsUtil.exists(gridFiles, config.selectedGrid.get())) {
                path = config.selectedGrid.get();
            }
            selectGrid(new File(path), false);
        }

        if (config.selectedSubrenders.get().length() > 0) try {
            for (var part : config.selectedSubrenders.get().split(",")) {
                int selected = Integer.parseInt(part);
                subrenderIndexes.add(selected);
            }
        } catch (Exception e) {
            config.selectedSubrenders.set("");
        }
    }

    private JButton makeBarButton(String text, Runnable action, Image image) {
        JButton button = new JButton(text);
        button.setIcon(new ImageIcon(image));
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorder(new EmptyBorder(0, 2, 0, 5));

        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(Color.black);
        button.setBackground(Color.lightGray);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setOpaque(true);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setOpaque(false);
                button.repaint();
            }
        });

        button.addActionListener(ev -> action.run());
        return button;
    }

    private void updateFrameTitle() {

        var gridNames = selectedGrids.stream().map(x -> new File(x).getName()).reduce((a, b) -> a + "," + b).orElse("");

        frame.setTitle("Imagen4 - " + projectFile.getName() + " - " + gridNames);
    }

    private Image getLogoImage() {
        return getImage("logo64.png");
    }

    private Image getRenderImage() {
        return getImage("play20.png");
    }

    private Image getRerenderImage() {
        return getImage("replay20.png");
    }

    private Image getSubrenderImage() {
        return getImage("subplay20.png");
    }

    private Image getReloadImagesImage() {
        return getImage("reload20.png");
    }

    private Image getImage(String path) {
        try {
            var logoStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
            if (logoStream == null) throw new NullPointerException("logo stream is null");
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

        if (scriptData == null) {
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

    private Set<Integer> showIndexesToRenderOptionPane(String[] paramTypes, Grid grid) {
        var modal = new ModalGrid(paramTypes, grid, subrenderIndexes, scriptData.paramsNumber);
        modal.setLocationRelativeTo(frame);
        modal.setVisible(true);
        return modal.showAndGetIndexes();
    }

    //
    // RENDER
    //

    void fullRender() {
        if (isRendering) return;
        var grid = new Grid(selectedGrids, scriptData);
        List<Integer> allIndexes = IntStream.range(0, grid.rowsNumber).boxed().collect(Collectors.toList());
        startRender("full-render", grid, allIndexes);
    }

    void subRender() {
        if (isRendering) return;
        var grid = new Grid(selectedGrids, scriptData);
        Set<Integer> newIndexes = showIndexesToRenderOptionPane(scriptData.paramsNames, grid);
        if (newIndexes != null) {
            subrenderIndexes.clear();
            subrenderIndexes.addAll(newIndexes);
            config.selectedSubrenders.set(subrenderIndexes.stream().map(String::valueOf).reduce("", (x, y) -> x.equals("") ? y : x + "," + y));
            startRender("sub-render", grid, subrenderIndexes);
        }
    }

    void reloadImagesAndRerender() {
        if (isRendering) return;
        imageReader.clear();
        reRender();
    }

    void reRender() {
        startRender("re-render", new Grid(selectedGrids, scriptData), null);
    }

    void startRender(String typeOfRender, Grid grid, List<Integer> _indexesToRender) {
        if (!isRendering) {
            List<Integer> indexesToRender;
            if (_indexesToRender == null) {
                indexesToRender = new LinkedList<>(rerenderIndexes);
            } else {
                indexesToRender = new LinkedList<>(_indexesToRender);
                rerenderIndexes.clear();
                rerenderIndexes.addAll(indexesToRender);
            }
            updateStatusBar("Starting " + typeOfRender + "... ", false, indexesToRender.size(), null);
            setIsRendering(true);
            new Thread(() -> asyncRender(grid, indexesToRender)).start();
        }
    }

    private void setIsRendering(boolean isRendering) {
        this.isRendering = isRendering;
        Arrays.stream(frame.getJMenuBar().getComponents()).filter(x -> !(x instanceof JProgressBar)).forEach(x -> x.setEnabled(!isRendering));
    }

    void asyncRender(Grid grid, List<Integer> indexesToRender) {

        try {

            ErrorTrace trace = new ErrorTrace(grid);

            progressBar.setValue(0);
            UIManager.put("nimbusOrange", new Color(120, 198, 255));

            int nRows = indexesToRender.size();
            if (nRows == 0) {
                progressBar.setValue(0);
                UIManager.put("nimbusOrange", new Color(100, 100, 100));
                updateStatusBar("Nothing to render. ", false, indexesToRender.size(), null);
                setIsRendering(false);
                return;
            }

            LinkedList<BufferedImage> images = new LinkedList<>();
            {
                int i = 0;
                for (int imageIndex : indexesToRender) {
                    RendereredImage img = new RendereredImage(config.width.get(), config.height.get(), config.isDebug.get(), imageReader);
                    scriptData.run(new RendererWrapper(img), grid, imageIndex, trace);
//                    if (!localErrors.isEmpty()) nErrors++;
//                    errors.add(localErrors);

                    final int progressBarValue = 100 * i / nRows;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progressBarValue));

                    if (config.outputIndividual.get()) imageWriter.outputImage(grid.name + "_" + imageIndex, img);
                    images.add(img);
                    i++;
                }
            }

            trace.startingOutputs();

            {
                BufferedImage finalImage = generateCompoundImage(images);
                displayer.render(grid.hash, finalImage);
                if (config.outputCombined.get()) imageWriter.outputImage(grid.name, finalImage);
            }

            if (config.outputPages.get()) {
                var imgsToSplit = new LinkedList<>(images);
                int pageIndex = 0;
                do {
                    var imgs = new LinkedList<BufferedImage>();
                    for (int i = 0; i < 9; i++)
                        if (!imgsToSplit.isEmpty()) imgs.add(imgsToSplit.pop());

                    BufferedImage finalImage = generateCompoundImage(imgs);
                    String name = grid.name + "_page" + pageIndex;
                    imageWriter.outputImage(name, finalImage);

                    pageIndex++;
                } while (!imgsToSplit.isEmpty());
            }

            if (trace.hasErrors()) UIManager.put("nimbusOrange", new Color(191, 98, 4));
            else UIManager.put("nimbusOrange", new Color(39, 175, 13));

            SwingUtilities.invokeLater(() -> progressBar.setValue(100));

            lastTrace = trace;
            trace.finished();
            updateStatusBar("Finished render. ", false, indexesToRender.size(), trace.getNumberOfErrors());

        } catch (Exception e) {
            updateStatusBar("Unable to start render. ", false, null, null, e.getMessage());
        }
        setIsRendering(false);
    }

//    private void printAllErrors(List<List<String>> errors) {
//        int imageIndex = 0;
//        for (List<String> localErrors : errors) {
//            int finalImageIndex = imageIndex;
//            localErrors.forEach(x -> System.err.println("[IMAGEN NR " + finalImageIndex + "] " + x));
//            imageIndex++;
//        }
//    }

    public BufferedImage generateCompoundImage(List<BufferedImage> images) {
        int width = images.get(0).getWidth();
        int height = images.get(0).getHeight();
        int totalN = images.size();
        int nLines = 1;
        int nCols = config.outputCombinedWidth.get();
        if (nCols == 0) {
            boolean increasingCols = true;
            while (nLines * nCols < totalN) {
                if (increasingCols) nCols++;
                else if (nLines < 7) nLines++;
                increasingCols = !increasingCols;
            }
        } else {
            nLines = 1 + totalN / nCols;
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

    public void updateGridsMenu() {

        gridsMenu.removeAll();

        for (File file : GridsUtil.getGridsFiles(gridsPath)) {

            String fileName = file.getName();
            String filePath = file.getAbsolutePath();
            final String name = (selectedGrids.contains(filePath) ? "✓ " : "") //
                    + (fileName.endsWith(".csv") ? fileName.substring(0, fileName.length() - 4) : fileName);
            JMenuItem menu = new JMenuItem(name);
            menu.addActionListener(ev -> {
                var isShiftClick = (ev.getModifiers() & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK;
                selectGrid(file, isShiftClick);
                if (isShiftClick) gridsMenu.doClick();
            });
            gridsMenu.add(menu);

        }

        JMenuItem addNewGridMenu = new JMenuItem("+");
        addNewGridMenu.addActionListener(ev -> {
            GridsUtil.createNewGridFile(gridsPath, scriptData.paramsNames);
            updateGridsMenu();
        });

        gridsMenu.add(addNewGridMenu);
    }

    private void selectGrid(File file, boolean isAdd) {

        String path = file.getAbsolutePath();

        if (isAdd) {
            if (selectedGrids.contains(path)) {
                selectedGrids.remove(path);
            } else {
                selectedGrids.add(path);
            }
        } else {
            selectedGrids.clear();
            selectedGrids.add(path);
            config.selectedGrid.set(file.getAbsolutePath());
        }

        updateFrameTitle();
        updateStatusBar("Grid loaded.", true, new Grid(selectedGrids, scriptData).rowsNumber, null);

        updateGridsMenu();
    }

    public void watchedGridChanged(File file) {
        var path = file.getAbsolutePath();
        if (selectedGrids.contains(path)) {
            updateStatusBar("Grid reloaded.", true, new Grid(selectedGrids, scriptData).rowsNumber, null);
            updateGridsMenu();
            if (config.rerenderOnGridChange.get()) reRender();
        }
    }

    public void scriptChanged() {
        System.out.println("[Main] Script changed");
        scriptData = new Script(scriptPath);
        if (config.rerenderOnScriptChange.get()) reRender();
    }

}