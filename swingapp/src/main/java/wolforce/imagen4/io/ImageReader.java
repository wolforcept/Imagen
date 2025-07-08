package wolforce.imagen4.io;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

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

    private final String inputsPath;

    public ImageReader(String inputsPath) {
        this.inputsPath = inputsPath;
    }

    @Override
    public BufferedImage get(Object key) {
        if (!containsKey(key))
            readImage(key.toString());
        if (containsKey(key))
            return super.get(key);
        return MISSING_IMAGE;
    }

    public void readImage(String path) {
        File file = Path.of(inputsPath, path).toFile();
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

    public static String tryGetOrCreateInputFolder(String projectPath) {
        File file = Paths.get(projectPath, "inputs").toFile();
        if (!file.exists())
            if (!file.mkdir())
                throw new RuntimeException("Could not create Inputs folder.");
        return file.getAbsolutePath();
    }
}
