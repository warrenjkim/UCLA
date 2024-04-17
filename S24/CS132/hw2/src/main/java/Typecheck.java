import minijava.*;
import minijava.syntaxtree.*;
import java.util.HashMap;

public class Typecheck {
    public static void main(String [] args) {
        try {
            Node root = new MiniJavaParser(System.in).Goal();
            ClassVisitor cv = new ClassVisitor();
            TypeStruct err = root.accept(cv, new Context());
            if (err != null) {
                System.out.println(err.GetType());
                return;
            }

            err = root.accept(new TypeVisitor(cv.ClassTable()), new Context());
            if (err != null) {
                System.out.println(err.GetType());
                return;
            }

            System.out.println("Program type checked successfully");
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}

