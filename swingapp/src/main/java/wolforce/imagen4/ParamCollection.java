package wolforce.imagen4;

import org.apache.commons.lang3.EnumUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamCollection {

    private record Param(String name, ParamType type) {
    }

    private final ArrayList<Param> params = new ArrayList<>();

    ParamCollection(String scriptString) {
        parse(scriptString);
    }

    public String[] getNames() {
        return params.stream().map(x -> x.name).toArray(String[]::new);
    }

    public ParamType[] getTypes() {
        return params.stream().map(x -> x.type).toArray(ParamType[]::new);
    }

    public ParamType getParamType(int index) {
        return params.get(index).type;
    }

    public int size() {
        return params.size();
    }

    public void parse(String scriptString) {

        Matcher mainMatcher = Pattern.compile("(?sm)class Script\\((.*?)\\)").matcher(scriptString);
        if (!mainMatcher.find()) throw new RuntimeException("Could not find Script class in script file");
        String all = mainMatcher.group(1);
        all = all.replaceAll("val ", "")
                .replaceAll("\r", "")
                .replaceAll("\n", "")
                .replaceAll(" ", "")
                .strip();
        if (all.endsWith(",")) all = all.substring(0, all.length() - 1);
        Arrays.stream(all.split(",")).forEach(x -> {
            String[] subParts = x.split(":");
            String name = subParts[0].strip();
            if (name.equals("imagen")) return;
            String typeString = subParts[1].strip().replace(":", "").strip();
            if (EnumUtils.isValidEnum(ParamTypeBasic.class, typeString))
                params.add(new Param(name, ParamTypeBasic.valueOf(typeString)));
            else {
                String[] enumValues = loadEnumValues(scriptString, typeString);
                params.add(new Param(typeString, new ParamTypeEnum(typeString, enumValues)));
            }
        });

    }

    private String[] loadEnumValues(String scriptString, String name) {

        Matcher enumMatcher = Pattern.compile("(?sm)enum class " + name + " ?\\r?\\n?\\{\\s*(.*?)\\r?\\n?\\}").matcher(scriptString);
        if (!enumMatcher.find())
            throw new RuntimeException("Could not find Enum class " + name + " in script file");
        String[] enumPossibleValues = enumMatcher.group(1).split(",");
        for (int i = 0; i < enumPossibleValues.length; i++) enumPossibleValues[i] = enumPossibleValues[i].strip();
        return enumPossibleValues;
    }
}
