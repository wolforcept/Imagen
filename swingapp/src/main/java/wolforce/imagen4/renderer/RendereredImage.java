package wolforce.imagen4.renderer;

import wolforce.imagen4.io.ImageReader;
import wolforce.imagen4.renderer.tokens.ImageToken;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

@SuppressWarnings("unused")
public class RendereredImage extends BufferedImage {

    enum Align {
        LEFT, CENTER, RIGHT
    }

    private final ImageReader imageReader;
    private final boolean isDebug;

    private final HashMap<String, String> imgAbrevs;
    private final Graphics2D graphics;
    private Align fontAlignement = Align.LEFT;

    public RendereredImage(int width, int height, boolean isDebug, ImageReader imageReader) {

        super(width, height, BufferedImage.TYPE_INT_ARGB);

        this.isDebug = isDebug;

        this.imageReader = imageReader;

        imgAbrevs = new HashMap<>();
        graphics = createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    }

    // GET

    public BufferedImage getImage(String name) {
        return imageReader.get(imgAbrevs.getOrDefault(name, name));
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    // SET

    public void setTextImagesDefaults(int x, int y, float s) {
        ImageToken.defaultX = x;
        ImageToken.defaultY = y;
        ImageToken.defaultScale = s;
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
                case "right" -> fontAlignement = Align.RIGHT;
                case "center" -> fontAlignement = Align.CENTER;
                case "bold" -> style |= Font.BOLD;
                case "italic" -> style |= Font.ITALIC;
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
        drawImage(image, x, y, image.getWidth(null), image.getHeight(null));
    }

    public void drawImage(Image image, int x, int y, int w, int h) {
        graphics.drawImage(image, x, y, w, h, null);
        if (isDebug) {
            Color prevColor = graphics.getColor();
            graphics.setColor(Color.white);
            graphics.drawRect(x, y, w, h);
            graphics.setColor(prevColor);
        }
    }

    public void drawImage(String imageId, int x, int y) {
        drawImage(getImage(imageId), x, y);
    }

    public void drawImage(String imageId, int x, int y, int w, int h) {
        drawImage(getImage(imageId), x, y, w, h);
    }

    public int drawString(String s, int x, int y, int w) {
        var textRenderer = new TextRenderer(this, graphics, isDebug, fontAlignement);
        textRenderer.renderText(s, x, y, w, false);
        return textRenderer.getFinalHeight();
    }

    public int textHeight(String s, int w) {
        var textRenderer = new TextRenderer(this, graphics, isDebug, fontAlignement);
        textRenderer.renderText(s, 0, 0, w, true);
        return textRenderer.getFinalHeight();
    }

}
