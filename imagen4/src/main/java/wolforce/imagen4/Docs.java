package wolforce.imagen4;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Docs {

    public static void generate() {

        String docs = "";

        for (Method method : RendererWrapper.class.getMethods()) {
            String sig = calculateMethodSignature(method);
            docs += sig + "\n\n";
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("docs.txt"));) {
            writer.write(docs);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // try(BufferedReader reader = new BufferedReader(new
        // FileReader("src/main/java/wolforce/imagen4/RenderedWrapper.java"))){
        // reader.transferTo
        // }

        // try {
        // String path = "imagen4/src/main/java/wolforce/imagen4/RendererWrapper.java";
        // path = Paths.get(new File("").getAbsolutePath(),
        // path).toFile().getAbsolutePath();
        // System.out.println(path);
        // String text = new String(Files.readAllBytes(Paths.get(path)),
        // StandardCharsets.UTF_8);
        // try (BufferedWriter writer = new BufferedWriter(new FileWriter("docs.txt"));)
        // {
        // writer.write(text);
        // writer.close();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

    }

    private static String calculateMethodSignature(Method method) {
        String signature = "";
        if (method != null) {

            Class<?> returnType = method.getReturnType();
            if (returnType == void.class) {
                signature += "void";
            } else {
                signature += Array.newInstance(returnType, 1).getClass().getSimpleName();
            }

            signature += " " + method.getName() + " ";

            signature += "(";
            for (Parameter p : method.getParameters()) {
                signature += p.getType().getSimpleName() + " " + p.getName() + ", ";
            }
            signature += ")";

            // signature = signature.replace('.', '/');
        }

        return signature;
    }
}
