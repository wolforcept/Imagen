package wolforce.imagen4;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {

    private static String ouputPath;

    public void outputImage(String name, BufferedImage img) {
        File destDir = new File(ouputPath);
        if (!destDir.exists())
            destDir.mkdir();

        try {
            File output = new File(destDir, name + ".png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String tryCreateOutputFolder() {
        File file = new File("outputs");
        if (!file.exists())
            file.mkdir();
        ouputPath = file.getAbsolutePath();
        return ouputPath;
    }

}
