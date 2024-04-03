package wolforce.imagen4;

import java.awt.Image;

import groovy.lang.Script;

public class RendererWrapper extends Script {

    public static RendereredImage renderer;

    public int width() {
        return renderer.getWidth();
    }

    public int height() {
        return renderer.getHeight();
    }

    @Override
    public Object run() {
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

    public void color(String color) {
        renderer.setColor(color);
    }

    public void font(String font, int size, String attrs) {
        renderer.setFont(font, size, attrs);
    }

    public void font(String font, int size) {
        font(font, size, "");
    }

    public void font(String font) {
        font(font, 30);
    }

    // DRAW

    public void image(String imageId) {
        image(imageId, 0, 0);
    }

    public void image(Image image, int x, int y) {
        renderer.drawImage(image, x, y);
    }

    public void image(Image image, int x, int y, int w, int h) {
        renderer.drawImage(image, x, y, w, h);
    }

    public void image(String imageId, int x, int y) {
        renderer.drawImage(imageId, x, y);
    }

    public void image(String imageId, int x, int y, int w, int h) {
        renderer.drawImage(imageId, x, y, w, h);
    }

    public void text(String string, int x, int y) {
        renderer.drawString(string, x, y, Integer.MAX_VALUE);
    }

    public void text(String string, int x, int y, int w) {
        renderer.drawString(string, x, y, w);
    }

    public void textImagesDefaults(int x, int y, float scale) {
        renderer.setTextImagesDefaults(x, y, scale);
    }

    public void abrev(String abrev, String path) {
        renderer.addImageAbrev(abrev, path);
    }
}
