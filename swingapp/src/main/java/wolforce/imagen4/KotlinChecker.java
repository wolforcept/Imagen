package wolforce.imagen4;

import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class KotlinChecker {
    public static void main(String[] args) {
        ScriptEngineManager mgr = new ScriptEngineManager();
        for (ScriptEngineFactory f : mgr.getEngineFactories()) {
            System.out.println(f.getEngineName() + " (" + f.getLanguageName() + ")");
        }
    }
}
