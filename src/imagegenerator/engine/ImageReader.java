package imagegenerator.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.imageio.ImageIO;

public class ImageReader {

	private HashMap<String, BufferedImage> imgs = new HashMap<>();

	public HashMap<String, BufferedImage> getImgs() {
		return imgs;
	}

	public LinkedList<File> getAllImageFiles(Template template) {
		File srcDir = new File(template.getDir(), "inputs");

		if (!srcDir.isDirectory())
			throw new RuntimeException("Could not find image sources dir " + srcDir);

		LinkedList<File> files = new LinkedList<>();
		for (File file : srcDir.listFiles()) {
			if (!file.isDirectory() && (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")))
				files.add(file);
		}
		return files;
	}

	public Exception readImage(File imgFile) {
		String name = imgFile.getName().toLowerCase();
		String id = name.substring(0, name.length() - 4);

		try {
			BufferedImage img = ImageIO.read(imgFile);
			imgs.put(id, img);
			return null;
		} catch (IOException e) {
			return e;
		}

	}
}
