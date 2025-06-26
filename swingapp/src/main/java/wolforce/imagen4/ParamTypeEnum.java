package wolforce.imagen4;

public class ParamTypeEnum implements ParamType {

    public final String name;
    public final String[] values;

    public ParamTypeEnum(String name, String[] values) {
        this.name = name;
        this.values = values;
    }

    @Override
    public String getName() {
        return name;
    }
}
