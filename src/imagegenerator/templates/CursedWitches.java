//package imagegenerator.templates;
//
//import static imagegenerator.engine.Util.*;
//
//import java.awt.Color;
//import java.awt.Font;
//import java.awt.font.FontRenderContext;
//import java.awt.geom.AffineTransform;
//
//import imagegenerator.engine.Renderer;
//import imagegenerator.engine.Template;
//import imagegenerator.engine.Util;
//
//public class CursedWitches extends Template {
//
//	static final Font FONT_TITLE = new Font("Permanent Marker", Font.PLAIN, 44);
//	static final Font FONT_FOUND_AT = new Font("Bubblegum Sans", Font.PLAIN, 40);
//	static final Font FONT_TEXT = new Font("Bubblegum Sans", Font.PLAIN, 50);
//
//	@Override
//	public int getWidth() {
//		return 799;
//	}
//
//	@Override
//	public int getHeight() {
//		return 1059;
//	}
//
//	@Override
//	public boolean lineValid(String line) {
//		return line.split("---").length > 5;
//	}
//
//	@Override
//	public Object[] readLine(String _line) {
//		String[] line = _line.split("---");
//		String[] props = stripArray(line[4].split(","));
//		int[] propValues = arrayToArray(line[5].split(","));
//		return params(line[0].strip(), line[1].strip(), line[2].strip(), line[3].strip(), props, propValues,
//				line.length > 6 ? line[6].split(";") : null);
//	}
//
//	public void draw(Renderer r, String _title, String found_at, String type, String rarity, String[] attributes, int[] attributesValues,
//			String[] otherText) {
//
//		boolean borderless = _title.startsWith("::");
//		String title = borderless ? _title.substring(2) : _title;
//
//		String img_id = "img_" + title.toLowerCase().replace(' ', '_');
//		if (r.getImagesKeySet().contains(img_id)) {
//			if (borderless)
//				r.drawImage(img_id, 0, 0, getWidth(), getHeight());
//			else
//				r.drawImage(img_id, 39, 148, 721, 512);
//		}
//
//		if (!type.contains(":")) {
//			r.drawImage((borderless ? "borderless_" : "back_") + type, 0, 0);
//		} else {
//			String[] types = type.split(":");
//			r.drawImage((borderless ? "borderless_" : "back_") + types[0], 0, 0);
//			r.drawImage((borderless ? "borderless_half_" : "half_") + types[1], 0, 0);
//		}
//
//		r.drawImage("rarity_" + rarity, 685, 52, 54, 54);
//
//		r.setColor(Color.black);
//		r.setFont(FONT_TITLE);
//		if (type.equals("spell"))
//			r.drawString(title, 57, 94);
//		else
//			r.drawString(title, 150, 94);
//		r.setFont(FONT_FOUND_AT);
//		r.drawString(found_at, 50, 735);
//
//		r.setFont(FONT_TEXT);
//
//		int x = 58, y = 845;
//		for (int i = 0; i < attributes.length; i++) {
//			String attr = attributes[i];
//			int attrValue = attributesValues[i];
//			if (attrValue == 0)
//				continue;
//			String text = attr + ": ";
//			r.drawString(text, x, y);
//			int w = (int) FONT_TEXT.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, false)).getWidth();
//			for (int j = 0; j < attrValue; j++) {
//				r.drawImage("symbol_" + attr.toLowerCase(), x + w + 68 * j, y - 46);
//			}
//			y += 75;
//		}
//
//		if (otherText != null) {
//			Util.drawTextWithImgs(r, x, y, FONT_TEXT, otherText, 60, 64);
//		}
//	}
//
//}
