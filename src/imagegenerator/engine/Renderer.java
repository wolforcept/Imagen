package imagegenerator.engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Renderer {

	private HashMap<String, BufferedImage> imgs;
	private Graphics2D graphics;
	private HashMap<String, String> imgAbrevs;

	public Renderer(HashMap<String, BufferedImage> imgs, Graphics2D graphics, HashMap<String, String> imgAbrevs) {
		this.imgs = imgs;
		this.imgAbrevs = imgAbrevs;
		this.graphics = graphics;

		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	// GET

	public BufferedImage getImage(String name) {
		if (imgAbrevs.containsKey(name))
			return imgs.get(imgAbrevs.get(name));
		return imgs.get(name);
	}

	public Set<String> getImagesKeySet() {
		HashSet<String> set = new HashSet<>();
		set.addAll(imgs.keySet());
		set.addAll(imgAbrevs.keySet());
		return set;
	}

	public Graphics2D getGraphics() {
		return graphics;
	}

	public Font getFont() {
		return graphics.getFont();
	}

	// SET

	public void setColor(Color color) {
		graphics.setColor(color);
	}

	public void setFont(Font font) {
		graphics.setFont(font);
	}

	// DRAW

	public void drawImage(String imageId) {
		drawImage(imageId, 0, 0);
	}

	public void drawImage(Image image, int x, int y) {
		graphics.drawImage(image, x, y, null);
	}

	public void drawImage(Image image, int x, int y, int w, int h) {
		graphics.drawImage(image, x, y, w, h, null);
	}

	public void drawImage(String imageId, int x, int y) {
		if (imgs.containsKey(imageId))
			graphics.drawImage(imgs.get(imageId), x, y, null);
		else
			System.out.println("Could not find image to draw: " + imageId);
	}

	public void drawImage(String imageId, int x, int y, int w, int h) {
		if (imgs.containsKey(imageId))
			graphics.drawImage(imgs.get(imageId), x, y, w, h, null);
		else
			System.out.println("Could not find image to draw: " + imageId);
	}

	public void drawString(String string, int x, int y) {
		graphics.drawString(string, x, y);
	}

	public void drawStringCentered(String string, int x, int y, int w) {
		Rectangle2D bounds = getFont().getStringBounds(string, graphics.getFontRenderContext());
		drawString(string, (int) (x + w / 2 - bounds.getWidth() / 2), y);
	}
}
