package wolforce.imagen4.scripting;

import wolforce.imagen4.data.Grid;

import javax.script.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Script {

    public static String tryGetOrCreateScript(String projectPath) {
        File file = Paths.get(projectPath, "script.kt").toFile();
        if (!file.exists() || file.isDirectory()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()))) {
                writer.write(getNewScriptText());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("could not create necessary script.kt file!");
            }
        }
        return file.getAbsolutePath();
    }

    private static String getNewScriptText() {
        return """
                interface Imagen {
                     fun width(): Int
                     fun height(): Int
                     fun color(color: String)
                     fun font(font: String, size: Int, attrs: String) // attrs: right, center, bold, italic
                     fun font(font: String, size: Int)
                     fun font(font: String)
                     fun draw(imageId: String)
                     fun draw(imageId: String, x: Int, y: Int)
                     fun draw(imageId: String, x: Int, y: Int, w: Int, h: Int)
                     fun text(string: String, x: Int, y: Int)
                     fun text(string: String, x: Int, y: Int, w: Int)
                     fun textImagesDefaults(x: Int, y: Int, scale: Float)
                     fun abrev(abrev: String, path: String)
                 }
                 
                 enum class TestEnum {
                     Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
                 }
                  
                 class Point(x: Int, y: Int);
                 
                 class Script(
                     val imagen: Imagen,
                     val name: String,
                     val i: Int,
                     val f: Float,
                     val b: Boolean,
                     val test: TestEnum
                 ) {
                 
                     val point = Point(0, 0)
                 
                     init {
                         imagen.abrev("health", "heart.png")
                         imagen.draw("$name.png", imagen.width() / 2, imagen.height() / 2, imagen.width() / 2, imagen.height() / 2)
                         imagen.font("Arial", 35, "center")
                         imagen.text("name is $name|", 50, 50, imagen.width())
                     }
                 
                 }
                """;
    }

    //

    public final int paramsNumber;
    //    private final ParamCollection params;
    public final ParamType[] paramsTypes;
    public final String[] paramsNames;
    private String scriptString;

    public Script(String path) {
        scriptString = loadFromFile(path);
        var params = new ParamCollection(scriptString);
        paramsNumber = params.size();
        paramsTypes = params.getTypes();
        paramsNames = params.getNames();
    }

    private String loadFromFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Could not read Script file: " + path);
        }
    }

    public void run(Imagen imagen, Grid grid, int rowIndex, ErrorTrace trace) {

        List<String> errors = new LinkedList<>();

        trace.startingRow(grid, rowIndex);

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ScriptEngine engine = new ScriptEngineManager(classLoader).getEngineByExtension("kts");
            Bindings bindings = engine.createBindings();
            bindings.put("imagen", imagen);
            engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);

            StringBuilder callStringBuilder = new StringBuilder("import wolforce.imagen4.scripting.Imagen\nScript(imagen, ");
            for (int i = 0; i < paramsNumber; i++) {
                String toAppend;
                ParamType paramType = paramsTypes[i];
                String paramName = paramsNames[i];
                String paramValue = grid.get(rowIndex, i);
                if (paramType instanceof ParamTypeBasic paramTypeBasic) {
                    toAppend = switch (paramTypeBasic) {
                        case String -> "\"" + paramValue + "\",";
                        case Int -> tryParseInt(paramName, paramType.getName(), paramValue, errors) + ", ";
                        case Boolean -> tryParseBoolean(paramName, paramType.getName(), paramValue, errors) + ", ";
                        case Float -> tryParseFloat(paramName, paramType.getName(), paramValue, errors) + "f, ";
                    };
                } else if (paramType instanceof ParamTypeEnum paramTypeEnum) {
                    if (Arrays.stream(paramTypeEnum.values).noneMatch(x -> x.equals(paramValue))) {
                        errors.add("Could not parse param " + paramsNames[i] + ":" + paramType.getName() + " = " + paramValue);
                        toAppend = paramTypeEnum.name + "." + paramTypeEnum.values[0];
                    } else {
                        toAppend = paramTypeEnum.name + "." + paramValue;
                    }
                } else {
                    throw new RuntimeException("Unknown Script parameter type: " + paramType.getName());
                }
                callStringBuilder.append(toAppend);
            }
            callStringBuilder.append(")");
            String callString = callStringBuilder.toString();

            scriptString = scriptString.replaceFirst("(?ms)interface Imagen \\{.*?}", "import wolforce.imagen4.scripting.Imagen");

            engine.eval(scriptString);
            engine.eval(callString);

            trace.finishedRow();

        } catch (ScriptException e) {
            errors.add(e.getMessage());
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            trace.errored(sw.toString());
        }

    }

    private int tryParseInt(String paramName, String paramType, String paramValue, List<String> errors) {
        if (paramValue.equals(""))
            return 0;
        try {
            return Integer.parseInt(paramValue);
        } catch (Exception e) {
            errors.add("Could not parse param " + paramName + ":" + paramType + " = " + paramValue);
        }
        return 0;
    }

    private float tryParseFloat(String paramName, String paramType, String paramValue, List<String> errors) {
        try {
            return Float.parseFloat(paramValue);
        } catch (Exception e) {
            errors.add("Could not parse param " + paramName + ":" + paramType + " = " + paramValue);
        }
        return 0f;
    }

    private boolean tryParseBoolean(String paramName, String paramType, String paramValue, List<String> errors) {
        if (paramValue.equalsIgnoreCase("true")) return true;
        if (paramValue.equalsIgnoreCase("false")) return false;
        errors.add("Could not parse param " + paramName + ":" + paramType + " = " + paramValue);
        return false;
    }

    public String getParamName(int paramIndex) {
        if (paramIndex < 0 || paramIndex >= paramsNames.length)
            return "";
        return paramsNames[paramIndex];
    }

    public String getParamsString() {
//        int nParams = getParamsNumber();
//        String[] names = getParamsNames();
//        ParamType[] types = getParamsTypes();

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramsNumber; i++)
            params.append(paramsNames[i]).append(":").append(paramsTypes[i].getName()).append(" ");
        return params.toString();
    }
}
