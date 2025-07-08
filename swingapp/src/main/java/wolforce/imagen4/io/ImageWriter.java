package wolforce.imagen4.io;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class ImageWriter {

    private final String outputsPath;

    public ImageWriter(String outputsPath) {
        this.outputsPath = outputsPath;
    }

    public void outputImage(String name, BufferedImage img) {
        File destDir = new File(outputsPath);

        try {
            File output = new File(destDir, name + ".png");
            ImageIO.write(img, "png", output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String tryGetOrCreateOutputFolder(String projectPath) {
        File file = Paths.get(projectPath, "outputs").toFile();
        if (!file.exists())
            if (!file.mkdir())
                throw new RuntimeException("Could not create Outputs folder");
        return file.getAbsolutePath();
    }

}
