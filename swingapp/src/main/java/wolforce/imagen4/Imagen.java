package wolforce.imagen4;

public interface Imagen {

    int width();

    int height();

    void color(String color);

    void font(String font, int size, String attrs);

    void font(String font, int size);

    void font(String font);

    void draw(String imageId);

    void draw(String imageId, int x, int y);

    void draw(String imageId, int x, int y, int w, int h);

    int text(String string, int x, int y);

    int text(String string, int x, int y, int w);

    void textImagesDefaults(int x, int y, float scale);

    void abrev(String abrev, String path);
}
