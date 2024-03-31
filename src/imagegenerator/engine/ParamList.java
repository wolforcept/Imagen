package imagegenerator.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class ParamList {

	private Template template;
	private File paramsFile;

	ParamList(Template template, String runName) {
		this.template = template;
		this.paramsFile = new File(template.getDir(), runName + ".paramlist");
	}

	public String[] getLines() {

		try {
			BufferedReader reader = new BufferedReader(new FileReader(paramsFile));

			LinkedList<String> lines = new LinkedList<>();
			String line;
			do {
				line = reader.readLine();
				if (line != null) {
					if (line.strip().startsWith(template.separator))
						lines.add(lines.removeLast() + line);
					else
						lines.add(line.strip());
				}
			} while (line != null);

			reader.close();
			return (String[]) lines.toArray(new String[lines.size()]);

		} catch (IOException e) {
			throw new RuntimeException("Could not read lines of paramlist file " + paramsFile.getPath(), e);
		}
	}

	public Object[] readParamLine(String line) {

		if (line.strip().length() > 0 && !line.strip().startsWith("//") && !line.strip().startsWith("#")) {
			if (template.lineValid(line)) {
				Object[] params = template.readLine(line);

				return params;
			}
		}
		return null;
	}

}
