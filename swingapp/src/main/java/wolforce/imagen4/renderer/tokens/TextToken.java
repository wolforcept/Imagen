package wolforce.imagen4.renderer.tokens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class TextToken implements Token {

    public final TokenParser parser;
    public final String text;
    public final Color color;
    public final Font font;

    public TextToken(TokenParser parser, String text, Color color, Font font) {
        this.parser = parser;
        this.text = text;
        this.color = color;
        this.font = font;
    }

    @Override
    public Rectangle2D getBounds() {
        Graphics2D graphics = parser.graphics;
        return font.getStringBounds(text, graphics.getFontRenderContext());
    }

    @Override
    public void render(int x, int y, boolean isDebug) {
        Graphics2D graphics = parser.graphics;

        var prevColor = graphics.getColor();
        var prevFont = graphics.getFont();

        graphics.setColor(color);
        graphics.setFont(font);
        graphics.drawString(text, x, y);

        graphics.setFont(prevFont);
        graphics.setColor(prevColor);
    }
}
