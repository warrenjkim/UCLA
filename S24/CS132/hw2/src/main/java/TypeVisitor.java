import minijava.syntaxtree.*;
import minijava.visitor.GJDepthFirst;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order. Your visitors may extend this class.
 */
public class TypeVisitor extends GJDepthFirst<TypeStruct, Context> {
    public static final void DebugLogln(String msg) {
        // System.out.println(msg);
    }

    public static final void DebugLog(String msg) {
        // System.out.print(msg);
    }

    HashMap<String, ClassSymbol> classTable;

    public TypeVisitor(HashMap<String, ClassSymbol> classTable) {
        this.classTable = classTable;
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
            DebugLogln("Goal mainclass err");
            return new TypeStruct("Type error");
        }

        err = n.f1.accept(this, context);
        if (err != null) {
            DebugLogln("Goal typedeclaration err");
            return new TypeStruct("Type error");
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
        DebugLogln("MainClass");
        context.SetClass(this.classTable.get(n.f1.f0.tokenImage));
        context.SetMethod(context.Class().FindMethod(n.f6.tokenImage));

        TypeStruct err = n.f14.accept(this, context); // variables
        if (err != null) {
            DebugLogln("MainClass vars error");
            return err;
        }

        err = n.f15.accept(this, context); // statements
        if (err != null) {
            DebugLogln("MainClass stmt error");
            return err;
        }

        DebugLogln("End of MainClass\n");
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
        context.SetClass(this.classTable.get(n.f1.f0.tokenImage)); // set class context
        return n.f4.accept(this, context); // methods
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
        context.SetClass(this.classTable.get(n.f1.f0.tokenImage)); // set class context
        DebugLogln("\nClass: " + context.ClassName() + " extends " + context.Class().Parent());
        return n.f6.accept(this, context); // methods
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    @Override
    public TypeStruct visit(VarDeclaration n, Context context) {
        return context.Method().AddVariable(n.f1, n.f0.accept(this, context)); // add to method scope
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
        context.SetMethod(this.classTable.get(context.ClassName()).FindMethod(n.f2)); // set method context
        DebugLogln("Method: " + context.MethodName());

        DebugLogln("\n    VarDeclarations");
        TypeStruct err = n.f7.accept(this, context); // vars
        if (err != null) {
            DebugLogln("MethodDeclaration vars error");
            return err;
        }
        DebugLogln("\n    End of VarDeclarations");

        DebugLogln("\n    Statements");
        err = n.f8.accept(this, context); // statements
        if (err != null) {
            DebugLogln("MethodDeclaration stmt error");
            return err;
        }

        TypeStruct returnType = n.f10.accept(this, context); // make sure Expression() matches the return type
        if (returnType == null || !returnType.MatchType(context.Method().Type())) {
            DebugLogln("MethodDeclaration expr not method return type");
            return new TypeStruct("Type error");
        }

        DebugLogln("    End of Statements\n");
        DebugLogln("End of Method: " + context.MethodName() + "\n");
        return null;
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
     * f0 -> Block()
     * | AssignmentStatement()
     * | ArrayAssignmentStatement()
     * | IfStatement()
     * | WhileStatement()
     * | PrintStatement()
     */
    @Override
    public TypeStruct visit(Statement n, Context context) {
        return n.f0.accept(this, context);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    @Override
    public TypeStruct visit(Block n, Context context) {
        DebugLogln("Block");
        return n.f1.accept(this, context);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public TypeStruct visit(AssignmentStatement n, Context context) {
        DebugLog("      (AssignmentStatement) Looking for: " + n.f0.f0.tokenImage + "... ");
        // still need to add subtyping......................................... (fixed i think)
        TypeStruct id = context.Method().FindVariable(n.f0);
        if (id == null) {
            id = context.Class().FieldTypeStruct(n.f0);
        }

        if (id == null && context.Class().Parent() != null) {
            id = this.classTable.get(context.Class().Parent()).FieldTypeStruct(n.f0);
        }

        if (id == null) {
            DebugLogln("AssignmentStatement nullid");
            return new TypeStruct("Type error");
        }

        DebugLogln("Found " + n.f0.f0.tokenImage);


        TypeStruct expr = n.f2.accept(this, context);
        if (expr == null) {
            DebugLogln("AssignmentStatement null expr");
            return new TypeStruct("Type error");
        }

        if (!id.MatchType(expr)) {
            DebugLogln("AssignmentStatement id neq expr");
            return new TypeStruct("Type error");
        }

        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public TypeStruct visit(ArrayAssignmentStatement n, Context context) {
        DebugLog("      (ArrayAssignmentStatement) Looking for: " + n.f0.f0.tokenImage + "... ");
        TypeStruct id = context.Method().FindVariable(n.f0);
        if (id == null) {
            id = context.Class().FieldTypeStruct(n.f0);
        }

        if (id == null) {
            DebugLogln("ArrayAssignmentStatement nullid");
            return new TypeStruct("Type error");
        }

        if (!id.MatchType("ArrayType")) {
            DebugLogln("AssignmentStatement id neq ArrayType");
            return new TypeStruct("Type error");
        }

        DebugLogln("Found " + n.f0.f0.tokenImage);

        TypeStruct index = n.f2.accept(this, context);
        if (index == null || !index.MatchType("IntegerType")) {
            DebugLogln("AssignmentStatement index neq IntegerType");
            return new TypeStruct("Type error");
        }

        TypeStruct expr = n.f5.accept(this, context);
        if (expr == null || !expr.MatchType("IntegerType")) {
            DebugLogln("AssignmentStatement expr " + expr.Type() + " neq IntegerType");
            return new TypeStruct("Type error");
        }

        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public TypeStruct visit(IfStatement n, Context context) {
        DebugLogln("    IfStatement");
        TypeStruct expr = n.f2.accept(this, context);
        if (!expr.MatchType("BooleanType")) {
            DebugLogln("IfStatement expr neq BooleanType");
            return new TypeStruct("Type error");
        }

        // statement inside if
        DebugLogln("\n    If Statement");
        TypeStruct err = n.f4.accept(this, context);
        if (err != null) {
            DebugLogln("IfStatement stmt error");
            return new TypeStruct("Type error");
        }
        DebugLogln("    End of If Statement\n");

        // statement inside else
        DebugLogln("\n    Else Statement");
        err = n.f6.accept(this, context);
        if (err != null) {
            DebugLogln("IfStatement stmt error");
            return new TypeStruct("Type error");
        }
        DebugLogln("    End of Else Statement\n");

        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public TypeStruct visit(WhileStatement n, Context context) {
        DebugLogln("\n    WhileStatement");
        TypeStruct expr = n.f2.accept(this, context);
        if (!expr.MatchType("BooleanType")) {
            DebugLogln("WhileStatement expr not boolean");
            return new TypeStruct("Type error");
        }

        // statement inside if
        TypeStruct err = n.f4.accept(this, context);
        if (err != null) {
            DebugLogln("WhileStatement stmt error");
            return new TypeStruct("Type error");
        }

        DebugLogln("    End of WhileStatement\n");

        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public TypeStruct visit(PrintStatement n, Context context) {
        TypeStruct expr = n.f2.accept(this, context);
        if (expr == null) {
            DebugLogln("PrintStatement expr null");
            return new TypeStruct("Type error");
        }

        if (!expr.MatchType("IntegerType")) {
            DebugLogln("PrintStatement expr not integer");
            return new TypeStruct("Type error");
        }

        return null;
    }

    /**
     * f0 -> AndExpression()
     * | CompareExpression()
     * | PlusExpression()
     * | MinusExpression()
     * | TimesExpression()
     * | ArrayLookup()
     * | ArrayLength()
     * | MessageSend()
     * | PrimaryExpression()
     */
    @Override
    public TypeStruct visit(Expression n, Context context) {
        return n.f0.accept(this, context);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    @Override
    public TypeStruct visit(AndExpression n, Context context) {
        TypeStruct lhs = n.f0.accept(this, context);
        if (lhs == null || !lhs.MatchType("BooleanType")) {
            DebugLogln("AndExpression rhs null or not BooleanType");
            return new TypeStruct("Type error");
        }

        TypeStruct rhs = n.f2.accept(this, context);
        if (rhs == null || !rhs.MatchType("BooleanType")) {
            DebugLogln("AndExpression rhs null or not BooleanType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("BooleanType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public TypeStruct visit(CompareExpression n, Context context) {
        TypeStruct lhs = n.f0.accept(this, context);
        if (lhs == null || !lhs.MatchType("IntegerType")) {
            DebugLogln("CompareExpression lhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        TypeStruct rhs = n.f2.accept(this, context);
        if (rhs == null || !rhs.MatchType("IntegerType")) {
            DebugLogln("CompareExpression rhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("BooleanType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public TypeStruct visit(PlusExpression n, Context context) {
        TypeStruct lhs = n.f0.accept(this, context);
        if (lhs == null || !lhs.MatchType("IntegerType")) {
            DebugLogln("PlusExpression lhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        TypeStruct rhs = n.f2.accept(this, context);
        if (rhs == null || !rhs.MatchType("IntegerType")) {
            DebugLogln("PlusExpression rhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public TypeStruct visit(MinusExpression n, Context context) {
        TypeStruct lhs = n.f0.accept(this, context);
        if (lhs == null || !lhs.MatchType("IntegerType")) {
            DebugLogln("MinusExpression lhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        TypeStruct rhs = n.f2.accept(this, context);
        if (rhs == null || !rhs.MatchType("IntegerType")) {
            DebugLogln("MinusExpression rhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public TypeStruct visit(TimesExpression n, Context context) {
        TypeStruct lhs = n.f0.accept(this, context);
        if (lhs == null || !lhs.MatchType("IntegerType")) {
            DebugLogln("TimesExpression lhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        TypeStruct rhs = n.f2.accept(this, context);
        if (rhs == null || !rhs.MatchType("IntegerType")) {
            DebugLogln("TimesExpression rhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public TypeStruct visit(ArrayLookup n, Context context) {
        DebugLogln("ArrayLookup");
        TypeStruct id = n.f0.accept(this, context);
        if (id == null || !id.MatchType("ArrayType")) {
            DebugLogln("TimesExpression lhs null or not ArrayType");
            return new TypeStruct("Type error");
        }

        TypeStruct index = n.f2.accept(this, context);
        if (index == null || !index.MatchType("IntegerType")) {
            DebugLogln("TimesExpression rhs null or not IntegerType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    @Override
    public TypeStruct visit(ArrayLength n, Context context) {
        DebugLogln("ArrayLength");
        TypeStruct id = n.f0.accept(this, context);
        if (id == null || !id.MatchType("ArrayType")) {
            DebugLogln("ArrayLength expr null or not ArrayType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    @Override
    public TypeStruct visit(MessageSend n, Context context) {
        DebugLogln("\n      MessageSend");
        DebugLog("context is: " + context.ClassName() + "\n    ");
        for (SymbolTable d : context.Class().Fields()) {
            for (Pair p : d.Table()) {
                DebugLog(p.Type() + " " + p.Name() + ", ");
            }
        }
        DebugLogln("");
        DebugLog("context method is: " + context.MethodName() + "(");
        for (Pair p : context.Method().FormalParameters().Table()) {
            DebugLog(p.Type() + " " + p.Name() + ", ");
        }
        DebugLogln(")");
        ClassSymbol targetClass = this.classTable.get(n.f0.accept(this, context).Type());
        if (targetClass == null) {
            DebugLogln("MessageSend nullp");
            return new TypeStruct("Type error");
        }

        MethodSymbol targetMethod = targetClass.FindMethod(n.f2);
        if (targetMethod == null) {
            DebugLogln("MessageSend method dne");
            return new TypeStruct("Type error");
        }

        Context calledContext = new Context(context.Class(), new MethodSymbol(targetMethod));
        for (SymbolTable scope : context.Method().Scopes()) {
            calledContext.Method().PushScope(scope);
        }
        calledContext.Method().PushScope(context.Method().FormalParameters());

        DebugLog("Class is: " + calledContext.ClassName() + "\n    ");
        for (SymbolTable d : calledContext.Class().Fields()) {
            for (Pair p : d.Table()) {
                DebugLog(p.Type() + " " + p.Name() + ", ");
            }
        }
        DebugLogln("");
        DebugLog("Called context is: " + calledContext.MethodName() + "(");
        for (Pair p : targetMethod.FormalParameters().Table()) {
            DebugLog(p.Type() + " " + p.Name() + ", ");
        }
        DebugLogln(")");

        TypeStruct err = n.f4.accept(this, calledContext);
        if (err != null) {
            DebugLogln("MessageSend params don't match");
            return new TypeStruct("Type error");
        }

        if (!calledContext.Method().FormalParameters().Empty()) {
            DebugLogln("MessageSend provided params < formal params");
            return new TypeStruct("Type error");
        }

        DebugLogln("      End of MessageSend\n");
        return targetMethod.TypeStruct();
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    @Override
    public TypeStruct visit(ExpressionList n, Context context) {
        DebugLogln("\n        ExpressionList");
        SymbolTable formalParameters = context.Method().FormalParameters();
        if (formalParameters.Empty()) {
            DebugLogln("ExpressionList provided params > formal params");
            return new TypeStruct("Type error");
        }

        TypeStruct expr = n.f0.accept(this, context);
        if (expr == null || expr.MatchType("Type error")) {
            DebugLogln("ExpressionList expr error");
            return expr;
        }

        Pair formal = formalParameters.PopParameter();
        if (formal == null) {
            DebugLogln("ExpressionList formal param is null");
            return new TypeStruct("Type error");
        }

        if (!expr.MatchType(formal.TypeStruct())) {
            DebugLogln("ExpressionList params don't match");
            return new TypeStruct("Type error");
        }

        DebugLogln("        End of ExpressionList\n");
        return n.f1.accept(this, context);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public TypeStruct visit(ExpressionRest n, Context context) {
        DebugLogln("ExpressionRest");
        SymbolTable formalParameters = context.Method().FormalParameters();
        if (formalParameters.Empty()) {
            DebugLogln("ExpressionRest provided params > formal params");
            return new TypeStruct("Type error");
        }

        TypeStruct expr = n.f1.accept(this, context);
        if (expr == null || expr.MatchType("Type error")) {
            DebugLogln("ExpressionRest expr error");
            return expr;
        }

        Pair formal = formalParameters.PopParameter();
        if (formal == null) {
            DebugLogln("ExpressionRest formal param is null");
            return new TypeStruct("Type error");
        }

        if (!expr.MatchType(formal.TypeStruct())) {
            DebugLogln("ExpressionRest params don't match");
            return new TypeStruct("Type error");
        }

        DebugLogln("    End of ExpressionRest\n");
        return null;
    }

    /**
     * f0 -> IntegerLiteral()
     * | TrueLiteral()
     * | FalseLiteral()
     * | Identifier()
     * | ThisExpression()
     * | ArrayAllocationExpression()
     * | AllocationExpression()
     * | NotExpression()
     * | BracketExpression()
     */
    @Override
    public TypeStruct visit(PrimaryExpression n, Context context) {
        return n.f0.accept(this, context);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    @Override
    public TypeStruct visit(IntegerLiteral n, Context context) {
        return new TypeStruct("IntegerType");
    }

    /**
     * f0 -> "true"
     */
    @Override
    public TypeStruct visit(TrueLiteral n, Context context) {
        return new TypeStruct("BooleanType");
    }

    /**
     * f0 -> "false"
     */
    @Override
    public TypeStruct visit(FalseLiteral n, Context context) {
        return new TypeStruct("BooleanType");
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    @Override
    public TypeStruct visit(Identifier n, Context context) {
        DebugLog("        (Identifier) Looking for: " + n.f0.tokenImage + "... ");
        TypeStruct id = context.Method().FindVariable(n);
        if (id == null) {
            id = context.Class().FieldTypeStruct(n);
        }

        // wtf is this one
        if (id == null) {
            ClassSymbol targetClass = this.classTable.get(n.f0.tokenImage);
            if (targetClass == null) {
                DebugLogln("why is this ehre: " + n.f0.tokenImage);
                DebugLogln("Identifier nullid");
                // return new TypeStruct("Type error");
            } else {
                id = targetClass.TypeStruct();
            }
        }

        if (id == null && context.Class().Parent() != null) {
            id = this.classTable.get(context.Class().Parent()).FieldTypeStruct(n);
        }

        if (id == null) {
            DebugLogln("Identifier nullid");
            return new TypeStruct("Type error");
        }

        DebugLogln("Found " + n.f0.tokenImage + " with type " + id.Type() + ", superType " + id.SuperType());

        return id;
    }

    /**
     * f0 -> "this"
     */
    @Override
    public TypeStruct visit(ThisExpression n, Context context) {
        if (context.Class().Parent() != null &&  context.Class().Parent().equals("main")) {
            DebugLogln("ThisExpression ref to main");
            return new TypeStruct("Type error");
        }

        DebugLogln("      (ThisExpression) " + context.ClassName());
        return context.Class().TypeStruct();
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public TypeStruct visit(ArrayAllocationExpression n, Context context) {
        TypeStruct expr = n.f3.accept(this, context);
        if (expr == null || !expr.MatchType("IntegerType")) {
            DebugLogln("ArrayAllocationExpression expr null or not integer");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("ArrayType");
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public TypeStruct visit(AllocationExpression n, Context context) {
        ClassSymbol classType = this.classTable.get(n.f1.f0.tokenImage);
        if (classType == null) {
            DebugLogln("AllocationExpression nullclass");
            return new TypeStruct("Type error");
        }

        return classType.TypeStruct();
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    @Override
    public TypeStruct visit(NotExpression n, Context context) {
        TypeStruct expr = n.f1.accept(this, context);
        if (expr == null || !expr.MatchType("BooleanType")) {
            DebugLogln("NotExpression expr null or not BooleanType");
            return new TypeStruct("Type error");
        }

        return new TypeStruct("BooleanType");
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public TypeStruct visit(BracketExpression n, Context context) {
        return n.f1.accept(this, context);
    }

}
