package wolforce.imagen4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageReader extends HashMap<String, BufferedImage> {

    private static BufferedImage MISSING_IMAGE = makeMissingImage();

    private static BufferedImage makeMissingImage() {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 50, 50);
        g.fillRect(50, 50, 100, 100);
        return img;
    }

    private String inputPath;

    @Override
    public BufferedImage get(Object key) {
        if (!containsKey(key))
            readImage(key.toString());
        if (containsKey(key))
            return super.get(key);
        return MISSING_IMAGE;
    }

    public void readImage(String path) {
        File file = Path.of(inputPath, path).toFile();
        System.out.print("[Image Reader] reading image " + file.getAbsolutePath());
        try {
            BufferedImage img = ImageIO.read(file);
            put(path, img);
            System.out.println(" Done.");
        } catch (IOException e) {
            System.out.println(" [ERROR] " + e.getMessage());
            // e.printStackTrace();
        }
    }

    public String tryCreateInputFolder() {
        File file = new File("inputs");
        if (!file.exists())
            file.mkdir();
        this.inputPath = file.getAbsolutePath();
        return inputPath;
    }
}
