package imagegenerator.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {

	private File destDir;

	public ImageWriter(Template template, String runName) {
		destDir = template.getDir(runName);
		if (!destDir.exists())
			destDir.mkdir();
	}

	public void outputImage(String name, BufferedImage img) {
		try {
			File output = new File(destDir, name + ".png");
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
