import minijava.syntaxtree.*;
import minijava.visitor.GJVoidDepthFirst;
import java.util.HashMap;

public class VoidClassVisitor extends GJVoidDepthFirst<ClassSymbol> {
    private HashMap<String, ClassSymbol> classTable;

    public HashMap<String, ClassSymbol> ClassTable() {
        return this.classTable;
    }

    public VoidClassVisitor() {
        super();
        this.classTable = new HashMap<>();
    }


    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public void visit(Goal n, ClassSymbol currClass) {
        System.out.println("Goal");
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
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
    public void visit(MainClass n, ClassSymbol currClass) {
        System.out.println("MainClass");
        System.out.println("adding class: main");

        currClass = new ClassSymbol(n.f1);
        this.classTable.put("main", currClass);

        MethodSymbol main = new MethodSymbol(n.f6.tokenImage, new Identifier(new NodeToken("void")));
        main.AddVariable(n.f11, new Identifier(new NodeToken("StringArray")));
        System.out.println("name: " + main.MethodName() + ", return type: " + main.ReturnType().GetType());
        currClass.AddMethod(main);





        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
        n.f6.accept(this, currClass);
        n.f7.accept(this, currClass);
        n.f8.accept(this, currClass);
        n.f9.accept(this, currClass);
        n.f10.accept(this, currClass);
        n.f11.accept(this, currClass);
        n.f12.accept(this, currClass);
        n.f13.accept(this, currClass);
        n.f14.accept(this, currClass);
        n.f15.accept(this, currClass);
        n.f16.accept(this, currClass);
        n.f17.accept(this, currClass);
    }

    /**
     * f0 -> ClassDeclaration()
     * | ClassExtendsDeclaration()
     */
    public void visit(TypeDeclaration n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public void visit(ClassDeclaration n, ClassSymbol currClass) {
        System.out.println("ClassDeclaration");
        currClass = new ClassSymbol(n.f1);
        this.classTable.put(currClass.ClassName(), currClass);
        System.out.println("adding class: " + currClass.ClassName());
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
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
    public void visit(ClassExtendsDeclaration n, ClassSymbol currClass) {
        System.out.println("ClassExtendsDeclaration");
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
        n.f6.accept(this, currClass);
        n.f7.accept(this, currClass);
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public void visit(VarDeclaration n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
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
    public void visit(MethodDeclaration n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
        n.f6.accept(this, currClass);
        n.f7.accept(this, currClass);
        n.f8.accept(this, currClass);
        n.f9.accept(this, currClass);
        n.f10.accept(this, currClass);
        n.f11.accept(this, currClass);
        n.f12.accept(this, currClass);
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> ( FormalParameterRest() )*
     */
    public void visit(FormalParameterList n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public void visit(FormalParameter n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public void visit(FormalParameterRest n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
    }

    /**
     * f0 -> ArrayType()
     * | BooleanType()
     * | IntegerType()
     * | Identifier()
     */
    public void visit(Type n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public void visit(ArrayType n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> "boolean"
     */
    public void visit(BooleanType n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "int"
     */
    public void visit(IntegerType n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> Block()
     * | AssignmentStatement()
     * | ArrayAssignmentStatement()
     * | IfStatement()
     * | WhileStatement()
     * | PrintStatement()
     */
    public void visit(Statement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public void visit(Block n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public void visit(AssignmentStatement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
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
    public void visit(ArrayAssignmentStatement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
        n.f6.accept(this, currClass);
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
    public void visit(IfStatement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
        n.f6.accept(this, currClass);
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public void visit(WhileStatement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public void visit(PrintStatement n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
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
    public void visit(Expression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    public void visit(AndExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public void visit(CompareExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public void visit(PlusExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public void visit(MinusExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public void visit(TimesExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public void visit(ArrayLookup n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public void visit(ArrayLength n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public void visit(MessageSend n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
        n.f5.accept(this, currClass);
    }

    /**
     * f0 -> Expression()
     * f1 -> ( ExpressionRest() )*
     */
    public void visit(ExpressionList n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public void visit(ExpressionRest n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
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
    public void visit(PrimaryExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public void visit(IntegerLiteral n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "true"
     */
    public void visit(TrueLiteral n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "false"
     */
    public void visit(FalseLiteral n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public void visit(Identifier n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "this"
     */
    public void visit(ThisExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public void visit(ArrayAllocationExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
        n.f4.accept(this, currClass);
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public void visit(AllocationExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
        n.f3.accept(this, currClass);
    }

    /**
     * f0 -> "!"
     * f1 -> Expression()
     */
    public void visit(NotExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public void visit(BracketExpression n, ClassSymbol currClass) {
        n.f0.accept(this, currClass);
        n.f1.accept(this, currClass);
        n.f2.accept(this, currClass);
    }

}
