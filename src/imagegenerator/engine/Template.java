package imagegenerator.engine;

import static imagegenerator.engine.Util.stripArray;

import java.awt.font.TextAttribute;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import imagegenerator.Config;

public abstract class Template {

	public static HashMap<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
	static {
		attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
		attributes.put(TextAttribute.TRACKING, 0.05F);
//		attributes.put(TextAttribute.BIDI_EMBEDDING, 2);
//		attributes.put(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
	}

	public final int width, height;
	public final String separator;

	public Template(int width, int height) {
		this(width, height, "---");
	}

	public Template(int width, int height, String separator) {
		this.width = width;
		this.height = height;
		this.separator = separator;
	}

	public abstract boolean lineValid(String line);

	public static abstract class GenerationTemplate extends Template {
		public GenerationTemplate(int width, int height, String separator) {
			super(width, height, separator);
		}

		abstract String[] generateLines();
	}

	public File getDir() {
		return new File(Config.BASE_DIR, getClassName());
	}

	public File getDir(String templateName) {
		return new File(getDir(), templateName);
	}

	public String getClassName() {
		return getClass().getSimpleName();
	}

	public HashMap<String, String> getImgsAbrevs() {
		return new HashMap<>();
	}

	public Object[] readLine(String line) {
		LinkedList<Object> parts = new LinkedList<>();
		for (String part : stripArray(line.strip().split(separator)))
			parts.add(part);
		return parts.toArray();
	}
}