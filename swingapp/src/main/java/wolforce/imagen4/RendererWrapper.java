package wolforce.imagen4;

public class RendererWrapper implements Imagen {
//public class RendererWrapper extends Script {

    public RendereredImage renderer;

    RendererWrapper(RendereredImage image) {
        this.renderer = image;
    }

    @Override
    public int width() {
        return renderer.getWidth();
    }

    @Override
    public int height() {
        return renderer.getHeight();
    }

//    @Override
//    public Object run() {
//        throw new UnsupportedOperationException("Unimplemented method 'run'");
//    }

    public void color(String color) {
        renderer.setColor(color);
    }

    @Override
    public void font(String font, int size, String attrs) {
        renderer.setFont(font, size, attrs);
    }

    @Override
    public void font(String font, int size) {
        font(font, size, "");
    }

    @Override
    public void font(String font) {
        font(font, 30);
    }

    // DRAW

    @Override
    public void draw(String imageId) {
        draw(imageId, 0, 0);
    }

//    public void draw(Image image, int x, int y) {
//        renderer.drawImage(image, x, y);
//    }
//
//    public void draw(Image image, int x, int y, int w, int h) {
//        renderer.drawImage(image, x, y, w, h);
//    }

    @Override
    public void draw(String imageId, int x, int y) {
        renderer.drawImage(imageId, x, y);
    }

    @Override
    public void draw(String imageId, int x, int y, int w, int h) {
        renderer.drawImage(imageId, x, y, w, h);
    }

    @Override
    public int text(String string, int x, int y) {
        return renderer.drawString(string, x, y, Integer.MAX_VALUE);
    }

    @Override
    public int text(String string, int x, int y, int w) {
        return renderer.drawString(string, x, y, w);
    }

    @Override
    public void textImagesDefaults(int x, int y, float scale) {
        renderer.setTextImagesDefaults(x, y, scale);
    }

    @Override
    public void abrev(String abrev, String path) {
        renderer.addImageAbrev(abrev, path);
    }
}
