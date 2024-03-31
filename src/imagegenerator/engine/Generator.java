package imagegenerator.engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import imagegenerator.Config;

public class Generator {

	private Template template;
	private String runName;

	private ImageWriter imageWriter;
	private LinkedList<BufferedImage> outImages;
	private int nImages;

	public Generator(Template template, String runName) {
		this.template = template;
		this.runName = runName;

		this.imageWriter = new ImageWriter(template, runName);
		this.outImages = new LinkedList<BufferedImage>();
		this.nImages = 0;
	}

	public void generateImage(ImageReader imageReader, Object[] params) {

		BufferedImage img = new BufferedImage(template.width, template.height, BufferedImage.TYPE_INT_ARGB);
		Renderer renderer = new Renderer(imageReader.getImgs(), (Graphics2D) img.getGraphics(), template.getImgsAbrevs());

		invoke(renderer, runName, params);
		outImages.add(img);

		String imgName = runName + "_" + nImages++;
		if (Config.OUTPUT_SINGLE_IMAGES)
			imageWriter.outputImage(imgName, img);
		if (Config.DISPLAY_SINGLE_IMAGES)
			Displayer.add(img, imgName);
		if (Config.OUTPUT_PAGES)
			PageWriter.add(template, runName, img, imgName);

	}

	public void generateCompoundImage(Template template, String outputFileName) {
		int totalN = outImages.size();
		int nLines = 1;
		int nCols = 1;
		boolean increasingCols = true;
		while (nLines * nCols < totalN) {
			if (increasingCols)
				nCols++;
			else {
				if (nLines < 7)
					nLines++;
			}
			increasingCols = !increasingCols;
		}
		System.out.println("|  > Size: " + nCols + " x " + nLines);
		BufferedImage compImg = new BufferedImage(template.width * nCols, template.height * nLines, BufferedImage.TYPE_INT_ARGB);
		int x = 0;
		int y = 0;
		for (BufferedImage subimg : outImages) {
			compImg.getGraphics().drawImage(subimg, x, y, null);
			x += template.width;
			if (x >= template.width * nCols) {
				x = 0;
				y += template.height;
			}
		}
		if (Config.OUTPUT_COMPOUND_IMAGES)
			imageWriter.outputImage(runName, compImg);
		if (Config.DISPLAY_COMPOUND_IMAGES)
			Displayer.add(compImg, runName);
	}

	private void invoke(Renderer renderer, String runName, Object[] params) {

		LinkedList<Object> methodParams = new LinkedList<>();
		methodParams.add(renderer);
		methodParams.add(runName);

		for (Object o : params)
			methodParams.add(o);

		Object[] paramArray = methodParams.toArray();
		for (Method method : template.getClass().getMethods()) {
			if (method.getName().equals("draw") && method.getParameterCount() == paramArray.length) {
				try {
					method.invoke(template, paramArray);
					return;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
