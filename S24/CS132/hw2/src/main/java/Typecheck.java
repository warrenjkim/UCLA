import minijava.*;
import minijava.syntaxtree.*;

public class Typecheck {
    public static void main(String [] args) {
        try {
            Node root = new MiniJavaParser(System.in).Goal();
            root.accept(new ExpressionVisitor());
        } catch (ParseException e) {
            System.out.println(e.toString());
        }
    }
}

