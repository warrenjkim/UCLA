import minijava.*;
import minijava.syntaxtree.*;

public class Typecheck {
    public static void main(String [] args) {
        try {
            Node root = new MiniJavaParser(System.in).Goal();
            ClassVisitor cv = new ClassVisitor();
            TypeStruct err = root.accept(cv, new Context());
            if (err != null) {
                System.out.println(err.Type());
                return;
            }

            err = root.accept(new TypeVisitor(cv.ClassTable()), new Context());
            if (err != null) {
                System.out.println(err.Type());
                return;
            }

            System.out.println("Program type checked successfully");
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}

