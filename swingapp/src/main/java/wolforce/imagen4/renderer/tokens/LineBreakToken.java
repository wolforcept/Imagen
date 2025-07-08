package wolforce.imagen4.renderer.tokens;

import java.awt.geom.Rectangle2D;

public class LineBreakToken implements Token {

    @Override
    public void render(int x, int y, boolean isDebug) {
    }

    @Override
    public Rectangle2D getBounds() {
        return new Rectangle2D.Float(0, 0, 0, 0);
    }
}
