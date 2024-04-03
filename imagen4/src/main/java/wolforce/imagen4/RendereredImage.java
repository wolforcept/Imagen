package wolforce.imagen4;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

public class RendereredImage extends BufferedImage {

    enum Align {
        LEFT, CENTER, RIGHT
    }

    static {
        // GraphicsEnvironment graphicsEnvironment =
        // GraphicsEnvironment.getLocalGraphicsEnvironment();
        // Font[] fontNames = graphicsEnvironment.getAllFonts();
        // for (Font s : fontNames) {
        // System.out.println(s);
        // }
    }

    private final ImageReader imageReader;
    private final ImageWriter imageWriter;
    private final DataConfig config;

    private HashMap<String, String> imgAbrevs;
    private Graphics2D graphics;
    private Align fontAlignement = Align.LEFT;

    public RendereredImage(int i,
            String[] paramNames,
            String[] params,
            String scriptString,
            DataConfig dataConfig,
            ImageReader imageReader,
            ImageWriter imageWriter) {

        super(dataConfig.width, dataConfig.height, BufferedImage.TYPE_INT_ARGB);
        this.config = dataConfig;
        RendererWrapper.renderer = this;

        this.imageReader = imageReader;
        this.imageWriter = imageWriter;

        imgAbrevs = new HashMap<>();
        graphics = createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        CompilerConfiguration compilerConfig = new CompilerConfiguration();
        compilerConfig.setScriptBaseClass("wolforce.imagen4.RendererWrapper");
        Binding binding = new Binding();
        for (int paramIndex = 0; paramIndex < params.length; paramIndex++)
            // System.out.println(paramNames[paramIndex] + " -> " + params[paramIndex]);
            binding.setProperty(paramNames[paramIndex], params[paramIndex]);

        GroovyShell shell = new GroovyShell(RendereredImage.class.getClassLoader(), binding, compilerConfig);
        Script script = shell.parse(scriptString);
        script.run();

    }

    // GET

    public BufferedImage getImage(String name) {
        return imageReader.get(imgAbrevs.containsKey(name) ? imgAbrevs.get(name) : name);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    // SET

    public void setTextImagesDefaults(int x, int y, float s) {
        TextRenderer.textImagesDefaultX = x;
        TextRenderer.textImagesDefaultY = y;
        TextRenderer.textImagesDefaultScale = s;
    }

    public void addImageAbrev(String abrev, String path) {
        imgAbrevs.put(abrev, path);
    }

    public void setColor(String color) {
        graphics.setColor(Color.decode(color));
    }

    public void setFont(String fontName, int size, String attrs) {
        String[] attrParts = attrs.split(",");
        int style = Font.PLAIN;
        fontAlignement = Align.LEFT;
        for (String attr : attrParts) {
            switch (attr.toLowerCase()) {
                case "right":
                    fontAlignement = Align.RIGHT;
                    break;
                case "center":
                    fontAlignement = Align.CENTER;
                    break;
                case "bold":
                    style |= Font.BOLD;
                    break;
                case "italic":
                    style |= Font.ITALIC;
                    break;
            }
        }
        Font font = new Font(fontName, style, size);
        graphics.setFont(font);

    }

    // DRAW IMAGE

    public void drawImage(String imageId) {
        drawImage(imageId, 0, 0);
    }

    public void drawImage(Image image, int x, int y) {
        graphics.drawImage(image, x, y, null);
    }

    public void drawImage(Image image, int x, int y, int w, int h) {
        graphics.drawImage(image, x, y, w, h, null);
    }

    public void drawImage(String imageId, int x, int y) {
        graphics.drawImage(getImage(imageId), x, y, null);
    }

    public void drawImage(String imageId, int x, int y, int w, int h) {
        graphics.drawImage(getImage(imageId), x, y, w, h, null);
    }

    public void drawString(String s, int x, int y, int w) {
        new TextRenderer(this, graphics, config.isDebug, fontAlignement).renderText(s, x, y, w);
    }

}
