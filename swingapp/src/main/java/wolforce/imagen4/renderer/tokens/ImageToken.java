package wolforce.imagen4.renderer.tokens;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ImageToken implements Token {

    public static int defaultX = 0;
    public static int defaultY = 0;
    public static double defaultScale = 1;

    private final TokenParser parser;
    private final BufferedImage image;

    private int dx;
    private int dy;
    private double scale;

    ImageToken(TokenParser parser, String raw) {
        this.parser = parser;

        String[] mainParts = raw.substring(1, raw.length() - 1).split(":");

        this.image = parser.getImage(mainParts[0]);
        this.dx = defaultX;
        this.dy = defaultY;
        this.scale = defaultScale;

        if (mainParts.length == 1) return;

        String[] attrs = mainParts[1].split(",");


        for (String attrRaw : attrs) {
            try {
                if (attrRaw.startsWith("x="))
                    dx = Integer.parseInt(attrRaw.substring(2));
                else if (attrRaw.startsWith("y="))
                    dy = Integer.parseInt(attrRaw.substring(2));
                else if (attrRaw.startsWith("scale="))
                    scale = Double.parseDouble(attrRaw.substring(6));
                else
                    throw new Exception();
            } catch (Exception e) {
                System.err.println("Attribute wrongly formatted: " + attrRaw);
            }
        }
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Float(dx, dy, (float) (image.getWidth() * scale),
                (float) (image.getHeight() * scale));
    }

    @Override
    public void render(int x, int y, boolean isDebug) {
        Graphics2D graphics = parser.graphics;

        int w = (int) (scale * image.getWidth());
        int h = (int) (scale * image.getHeight());

        graphics.drawImage(image, x + dx, y + dy, w, h, null);

        if (isDebug) {
            Color prevColor = graphics.getColor();
            graphics.setColor(Color.white);
            graphics.drawRect(x + dx, y + dy, w, h);
            graphics.setColor(prevColor);
        }
    }

}

//private static class RichTextToken extends Token {
//
//    private final Runnable action;
//    public String raw;
//
//    RichTextToken(String raw, Runnable action) {
////            super(_raw.substring(1, _raw.length() - 1));
//
////            String[] mainParts = raw.split(":");
////            text = mainParts[0];
//
////            if (mainParts.length > 1) {
////                String[] attrs = mainParts[1].split(",");
////
////                for (String attrRaw : attrs) {
////                    try {
////                        if (attrRaw.equals("bold"))
////                            isBold = true;
////                        else if (attrRaw.equals("italic"))
////                            isItalic = true;
////                        else if (attrRaw.startsWith("#"))
////                            color = Color.decode(attrRaw);
////                        else
////                            throw new Exception();
////                        // if (attrRaw.startsWith("color="))
////                        // bold = Integer.parseInt(attrRaw.substring(2));
////                    } catch (Exception e) {
////                        System.err.println("Attribute wrongly formatted: " + attrRaw);
////                    }
////                }
////            } else {
////                text = "[" + text + "]";
////            }
//        this.raw = raw;
//        this.action = action;
//    }
//
//    @Override
//    Rectangle2D getBounds() {
//        return new Rectangle2D.Float(0f, 0f, 0f, 0f);
//    }
//
//    @Override
//    String getRawText() {
//        return raw;
//    }
//
//    @Override
//    public void render(int x, int y) {
//        action.run();
//    }
//
//}