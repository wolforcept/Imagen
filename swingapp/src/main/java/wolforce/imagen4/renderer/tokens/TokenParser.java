package wolforce.imagen4.renderer.tokens;

import wolforce.imagen4.renderer.TextRenderer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class TokenParser {

    Graphics2D graphics;

    private boolean isBold = false;
    private boolean isItalic = false;
    private Integer size = null;
    private Color color = null;
    private String fontName = null;

    private final TextRenderer textRenderer;

    public TokenParser(TextRenderer textRenderer, Graphics2D graphics) {
        this.textRenderer = textRenderer;
        this.graphics = graphics;
    }

    BufferedImage getImage(String id) {
        return textRenderer.getRendereredImage().getImage(id);
    }

    public List<Token> tokenize(String text) {
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
                Token token = tryParseToken(tokenString);
                list.add(token);
                tokenString = "";
                list.add(new LineBreakToken());
                continue;
            }

            if (isTokenOpeningChar(c)) {

                Token token = tryParseToken(tokenString);
                list.add(token);
                tokenString = "";
            }

            tokenString += c;

            if (isTokenClosingChar(c, tokenString)) {

                Token token = tryParseToken(tokenString);
                if (token != null) list.add(token);
                tokenString = "";
            }

        }

        if (tokenString.length() > 0) {
            Token token = tryParseToken(tokenString);
            if (token != null) list.add(token);
        }

        return list;
    }

    static boolean isTokenOpeningChar(char c) {
        return c == '{';
    }

    static boolean isTokenClosingChar(char c, String tokenString) {
        return c == '}' || (c == ' ' && !isTokenOpeningChar(tokenString.charAt(0)));
    }

    private Token tryParseToken(String tokenString) {

        if (tokenString.equalsIgnoreCase("{b}")) {
            isBold = true;
            return null;
        }
        if (tokenString.equalsIgnoreCase("{/b}")) {
            isBold = false;
            return null;
        }
        if (tokenString.equalsIgnoreCase("{i}")) {
            isItalic = true;
            return null;
        }
        if (tokenString.equalsIgnoreCase("{/i}")) {
            isItalic = false;
            return null;
        }

        if (tokenString.matches("\\{size=\\d+}")) {
            try {
                size = Integer.parseInt(tokenString.substring(6, tokenString.length() - 1));
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse text size token: " + tokenString);
            }
            return null;
        }
        if (tokenString.matches("\\{/size}")) {
            size = null;
            return null;
        }

        if (tokenString.matches("\\{font=\\d+}")) {
            fontName = tokenString.substring(6, tokenString.length() - 1);
            return null;
        }
        if (tokenString.matches("\\{/font}")) {
            fontName = null;
            return null;
        }

        if (tokenString.matches("\\{color=.+}")) {
            try {
                color = Color.decode("#" + tokenString.substring(7, tokenString.length() - 1));
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse text color token: " + tokenString);
            }
            return null;
        }
        if (tokenString.matches("\\{/color}")) {
            color = null;
            return null;
        }

        if (tokenString.matches("\\{[^{}]+}")) {
            return new ImageToken(this, tokenString);
        }

        int style = (isBold ? Font.BOLD : 0) | (isItalic ? Font.ITALIC : 0);
        int size = this.size != null ? this.size : graphics.getFont().getSize();
        Font font = fontName != null //
                ? new Font(fontName, style, size).deriveFont(style) //
                : graphics.getFont().deriveFont(style).deriveFont((float) size);
        Color color = this.color != null ? this.color : graphics.getColor();

        return new TextToken(this, tokenString, color, font);
    }
}
