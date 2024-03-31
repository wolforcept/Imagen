package wolforce.imagen4;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final String imageRegex = "\\{[\\w-:\\.]+\\}";

    private final ImageReader imageReader;
    private final ImageWriter imageWriter;

    private HashMap<String, String> imgAbrevs;
    private Graphics2D graphics;
    private Align fontAlignementRight = Align.LEFT;
    private int textImagesDefaultX = 0;
    private int textImagesDefaultY = 0;
    private float textImagesDefaultScale = 1;

    public RendereredImage(int i,
            String[] paramNames,
            String[] params,
            String scriptString,
            DataConfig dataConfig,
            ImageReader imageReader,
            ImageWriter imageWriter) {

        super(dataConfig.width(), dataConfig.height(), BufferedImage.TYPE_INT_ARGB);

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

    private BufferedImage getImage(String name) {
        return imageReader.get(imgAbrevs.containsKey(name) ? imgAbrevs.get(name) : name);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    // SET

    public void setTextImagesDefaults(int x, int y, float s) {
        textImagesDefaultX = x;
        textImagesDefaultY = y;
        textImagesDefaultScale = s;
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
        fontAlignementRight = Align.LEFT;
        for (String attr : attrParts) {
            switch (attr.toLowerCase()) {
                case "right":
                    fontAlignementRight = Align.RIGHT;
                    break;
                case "center":
                    fontAlignementRight = Align.CENTER;
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

    // DRAW STRING

    // public void drawString(String string, int x, int y) {
    // Rectangle2D bounds = graphics.getFont().getStringBounds(string,
    // graphics.getFontRenderContext());
    // int dx = fontAlignementRight ? -(int) bounds.getWidth() : 0;
    // graphics.drawString(string, x + dx, y);
    // }

    public void drawStringLines(String text, int x, int y, int w) {

        FontMetrics m = graphics.getFontMetrics();

        int stringWidth = getStringWidth(text);
        if (stringWidth < w) {
            drawStringAligned(text, x, y, w);

        } else {
            String[] words = text.split(" ");
            String currentLine = words[0];
            for (int i = 1; i < words.length; i++) {
                if (getStringWidth(currentLine + words[i]) < w) {
                    currentLine += " " + words[i];
                } else {
                    drawStringAligned(currentLine, x, y, w);
                    y += m.getHeight();
                    currentLine = words[i];
                }
            }
            if (currentLine.trim().length() > 0) {
                drawStringAligned(currentLine, x, y, w);
                // graphics.drawString(currentLine, x, y);
            }
        }

        // int caret = 0;
        // int currW = 0;
        // do{

        // }while();

        // int width = getStringWidth(string);
        // if(fontAlignementRight)
        // drawStringWithImgs(string, (int) (x + w / 2 - width / 2), y);

    }

    private void drawStringAligned(String text, int x, int y, int w) {

        if (fontAlignementRight == Align.CENTER && w != Integer.MAX_VALUE) {
            int width = getStringWidth(text);
            drawStringWithImgs(text, (int) (x + w / 2 - width / 2), y);
        } else if (fontAlignementRight == Align.RIGHT) {
            int width = getStringWidth(text);
            drawStringWithImgs(text, x - width, y);
        } else {
            drawStringWithImgs(text, x, y);
        }
    }

    private void drawStringWithImgs(String fullText, int x, int y) {

        graphics.drawString(getStringWithoutImages(fullText), x, y);

        List<String> images = getAllMatches(fullText, imageRegex);
        String text = fullText.replaceAll(imageRegex, "¿½");

        String[] parts = text.split("¿½");
        int dx = 0;
        for (int i = 0; i < parts.length; i++) {

            dx += getStringWidth(parts[i]);

            if (images.size() > i) {
                String[] imgParts = images.get(i).substring(1, images.get(i).length() - 1).split("\\:");
                String imgId = imgParts[0];
                int imgDx = imgParts.length > 1 ? Integer.parseInt(imgParts[1]) : textImagesDefaultX;
                int imgDy = imgParts.length > 2 ? Integer.parseInt(imgParts[2]) : textImagesDefaultY;
                float imgScale = imgParts.length > 3 ? Float.parseFloat(imgParts[3]) : textImagesDefaultScale;
                BufferedImage img = getImage(imgId);
                int imgW = (int) (img.getWidth() * imgScale);
                int imgH = (int) (img.getHeight() * imgScale);
                drawImage(img, x + dx + imgDx, y + imgDy - imgH, imgW, imgH);
            }
        }

    }

    // UTILS

    private int getStringWidth(String string) {
        String text = getStringWithoutImages(string);
        Rectangle2D bounds = graphics.getFont().getStringBounds(text, graphics.getFontRenderContext());
        return (int) bounds.getWidth();
    }

    private String getStringWithoutImages(String string) {
        return string.replaceAll(imageRegex, "");
    }

    public static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while (m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }

}
