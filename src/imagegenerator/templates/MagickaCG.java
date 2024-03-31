//package imagegenerator.templates;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.image.BufferedImage;
//
//import imagegenerator.engine.Renderer;
//import imagegenerator.engine.Template;
//import imagegenerator.engine.Util;
//
//public class MagickaCG extends Template {
//
//	static final Font FONT_TITLE = new Font("Stonecross", Font.BOLD, 20);
//	static final Font FONT_TEXT = new Font("Lato", Font.ITALIC, 26);
//	static final Font FONT_NRS = new Font("Kelmscott", Font.PLAIN, 40);
//
//	@Override
//	public int getWidth() {
//		return 512;
//	}
//
//	@Override
//	public int getHeight() {
//		return 512;
//	};
//
//	@Override
//	public boolean lineValid(String line) {
//		return true;
//	}
//
//	@Override
//	public Object[] readLine(String line) {
//		String[] ret = new String[] { "", "", "", "", "" };
//		int i = 0;
//		for (String string : Util.stripArray(line.strip().split("---"))) {
//			ret[i] = string;
//			i++;
//		}
//		return ret;
//	}
//
//	public void draw(Renderer r, String name, String color, String costs, String text) {
//		draw(r, name, color, costs, text, "");
//	}
//
//	public void draw(Renderer r, String name, String color, String costs, String text, String nrs) {
//
//		int x, y;
//
//		String img_name = "img_" + name.replace(" ", "_").toLowerCase();
//		r.drawImage(img_name, 0, 0);
//
//		if (!text.isBlank() && text.startsWith("::"))
//			if (text.contains(","))
//				r.drawImage("back_symbol_large");
//			else
//				r.drawImage("back_symbol");
//		else {
//			r.drawImage("back_" + color);
//			if (!text.isBlank())
//				r.drawImage(color.isBlank() ? "back_large_textbox" : "back_textbox", 0, 0);
//		}
//		r.drawImage("back_border", 0, 0);
//
//		x = 16;
//		y = 32;
//		r.setColor(Color.white);
//		r.setFont(FONT_TITLE);
//		r.drawString(name.toUpperCase(), x, y);
//
//		if (!text.isBlank()) {
//			r.setColor(Color.black);
//			r.setFont(FONT_TEXT);
//			x = color.isBlank() ? 24 : 100;
//			y = 388;
//			if (text.startsWith("::")) {
//				String[] elements = text.substring(2).split(",");
//				if (elements.length == 1) {
//					BufferedImage img = r.getImage("symbol_big_" + elements[0]);
//					r.drawImage(img, getWidth() / 2 - img.getWidth() / 2, 348);
//				} else {
//					int border = 20;
//					BufferedImage img1 = r.getImage("symbol_big_" + elements[0]);
//					BufferedImage img2 = r.getImage("symbol_big_" + elements[1]);
//					r.drawImage(img1, getWidth() / 2 - img1.getWidth() - border / 2, 348);
//					r.drawImage(img2, getWidth() / 2 + border / 2, 348);
//				}
//			} else {
//				Util.drawTextWithImgs(r, x, y, FONT_TEXT, text.split("\\|"), 32, 21);
//			}
//		}
//
//		if (!costs.isBlank())
//
//		{
//			x = 20;
//			y = 64;
//			for (String nr : costs.split(",")) {
//				r.drawImage("symbol_" + nr.toLowerCase(), x, y);
//				y += 64;
//			}
//		}
//
//		if (!nrs.isBlank()) {
//			r.setFont(FONT_NRS);
//			r.setColor(Color.white);
//			int nrHeight = 56, nrWidth = 56, border = 10;
//			x = 15;
//			y = getHeight() - 8 - nrHeight;
//
//			String[] nrsArray = nrs.split(",");
//			for (int i = nrsArray.length - 1; i >= 0; i--) {
//
//				r.drawImage("back_nr", x, y);
//				Util.drawStringCentered(r, x + 1, y - 5, nrsArray[i], nrWidth, nrHeight);
//				y -= nrWidth + border;
//			}
//		}
//	}
//
//}
