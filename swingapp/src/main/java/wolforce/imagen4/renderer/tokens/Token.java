package wolforce.imagen4.renderer.tokens;


import java.awt.geom.Rectangle2D;

public interface Token {

    Rectangle2D getBounds();

    void render(int x, int y, boolean isDebug);

}

