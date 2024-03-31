package imagegenerator;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import imagegenerator.engine.Template;
import imagegenerator.templates.Lolgame;

public class Config {

	public static final boolean VERBOSE = true;
	public static final File BASE_DIR = new File("templates");
	public static final boolean OUTPUT_SINGLE_IMAGES = false;
	public static final boolean DISPLAY_SINGLE_IMAGES = false;
	public static final boolean OUTPUT_COMPOUND_IMAGES = true;
	public static final boolean DISPLAY_COMPOUND_IMAGES = true;
	public static final boolean OUTPUT_PAGES = true;
	public static final boolean MERGE_PAGES = true;

	private HashMap<String, Template> runs = new HashMap<>();

	public Config() {
// 		runs.put("scouts2", new Scouts2());
//		runs.put("scouts1", new Scouts());
//		runs.put("scouts2", new Scouts());
//		runs.put("scouts3", new Scouts());
//		runs.put("scouts4", new Scouts());
//		runs.put("scouts5", new Scouts());

		// lolgame
		runs.put("champions1", new Lolgame(Lolgame.Type.CHAMPION));
		runs.put("champions2", new Lolgame(Lolgame.Type.CHAMPION));
		runs.put("champions3", new Lolgame(Lolgame.Type.CHAMPION));
		runs.put("champions4", new Lolgame(Lolgame.Type.CHAMPION));
		runs.put("champions5", new Lolgame(Lolgame.Type.CHAMPION));
		runs.put("lands", new Lolgame(Lolgame.Type.LAND));

//		runs.put("rules", new Blitzsphere(Blitzsphere.Type.RULES));
//		runs.put("starterdeck", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("lands", new Blitzsphere(Blitzsphere.Type.LAND));
//		runs.put("chaos", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("wilds", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("time", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("faith", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("occult", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("energy", new Blitzsphere(Blitzsphere.Type.NORMAL));
//		runs.put("wanderer", new Blitzsphere(Blitzsphere.Type.NORMAL));

//		runs.put("ingredient_cards", new CursedWitches());
//		runs.put("lesson_cards", new CursedWitches());
//		runs.put("paramlist", new AgeOfWar());
//		runs.put("paramlist", new AgeOfWarBacks());

//		runs.put("buy_deck", new MagickaCG());
//		runs.put("buy_deck_expansion", new MagickaCG());
//		runs.put("enemy_deck", new MagickaCG());
//		runs.put("magic_mana_deck", new MagickaCG());
//		runs.put("starting_deck", new MagickaCG());
//		runs.put("starting_deck_expansion", new MagickaCG());
//		runs.put("wounds_deck", new MagickaCG());

	}

	public Set<String> getRunNames() {
		return runs.keySet();
	}

	public Template getTemplate(String templateName) {
		return runs.get(templateName);
	}

}
