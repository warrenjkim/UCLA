import minijava.*;
import minijava.syntaxtree.*;
import java.util.HashMap;

public class Typecheck {
    public static void main(String [] args) {
        try {
            Node root = new MiniJavaParser(System.in).Goal();
            TypeStruct err = root.accept(new ClassVisitor(), new Context());
            if (err != null) {
                System.out.println(err.GetType());
            }
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}

