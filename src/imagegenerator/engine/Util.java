package imagegenerator.engine;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Util {

	public static int[] arrayToArray(String[] split) {
		int[] ret = new int[split.length];
		for (int i = 0; i < ret.length; i++) {
			if (split[i].isBlank())
				ret[i] = 0;
			else
				ret[i] = Integer.parseInt(split[i].strip());
		}
		return ret;
	}

	public static String[] stripArray(String[] arr) {
		String[] ret = new String[arr.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = arr[i].strip();
		}
		return ret;
	}

	public static Object[] params(Object... params) {
		return params;
	}

	public static Rectangle2D getStringBounds(Font font, String string) {
		return font.getStringBounds(string, new FontRenderContext(new AffineTransform(), true, false));
	}

	public static void drawStringCentered(Renderer r, int x, int y, String string, int w, int h) {
		Rectangle2D strBounds = getStringBounds(r.getFont(), string);
		int strWidth = (int) strBounds.getWidth();
		int strHeight = (int) strBounds.getHeight();
		r.drawString(string, x + w / 2 - strWidth / 2, y + h / 2 + strHeight / 2);
	}

	public static void drawTextWithImgs(Renderer r, int x, int y, Font font, String[] otherText, int lineHeight, int imgRaise) {
		Graphics2D g = r.getGraphics();
		Set<String> imgKeys = r.getImagesKeySet();
		for (String text : otherText) {

			for (String imgId : imgKeys) {
				text = text.replaceAll("\\/" + imgId, "\\!\\�\\$\\(" + imgId + "\\)\\!\\�");
			}

			String[] parts = text.split("\\!\\�");
			int xx = x;
			for (String part : parts) {
				boolean wasImage = false;
				CHECK_FOR_IMAGE: for (String key : imgKeys)
					if (part.equals("$(" + key.toLowerCase() + ")")) {
						BufferedImage img = r.getImage(key.toLowerCase());
						g.drawImage(img, xx, y - imgRaise, null);
						xx += img.getWidth();
						wasImage = true;
						break CHECK_FOR_IMAGE;
					}

				if (!wasImage) {
					g.drawString(part, xx, y);
					xx += Util.getStringBounds(font, part).getWidth();
				}
			}
			y += text.isEmpty() ? lineHeight / 2 : lineHeight;
		}
	}

	public static void drawTextWithWrap(Renderer r, int x, int y, Font font, String[] paragraphs, int lineHeight, int imgRaise, int width) {

		int dy = y;

		for (String text : paragraphs) {

			List<String> words = new ArrayList<String>(Arrays.stream(text.split(" ")).toList());

			String line = "";
			while (!words.isEmpty()) {

				String word = words.get(0);
				String lineWithNextWord = line + " " + word;
				double lineWidth = Util.getStringBounds(font, lineWithNextWord).getWidth();
				if (lineWidth < width) {
					line = lineWithNextWord;
					words.remove(0);
				} else {
					drawTextWithImgs(r, x, dy, font, new String[] { line }, lineHeight, imgRaise);
//					g.drawString(line, x, dy);
					dy += line.trim().equals("") || line.trim().equals(" ") ? lineHeight / 2 : lineHeight;
					line = "";
				}
			}
//			g.drawString(line, x, dy);
			drawTextWithImgs(r, x, dy, font, new String[] { line }, lineHeight, imgRaise);
			dy += line.trim().equals("") || line.trim().equals(" ") ? lineHeight / 2 : lineHeight;
			line = "";
		}
	}
}
