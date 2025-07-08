package wolforce.imagen4.scripting;

public enum ParamTypeBasic implements ParamType {
    String, Int, Float, Boolean;

    @Override
    public java.lang.String getName() {
        return name();
    }
}
