package imagegenerator.engine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import imagegenerator.Config;

public class PageWriter {

	public static PageWriter instance;

	public static void add(Template template, String runName, BufferedImage img, String imgName) {
		if (instance == null || (!Config.MERGE_PAGES && template != instance.template))
			instance = new PageWriter(template, runName);
		instance._add(img, imgName);
	}

	private File destDir;
	private final int xi = 118, yi = 191, w = 2480, h = 3508;
	private BufferedImage page;
	private int x = xi, y = yi, pageNr = 0;
	private String runName;
	private Template template;

	public PageWriter(Template template, String runName) {
		this.template = template;
		this.runName = runName;
		destDir = template.getDir(runName);
		if (!destDir.exists())
			destDir.mkdir();
		initPage();
	}

	private void initPage() {
		page = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		x = xi;
		y = yi;
	}

	public void outputImage(String name, BufferedImage img) {
		try {
			File output = new File(destDir, name + ".png");
			ImageIO.write(img, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void _add(BufferedImage img, String imgName) {

		Graphics g = page.getGraphics();
		g.drawImage(img, x, y, null);
		x += img.getWidth();
		if (x > w - img.getWidth()) {
			x = xi;
			y += img.getHeight();
		}
		if (y > h - img.getHeight()) {
			writePage();
		}
	}

	private void writePage() {
		try {
			File output = new File(destDir, runName + "_" + "page" + pageNr + ".png");
			pageNr++;
			ImageIO.write(page, "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		initPage();
	}

}
