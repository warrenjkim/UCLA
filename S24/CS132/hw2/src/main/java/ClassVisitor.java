import java.util.ArrayDeque;
import java.util.Enumeration;
import java.util.HashMap;
import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order. Your visitors may extend this class.
 */
public class ClassVisitor extends GJDepthFirst<TypeStruct, Context> {
    public static final void DebugLogln(String msg) {
        // System.out.println(msg);
    }

    public static final void DebugLog(String msg) {
        // System.out.print(msg);
    }

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
            if (err != null  && err.MatchType(new TypeStruct("Type error"))) {
                DebugLogln("(NodeList) " + err.Type());
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
                if (err != null && err.MatchType(new TypeStruct("Type error"))) {
                    DebugLogln("(NodeListOptional) " + err.Type());
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
            if (err != null && err.MatchType(new TypeStruct("Type error"))) {
                DebugLogln("(NodeSequence) " + err.Type());
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

        // cyclic dependency check
        for (HashMap.Entry<String, ClassSymbol> currClass : this.classTable.entrySet()) {
            ClassSymbol currSymbol = currClass.getValue();
            TypeStruct currType = currSymbol.TypeStruct(); // get the actual reference to currClass.type

            if (currSymbol.ParentTypeStruct() != null) {
                ClassSymbol parentSymbol = this.classTable.get(currSymbol.Parent()); // actual reference to parent

                while (parentSymbol != null) {
                    TypeStruct parentType = parentSymbol.TypeStruct();
                    DebugLogln(currType.Type() + "->" + parentType.Type());
                    currType.SetSuperTypeStruct(parentType);
                    parentSymbol = this.classTable.get(parentSymbol.Parent()); // actual reference to parent
                    currType = parentType;
                    if (currType.MatchType(currSymbol.Type())) {
                        DebugLogln("cycle !!");
                        return new TypeStruct("Type error");
                    }
                }
            }
        }

        for (HashMap.Entry<String, ClassSymbol> currClass : this.classTable.entrySet()) {
            ClassSymbol currSymbol = currClass.getValue();
            TypeStruct currType = currSymbol.TypeStruct(); // get the actual reference to currClass.type

            while (currSymbol != null) {
                DebugLogln(currType.Type() + "->" + currType.SuperType());
                currSymbol = this.classTable.get(currType.SuperType());
                currType = currType.SuperTypeStruct();
            }
        }

        DebugLogln("No cycles");

        DebugLogln("\n\n\nclassTable:");
        for (HashMap.Entry<String, ClassSymbol> currClass : this.classTable.entrySet()) {
            DebugLog("  Class Name: " + currClass.getValue().Name());
            TypeStruct parent = currClass.getValue().TypeStruct();
            while (parent.SuperTypeStruct() != null) {
                DebugLog(" -> " + currClass.getValue().Parent());
                parent = parent.SuperTypeStruct();
            }
            for (SymbolTable fieldScope : currClass.getValue().Fields()) {
                DebugLogln("\n    Fields");
                for (Pair field : fieldScope.Table()) {
                    DebugLogln("      " + field.Type() + " " + field.Name());
                }
            }

            DebugLogln("\n    Methods");
            for (HashMap.Entry<String, MethodSymbol> method : currClass.getValue().Methods().entrySet()) {
                DebugLog("      " + method.getValue().Type() + " " + method.getValue().Name() + " (");
                for (SymbolTable scope : method.getValue().Scopes()) {
                    for (Pair param : scope.Table()) {
                        DebugLog(param.Type() + " " + param.Name() + ", ");
                    }
                    DebugLogln(")");
                }
            }
            DebugLogln("");
        }
        DebugLogln("End of classTable\n\n");

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
        context.SetMethod(n.f6.tokenImage, new TypeStruct("void"));
        context.Method().AddVariable(n.f11, new TypeStruct("StringArrayType"));

        context.SetClass(n.f1, new Identifier(new NodeToken("main")));
        context.Class().AddMethod(context.Method());

        TypeStruct err = n.f14.accept(this, context); // build fields
        if (err != null) {
            return err;
        }

        this.classTable.put(context.Class().Name(), context.Class());

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
        if (this.classTable.containsKey(context.Class().Name())) {
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

        this.classTable.put(context.Class().Name(), context.Class());

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

        // // cyclic dependency
        // TypeStruct parent = n.f3.accept(this, context);
        // if (parent != null) {
        //     ClassSymbol parentClass = this.classTable.get(parent.Type());
        //     if (parentClass != null && parentClass.TypeStruct().MatchType(context.Class().TypeStruct())) {
        //         DebugLogln("Cyclic dependency error");
        //         return new TypeStruct("Type error");
        //     }
        // }

        // duplicate class name
        if (this.classTable.containsKey(context.Class().Name())) {
            return new TypeStruct("Type error");
        }

        TypeStruct err = n.f5.accept(this, context); // build fields
        if (err != null) {
            return err;
        }

        err = n.f6.accept(this, context); // build methods
        if (err != null) {
            return err;
        }

        this.classTable.put(context.Class().Name(), context.Class());

        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public TypeStruct visit(VarDeclaration n, Context context) {
        return context.Class().AddField(n.f1, n.f0.accept(this, context));
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
        if (context.Class().Methods().containsKey(context.Method().Name())) {
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
        return context.Method().AddFormalParameter(n.f1, n.f0.accept(this, context));
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
