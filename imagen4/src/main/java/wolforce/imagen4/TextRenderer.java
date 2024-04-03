package wolforce.imagen4;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import wolforce.imagen4.RendereredImage.Align;

public class TextRenderer {

    private static final String regexImages = "\\{[^\\{\\}]+\\}";
    private static final String regexRichText = "\\[[^\\[\\]]+\\]";

    public static int textImagesDefaultX = 0;
    public static int textImagesDefaultY = 0;
    public static float textImagesDefaultScale = 1;

    private final RendereredImage rendereredImage;
    private final Graphics2D graphics;
    private final boolean isDebug;
    private final Align fontAlignement;
    private final Font font;
    private final FontMetrics fontMetrics;

    public TextRenderer(
            RendereredImage rendereredImage,
            Graphics2D graphics,
            boolean isDebug,
            Align fontAlignement) {

        this.rendereredImage = rendereredImage;
        this.graphics = graphics;
        this.isDebug = isDebug;
        this.fontAlignement = fontAlignement;
        this.font = graphics.getFont();
        this.fontMetrics = graphics.getFontMetrics(font);

    }

    public void renderText(String text, int x, int y, int w) {
        List<Token> tokens = tokenize(text);

        List<Pair<Integer, List<Token>>> lines = new LinkedList<>();
        {
            int lineWidth = 0;
            List<Token> currentLine = new LinkedList<>();
            for (Token token : tokens) {

                var bounds = token.getBounds();

                if (lineWidth + bounds.getWidth() > w || token instanceof LineBreakToken) {
                    lines.add(Pair.of((int) (lineWidth), currentLine));
                    lineWidth = 0;
                    currentLine = new LinkedList<>();
                }

                lineWidth += bounds.getWidth();
                currentLine.add(token);
            }
            lines.add(Pair.of((int) (lineWidth), currentLine));
        }

        int lineY = y;
        for (Pair<Integer, List<Token>> line : lines) {
            int lineW = line.getLeft();
            var lineTokens = line.getRight();

            int lineX = x;

            if (fontAlignement == Align.RIGHT) {
                lineX = x + w - lineW;
            }

            if (fontAlignement == Align.CENTER) {
                lineX = x + w / 2 - lineW / 2;
            }

            if (isDebug) {
                Color prevColor = graphics.getColor();

                int lineH = fontMetrics.getHeight();
                graphics.setColor(Color.white);
                graphics.drawRect(x, lineY - lineH, w, lineH);
                graphics.setColor(Color.black);
                graphics.drawRect(lineX, lineY - lineH, lineW, lineH);

                graphics.setColor(prevColor);
            }

            renderLine(lineTokens, lineX, lineY);

            lineY += fontMetrics.getHeight();
        }
    }

    private void renderLine(List<Token> tokens, int x, int y) {
        int dx = x;
        for (Token token : tokens) {
            token.render(dx, y);
            dx += token.getBounds().getWidth();
        }
    }

    static boolean isTokenOpeningChar(char c) {
        return c == '{' || c == '[';
    }

    static boolean isTokenClosingChar(char c, String tokenString) {
        return c == '}' || c == ']' || (c == ' ' && !isTokenOpeningChar(tokenString.charAt(0)));
    }

    private List<Token> tokenize(String text) {
        List<Token> list = new LinkedList<>();

        String tokenString = "";

        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c == '\\' && i < text.length() - 1) {
                tokenString += text.charAt(i + 1);
                i++;
                continue;
            }

            if (c == '|') {
                Token token = tryParseToken(tokenString, false);
                list.add(token);
                tokenString = "";
                list.add(new LineBreakToken());
                continue;
            }

            if (isTokenOpeningChar(c)) {

                Token token = tryParseToken(tokenString, true);
                list.add(token);
                tokenString = "";
            }

            tokenString += c;

            if (isTokenClosingChar(c, tokenString)) {

                Token token = tryParseToken(tokenString, false);
                list.add(token);
                tokenString = "";
            }

        }

        if (tokenString.length() > 0)
            list.add(tryParseToken(tokenString, true));

        return list;
    }

    private Token tryParseToken(String tokenString, boolean force) {

        if (tokenString.matches(regexImages))
            return new ImageToken(tokenString);

        if (tokenString.matches(regexRichText))
            return new RichTextToken(tokenString);

        return new TextToken(tokenString);
    }

    private abstract class Token {
        public final String raw;

        Token(String raw) {
            this.raw = raw;
        }

        public void render(int x, int y) {
            graphics.drawString(this.raw, x, y);
        }

        Rectangle2D getBounds() {
            return font.getStringBounds(raw, graphics.getFontRenderContext());
        }

        @Override
        public String toString() {
            return raw.replaceAll(" ", "_");
        }
    }

    private class TextToken extends Token {

        TextToken(String raw) {
            super(raw);
        }
    }

    private class LineBreakToken extends Token {

        LineBreakToken() {
            super("");
        }
    }

    private class ImageToken extends Token {

        public final BufferedImage image;
        public int dx = textImagesDefaultX;
        public int dy = textImagesDefaultY;
        public double scale = textImagesDefaultScale;

        ImageToken(String _raw) {
            super(_raw.substring(1, _raw.length() - 1));

            String[] mainParts = raw.split(":");
            image = rendereredImage.getImage(mainParts[0]);

            if (mainParts.length > 1) {
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
        }

        @Override
        Rectangle2D getBounds() {
            return new Rectangle2D.Float(dx, dy, (float) (image.getWidth() * scale),
                    (float) (image.getHeight() * scale));
        }

        @Override
        public void render(int x, int y) {
            int w = (int) (scale * image.getWidth());
            int h = (int) (scale * image.getHeight());
            graphics.drawImage(image, x + dx, y + dy - fontMetrics.getHeight(), w, h, null);
        }

    }

    private class RichTextToken extends Token {

        public final String text;
        private boolean isBold = false;
        private boolean isItalic = false;
        private Color color = null;

        RichTextToken(String _raw) {
            super(_raw.substring(1, _raw.length() - 1));

            String[] mainParts = raw.split(":");
            text = mainParts[0];

            if (mainParts.length > 1) {
                String[] attrs = mainParts[1].split(",");

                for (String attrRaw : attrs) {
                    try {
                        if (attrRaw.equals("bold"))
                            isBold = true;
                        else if (attrRaw.equals("italic"))
                            isItalic = true;
                        else if (attrRaw.startsWith("#"))
                            color = Color.decode(attrRaw);
                        else
                            throw new Exception();
                        // if (attrRaw.startsWith("color="))
                        // bold = Integer.parseInt(attrRaw.substring(2));
                    } catch (Exception e) {
                        System.err.println("Attribute wrongly formatted: " + attrRaw);
                    }
                }
            }
        }

        @Override
        Rectangle2D getBounds() {
            return font.getStringBounds(text, graphics.getFontRenderContext());
        }

        @Override
        public void render(int x, int y) {
            Font prevFont = graphics.getFont();
            Color prevColor = graphics.getColor();

            int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
            graphics.setColor(color != null ? color : graphics.getColor());
            graphics.setFont(graphics.getFont().deriveFont(style));

            graphics.drawString(text, x, y);

            graphics.setColor(prevColor);
            graphics.setFont(prevFont);
        }

    }

}
