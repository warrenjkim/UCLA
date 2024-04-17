import java.util.Enumeration;
import java.util.HashMap;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order. Your visitors may extend this class.
 */
public class ClassVisitor extends GJDepthFirst<TypeStruct, Context> {
    private HashMap<String, ClassSymbol> classTable;

    public HashMap<String, ClassSymbol> ClassTable() {
        return this.classTable;
    }

    public ClassVisitor() {
        super();
        this.classTable = new HashMap<>();
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    @Override
    public TypeStruct visit(NodeList n, Context context) {
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            TypeStruct err = e.nextElement().accept(this, context);
            if (err != null) {
                return err;
            }

            _count++;
        }

        return null;
    }

    @Override
    public TypeStruct visit(NodeListOptional n, Context context) {
        if (n.present()) {
            int _count = 0;
            for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
                TypeStruct err = e.nextElement().accept(this, context);
                if (err != null) {
                    return err;
                }
                _count++;
            }
        }

        return null;
    }

    @Override
    public TypeStruct visit(NodeSequence n, Context context) {
        int _count = 0;
        for (Enumeration<Node> e = n.elements(); e.hasMoreElements();) {
            TypeStruct err = e.nextElement().accept(this, context);
            if (err != null) {
                return err;
            }

            _count++;
        }
        return null;
    }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    @Override
    public TypeStruct visit(Goal n, Context context) {
        TypeStruct err = n.f0.accept(this, context);
        if (err != null) {
            return err;
        }

        err = n.f1.accept(this, context);
        if (err != null) {
            return err;
        }

        System.out.println("classTable:");
        for (HashMap.Entry<String, ClassSymbol> x : this.classTable.entrySet()) {
            System.out.println("  Class Name: " + x.getValue().ClassName());
            for (HashMap.Entry<String, TypeStruct> field : x.getValue().Fields().Table().entrySet()) {
                System.out.println("    Field: (" + field.getKey() + ", " + field.getValue().GetType() + ")");
            }
            for (HashMap.Entry<String, MethodSymbol> method : x.getValue().Methods().entrySet()) {
                System.out.println("    Method: (" + method.getValue().MethodName() + ", "
                        + method.getValue().ReturnType().GetType() + ")");
                for (SymbolTable scope : method.getValue().VariableScopes()) {
                    if (scope.Table().isEmpty()) {
                        System.out.println("        No Formal Parameters..........");
                        break;
                    }
                    System.out.println("        Formal Parameters..........");
                    for (HashMap.Entry<String, TypeStruct> param : scope.Table().entrySet()) {
                        System.out.println(
                                "          Param: (" + param.getKey() + ", " + param.getValue().GetType() + ")");
                    }
                }
            }
            System.out.println();
        }

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public TypeStruct visit(MainClass n, Context context) {
        if (!this.classTable.isEmpty()) {
            return new TypeStruct("Type error");
        }

        context.SetMethod(n.f6.tokenImage, new TypeStruct("void"));
        context.Method().AddVariable(n.f11, new TypeStruct("StringArrayType"));

        context.SetClass(n.f1);
        context.Class().AddMethod(context.Method());

        TypeStruct err = n.f14.accept(this, context); // build fields
        if (err != null) {
            return err;
        }

        this.classTable.put(context.Class().ClassName(), context.Class());

        return null;
    }

    /**
     * f0 -> ClassDeclaration()
     * | ClassExtendsDeclaration()
     */
    @Override
    public TypeStruct visit(TypeDeclaration n, Context context) {
        return n.f0.accept(this, context);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public TypeStruct visit(ClassDeclaration n, Context context) {
        context.SetClass(n.f1);

        // duplicate class name
        if (this.classTable.containsKey(context.Class().ClassName())) {
            return new TypeStruct("Type error");
        }

        TypeStruct err = n.f3.accept(this, context); // build fields
        if (err != null) {
            return err;
        }

        err = n.f4.accept(this, context); // build methods
        if (err != null) {
            return err;
        }

        this.classTable.put(context.Class().ClassName(), context.Class());

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public TypeStruct visit(ClassExtendsDeclaration n, Context context) {
        context.SetClass(n.f1, n.f3);

        // duplicate class name
        if (this.classTable.containsKey(context.Class().ClassName())) {
            return new TypeStruct("Type error");
        }

        TypeStruct err = n.f3.accept(this, context); // build fields
        if (err != null) {
            return err;
        }

        err = n.f5.accept(this, context); // build methods
        if (err != null) {
            return err;
        }

        this.classTable.put(context.Class().ClassName(), context.Class());

        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public TypeStruct visit(VarDeclaration n, Context context) {
        return context.Class().Fields().AddSymbol(n.f1, n.f0.accept(this, context));
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public TypeStruct visit(MethodDeclaration n, Context context) {
        context.SetMethod(n.f2, n.f1.accept(this, context));

        // duplicate method name
        if (context.Class().Methods().containsKey(context.Method().MethodName())) {
            return new TypeStruct("Type error");
        }

        TypeStruct err = n.f4.accept(this, context); // build formal parameters
        if (err != null) {
            return err;
        }

        context.Class().AddMethod(context.Method());

        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    @Override
    public TypeStruct visit(FormalParameterList n, Context context) {
        TypeStruct err = n.f0.accept(this, context);
        if (err != null) {
            return err;
        }

        return n.f1.accept(this, context);
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public TypeStruct visit(FormalParameter n, Context context) {
        return context.Method().AddVariable(n.f1, n.f0.accept(this, context));
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public TypeStruct visit(FormalParameterRest n, Context context) {
        return n.f1.accept(this, context);
    }

    /**
     * f0 -> ArrayType()
     * | BooleanType()
     * | IntegerType()
     * | Identifier()
     */
    @Override
    public TypeStruct visit(Type n, Context context) {
        return n.f0.accept(this, context);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    @Override
    public TypeStruct visit(ArrayType n, Context context) {
        return new TypeStruct("ArrayType");
    }

    /**
     * f0 -> "boolean"
     */
    @Override
    public TypeStruct visit(BooleanType n, Context context) {
        return new TypeStruct("BooleanType");
        // TypeStruct _ret = null;
        // n.f0.accept(this, context);
        // return _ret;
    }

    /**
     * f0 -> "int"
     */
    @Override
    public TypeStruct visit(IntegerType n, Context context) {
        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public TypeStruct visit(Identifier n, Context context) {
        return new TypeStruct(n);
    }
}
