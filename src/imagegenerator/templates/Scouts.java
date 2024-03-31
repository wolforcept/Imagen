package imagegenerator.templates;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.HashMap;

import imagegenerator.engine.Renderer;
import imagegenerator.engine.Template;

public class Scouts extends Template {

	private static HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
	static {
		attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		attributes.put(TextAttribute.TRACKING, 0.05F);
//		attributes.put(TextAttribute.BIDI_EMBEDDING, 2);
//		attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
	}

	private static final Font FONT_TITLE = new Font("Beleren2016", Font.BOLD, 60).deriveFont(attributes);
	private static final Font FONT_TEXT = new Font("Segoe UI Emoji", Font.BOLD, 40);
	private static final String[] attrNames = { "Energia", "Sabedoria", "Destino" };
	private static final String[] attr = { "⚡", "🧠", "🍀" };
	private static final Color[] colors = { Color.red, Color.BLUE, Color.GREEN };
//	private static final float[] attrsizes = { 42, 50, 42 };

	public Scouts() {
		super(760, 1075);
	}

	@Override
	public boolean lineValid(String line) {
		int parts = line.split(super.separator).length;
		return parts == 4;
	}

	public void draw(Renderer r, String runName, String img, String nome, String subtexto, String attrs) {

		String[] attrsSplit = attrs.split(",");

		r.setColor(new Color(0, 0, 0));

		// TOP
		r.drawImage(img, 0, 0);
		r.drawImage(r.getImage("back"), 0, 0);
		r.setFont(FONT_TITLE);
		if (nome.length() >= 25)
			r.setFont(FONT_TITLE.deriveFont(50f));
		r.drawStringCentered(nome, 0, 100, 760);
		r.setFont(FONT_TEXT.deriveFont(40f));
		r.drawStringCentered(subtexto, 0, 600, 760);

		for (int i = 0; i < attrsSplit.length; i++) {
			r.setFont(FONT_TEXT);
			r.setColor(Color.black);
			r.drawString(attrNames[i] + ": ", 90, 750 + i * 100);
			String str = "";
			int n = Integer.parseInt(attrsSplit[i]);
			for (int j = 0; j < n; j++)
				str += attr[i];
			r.setColor(colors[i]);
			r.drawString(str, 300, 750 + i * 100);
		}

	}

}
