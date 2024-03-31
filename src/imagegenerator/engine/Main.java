package imagegenerator.engine;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import imagegenerator.Config;

public class Main {

	public static void main(String[] args) throws IOException {

		log("Image Generator v3\n");

		Config config = new Config();

		String loadedClass = null;
		ImageReader imageReader = new ImageReader();

		for (String runName : config.getRunNames()) {

			Template template = config.getTemplate(runName);
			String className = template.getClassName();

			if (!className.equals(loadedClass)) {
				log("----- " + className + " -----");
				log("loading input images");
				LinkedList<File> imgsFiles = imageReader.getAllImageFiles(template);
				for (File imgFile : imgsFiles) {
					Exception ex = imageReader.readImage(imgFile);
					if (ex != null) {
						log("! Could not read image file " + imgFile.getPath(), true);
						if (Config.VERBOSE)
							ex.printStackTrace();
					} else {
						log("| > " + imgFile.getName().toLowerCase());
					}
				}
				loadedClass = className;
				log("");
			}

			log("----- " + className + "." + runName + " -----");

			ParamList paramList = new ParamList(template, runName);
			Generator generator = new Generator(template, runName);

			for (String paramLine : paramList.getLines()) {
				Object[] params = paramList.readParamLine(paramLine);
				if (params != null) {
					generator.generateImage(imageReader, params);
					log("| > " + Arrays.asList(params));
				}
			}

			generator.generateCompoundImage(template, runName);
		}
	}

	public static void log(Object o) {
		log(o, false);
	}

	public static void log(Object o, boolean isErr) {
		if (isErr)
			System.err.println(o);
		else
			System.out.println(o);
	}

}
