package wolforce.imagen4.renderer;

import org.apache.commons.lang3.tuple.Pair;
import wolforce.imagen4.renderer.RendereredImage.Align;
import wolforce.imagen4.renderer.tokens.ImageToken;
import wolforce.imagen4.renderer.tokens.LineBreakToken;
import wolforce.imagen4.renderer.tokens.TextToken;
import wolforce.imagen4.renderer.tokens.Token;
import wolforce.imagen4.renderer.tokens.TokenParser;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

public class TextRenderer {

    private final RendereredImage rendereredImage;
    private final Graphics2D graphics;
    private final boolean isDebug;
    private final Align fontAlignement;

    private int finalHeight;

    public TextRenderer(
            RendereredImage rendereredImage,
            Graphics2D graphics,
            boolean isDebug,
            Align fontAlignement) {

        this.rendereredImage = rendereredImage;
        this.graphics = graphics;
        this.isDebug = isDebug;
        this.fontAlignement = fontAlignement;
        this.finalHeight = 0;
    }

    public RendereredImage getRendereredImage() {
        return rendereredImage;
    }

    public int getFinalHeight() {
        return finalHeight;
    }

    public void renderText(String text, int x, int y, int w, boolean isTest) {
        List<Token> tokens = new TokenParser(this, graphics).tokenize(text);

        List<Pair<Integer, List<Token>>> lines = new LinkedList<>();
        {
            int lineWidth = 0;
            List<Token> currentLine = new LinkedList<>();

            for (Token token : tokens) {

                var bounds = token.getBounds();

                if (lineWidth + bounds.getWidth() > w || token instanceof LineBreakToken) {
                    lines.add(Pair.of(lineWidth, currentLine));
                    lineWidth = 0;
                    currentLine = new LinkedList<>();
                }

                lineWidth += bounds.getWidth();
                currentLine.add(token);
            }
            lines.add(Pair.of(lineWidth, currentLine));
        }

        int firstLineH = calcMaxLineHeight(lines.get(0).getRight());
        int lineY = y - firstLineH;
        for (Pair<Integer, List<Token>> line : lines) {
            int lineW = line.getLeft();
            var lineTokens = line.getRight();

            int lineX = x;
            int lineH = calcMaxLineHeight(lineTokens);

            if (fontAlignement == Align.RIGHT) {
                lineX = x + w - lineW;
            }

            if (fontAlignement == Align.CENTER) {
                lineX = x + w / 2 - lineW / 2;
            }

            lineY += lineH;
            finalHeight += lineH;

            if (isDebug) {
                Color prevColor = graphics.getColor();

                graphics.setColor(Color.lightGray);
                graphics.drawRect(x, lineY - lineH, w, lineH);
                graphics.setColor(Color.black);
                graphics.drawRect(lineX, lineY - lineH, lineW, lineH);

                graphics.setColor(prevColor);
            }

            if (!isTest)
                renderLine(lineTokens, lineX, lineY, lineH);

        }
    }

    private int calcMaxLineHeight(List<Token> line) {
        int maxH = 0;
        for (Token token : line) {
            if (token instanceof TextToken textToken) {
                int h = graphics.getFontMetrics(textToken.font).getHeight();
                if (h > maxH) maxH = h;
            }
        }
        return maxH;
    }

    private void renderLine(List<Token> tokens, int x, int y, int lineH) {
        int dx = x;
        for (Token token : tokens) {
            int lineDeltaH = token instanceof ImageToken ? lineH : 0;
            token.render(dx, y - lineDeltaH, isDebug);
            dx += token.getBounds().getWidth();
        }
    }

}
