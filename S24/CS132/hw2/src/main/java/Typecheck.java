import minijava.*;
import minijava.syntaxtree.*;
import java.util.LinkedList;

public class Typecheck {
    public static void main(String [] args) {
        try {
            Node root = new MiniJavaParser(System.in).Goal();
            root.accept(new ClassVisitor(), new LinkedList<>());
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}

