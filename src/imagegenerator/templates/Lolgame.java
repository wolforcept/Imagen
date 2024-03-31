
package imagegenerator.templates;

import static imagegenerator.engine.Util.drawStringCentered;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Font;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import imagegenerator.engine.Renderer;
import imagegenerator.engine.Template;
import imagegenerator.engine.Util;

public class Lolgame extends Template {

	private static HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
	static {
		attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		attributes.put(TextAttribute.TRACKING, 0.05F);
//			attributes.put(TextAttribute.BIDI_EMBEDDING, 2);
//			attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);

	}

	private static final Font FONT_TITLE = new Font("Stonecross Special", Font.BOLD, 30).deriveFont(attributes);
//		private static final Font FONT_TEXT = new Font("Mythica Blitzsphere", Font.BOLD, 40);
	private static final Font FONT_TEXT = new Font("Marcellus", Font.BOLD, 40);
	private static final Font FONT_TEXT_SMALL = FONT_TEXT.deriveFont(35f);
	private static final Font FONT_TEXT_VERY_SMALL = FONT_TEXT.deriveFont(18f);
	private static final Font FONT_NRS = new Font("Kelmscott", Font.PLAIN, 40);
	private static final Color COLOR_TITLE = Color.white;
	private static final Color COLOR_TEXT = new Color(0, 0, 0, .8f);
//	private static final String trademark = "Blitzsphere 2021  -  ";

	private static final int[] LCX = { 527, 306, 85 };
	private static final int[] LCY = { 882, 767 };
	private static final Point[] LAND_COST_POSITIONS = { new Point(LCX[0], LCY[0]), new Point(LCX[1], LCY[0]), new Point(LCX[2], LCY[0]), new Point(LCX[0], LCY[1]), new Point(LCX[1], LCY[1]),
			new Point(LCX[2], LCY[1]), };
	private static final HashMap<String, String> IMG_ABREVS = new HashMap<>();
	private static final boolean HAS_WATERMARK = false; // TODO

	static {
		IMG_ABREVS.put("m", "symbol_small_melee");
		IMG_ABREVS.put("r", "symbol_small_ranged");
		IMG_ABREVS.put("g", "symbol_small_magic");
		IMG_ABREVS.put("u", "symbol_small_utility");
		IMG_ABREVS.put("t", "symbol_small_runeterra");
		IMG_ABREVS.put("mm", "symbol_medium_melee");
		IMG_ABREVS.put("rr", "symbol_medium_ranged");
		IMG_ABREVS.put("gg", "symbol_medium_magic");
		IMG_ABREVS.put("uu", "symbol_medium_utility");
		IMG_ABREVS.put("tt", "symbol_medium_runeterra");
		IMG_ABREVS.put("big_melee", "symbol_big_melee");
		IMG_ABREVS.put("big_ranged", "symbol_big_ranged");
		IMG_ABREVS.put("big_magic", "symbol_big_magic");
		IMG_ABREVS.put("big_utility", "symbol_big_utility");
		IMG_ABREVS.put("1", "symbol_small_1");
		IMG_ABREVS.put("2", "symbol_small_2");
		IMG_ABREVS.put("3", "symbol_small_3");
		IMG_ABREVS.put("4", "symbol_small_4");
		IMG_ABREVS.put("5", "symbol_small_5");
		IMG_ABREVS.put("6", "symbol_small_6");
		IMG_ABREVS.put("7", "symbol_small_7");
		IMG_ABREVS.put("8", "symbol_small_8");
	}

	public enum Type {
		CHAMPION, LAND
	}

	private Type cardType;

	public Lolgame(Type cardType) {
		super(748, 1042);
		this.cardType = cardType;
	}

	@Override
	public boolean lineValid(String line) {
		int parts = line.split(super.separator).length;
		return parts == 5 || parts == 6;
	}

	public void draw(Renderer r, String runName, String imgNr, String region, String name, String subname, String costsStr) {
		draw(r, runName, imgNr, region, name, subname, costsStr, "");
	}

	public void draw(Renderer r, String runName, String imgNr, String region, String name, String subname, String costsStr, String text) {

		System.out.println(imgNr);
		Composite defaultComposite = r.getGraphics().getComposite();
		int x0 = -34, y0 = -34;

		int x, y;

		String regionStr = region.toLowerCase().replace(" ", "_");

		// TOP
		if (cardType == Type.LAND)
			r.drawImage(imgNr, -34, -34);
		else
			r.drawImage(imgNr, -34, -34 + 132, 816, 512);
		r.drawImage("back_" + (cardType == Type.LAND ? "lands" : regionStr), x0, y0);

		x = x0 + 165;
		y = y0 + 93;
		r.setColor(COLOR_TITLE);
		r.setFont(FONT_TITLE);
		if (cardType == Type.LAND) {
			r.drawStringCentered(name.toUpperCase(), 0, y, width);
		} else
			r.drawString(name.toUpperCase(), x, y);

		// WATERMARK
		if (HAS_WATERMARK) {
			BufferedImage imgWatermark = r.getImage("watermark_" + runName);
//				x = width / 2 - imgWatermark.width / 2;
//				y = y0 + 572;
			if (runName == "energy" || runName == "chaos") {
				r.drawImage(imgWatermark, x0, y0);
			}
			r.getGraphics().setComposite(banner_symbol_composite);
			r.drawImage(imgWatermark, x0, y0);
			r.getGraphics().setComposite(defaultComposite);
		}

		// MAIN TEXT
		if (!text.isBlank()) {
			BufferedImage imgSingle = r.getImage(text.trim());
			if (imgSingle != null) {
				x = width / 2 - imgSingle.getWidth() / 2;
				y = 666;
				r.drawImage(imgSingle, x, y);
			} else {
				r.setColor(COLOR_TEXT);
				r.setFont(FONT_TEXT);
				x = 62;
				y = 666;
				if (text.startsWith("::")) {
					String[] elements = text.substring(2).split(",");
					if (elements.length == 1) {
						BufferedImage img = r.getImage("symbol_small_" + elements[0]);
						r.drawImage(img, width / 2 - img.getWidth() / 2, 348);
					} else {
						int border = 20;
						BufferedImage img1 = r.getImage("symbol_small_" + elements[0]);
						BufferedImage img2 = r.getImage("symbol_small_" + elements[1]);
						r.drawImage(img1, width / 2 - img1.getWidth() - border / 2, 348);
						r.drawImage(img2, width / 2 + border / 2, 348);
					}
				} else {
					Util.drawTextWithWrap(r, x, y, FONT_TEXT, text.split("\\|"), 51, 42, 600);
//					drawTextWithImgs(r, x, y, FONT_TEXT, text.split("\\|"), 51, 42);
				}
			}
		}
		// BOTTOM TEXT
		x = x0 + 64;
		y = y0 + 1047;
		r.setColor(COLOR_TITLE);
		r.setFont(FONT_TEXT_SMALL);
		if (cardType != Type.LAND)
			r.drawString(region, x, y - 26);
		r.setFont(FONT_TEXT_VERY_SMALL);
		r.drawString(cardType == Type.LAND ? region : subname, x, y);

		if (cardType == Type.CHAMPION) {
			// BOTTOM COST
			String[] costs = costsStr.split(",");
			if (costs.length > 0) {
				x = x0 + 701;
				y = y0 + 995;
				for (String nr : costs) {
					r.drawImage("symbol_small_" + nr.toLowerCase(), x, y);
					x -= 54 + 4;
				}
//				r.drawImage("banner_" + runName + "_" + costs.length, x0, y0);
				//
//				x = x0 + 646;
//				y = y0 + 646;
//				for (String nr : costs) {
//					r.drawImage("symbol_medium_" + nr.toLowerCase(), x, y);
//					y += 64 + 4;
//				}
			}
		}

		if (cardType == Type.LAND) {
			List<String> costs = Arrays.asList(costsStr.strip().trim().replace(" ", "").split(","));
			Collections.reverse(costs);
			if (costs.size() > 0) {
				for (int i = 0; i < costs.size(); i++) {
					x = x0 + LAND_COST_POSITIONS[i].x;
					y = y0 + LAND_COST_POSITIONS[i].y;
					String c = costs.get(i);

					try {
						boolean b = c.charAt(c.length() - 1) >= 48 && c.charAt(c.length() - 1) <= 57;
						String nr = b ? c : c.substring(0, c.length() - 1);
						String img = b ? "tt" : "" + c.charAt(c.length() - 1) + c.charAt(c.length() - 1);

						r.drawImage("land_cost_" + nr, x, y);
						r.drawImage(IMG_ABREVS.get(img), x + 114, y + 26);
					} catch (Exception e) {
						System.err.println("Could not read cost string part: " + c);
						e.printStackTrace();
					}
				}
			}
		}

		String nrs = "";
		if (!nrs.isBlank()) {
			r.setFont(FONT_NRS);
			r.setColor(Color.white);
			int nrHeight = 56, nrWidth = 56, border = 10;
			x = 15;
			y = height - 8 - nrHeight;

			String[] nrsArray = nrs.split(",");
			for (int i = nrsArray.length - 1; i >= 0; i--) {

				r.drawImage("back_nr", x, y);
				drawStringCentered(r, x + 1, y - 5, nrsArray[i], nrWidth, nrHeight);
				y -= nrWidth + border;
			}
		}
	}

	@Override
	public HashMap<String, String> getImgsAbrevs() {
		return IMG_ABREVS;
	}

	//
	//
	//
	//
	//
	//
	//
	//

	private static final Composite banner_symbol_composite = new Composite() {

		@Override
		public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
			return new CompositeContext() {

				@Override
				public void dispose() {

				}

				@Override
				public void compose(Raster srcIn, Raster dstIn, WritableRaster dstOut) {

					int w = Math.min(srcIn.getWidth(), dstIn.getWidth());
					int h = Math.min(srcIn.getHeight(), dstIn.getHeight());

					int srcMinX = srcIn.getMinX();
					int srcMinY = srcIn.getMinY();
					int dstInMinX = dstIn.getMinX();
					int dstInMinY = dstIn.getMinY();
					int dstOutMinX = dstOut.getMinX();
					int dstOutMinY = dstOut.getMinY();

					for (int x = 0; x < w; x++) {
						for (int y = 0; y < h; y++) {
							int[] dst = new int[4];
							int[] src = new int[4];
							int[] result = new int[4];
							srcIn.getPixel(x + srcMinX, y + srcMinY, src);
							dstIn.getPixel(x + dstInMinX, y + dstInMinY, dst);
//								result[3] = dst[3] + src[3] - dst[3] * src[3];
							result[3] = dst[3];

							for (int i = 0; i < 3; i++) {
//									int low = dst[i];
//									int upp = src[i];
//									if (low > 127.5) {
								//
//										double val = (255 - low) / 127.5;
//										double min = low - (255 - low);
//										result[i] = (int) ((upp * val) + min);
//									} else {
//										double val = low / 127.5;
//										result[i] = (int) (upp * val);
//									}

								double a = dst[i] / 255.0;
								double b = src[i] / 255.0;
								double r = 0;
								if (b < 127.5) {
									r = 2 * a * b + a * a * (1 - 2 * b);
								} else {
									r = 2 * a * (1 - b) + Math.sqrt(a) * (2 * b - 1);
								}

								result[i] = (int) (dst[i] * (255 - src[3]) / 255 + r * 255 * (src[3]) / 255);
							}
							dstOut.setPixel(x + dstOutMinX, y + dstOutMinY, result);
						}
					}

//						(Target > 0.5) * (1 - (1-2*(Target-0.5)) * (1-Blend)) + (Target <= 0.5) * ((2*Target) * Blend)			

				}
			};
		}
	};

}
