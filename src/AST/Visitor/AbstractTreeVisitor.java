package AST.Visitor;
import AST.*;

public class AbstractTreeVisitor implements Visitor {
  private int indent;

  public AbstractTreeVisitor() {
    super();
    indent = 0; //Keeps track of how many indents are needed
  }

  // Indents the string so that it matches correct form
  // Will be called by every function
  private void printIndent() {
    for (int i = 0; i < indent; i++) {
      System.out.print("   ");
    }
    //System.out.println(s);
  }

  private void printLineNum(ASTNode n) {
    System.out.println("(line " + n.line_number + ")");
  }

  // Unused for MiniJava
  public void visit(Display n){}

  public void visit(Program n) {
    //printIndent(); Dont need cause will be no indent
    System.out.println("Program");
    indent++;
    n.m.accept(this);

    // Visit all Classes
    for (int i = 0; i < n.cl.size(); i++) {
      n.cl.get(i).accept(this);
    }
    indent--;
  }

  public void visit(MainClass n) {
    printIndent();
    System.out.print("MainClass " + n.i1.s + " ");
    printLineNum(n);
    indent++;
    //n.i1.accept(this);
    //n.i2.accept(this);
    n.s.accept(this);
    indent--; //reduce indent level after s (statement) is accepted
  }

  public void visit(ClassDeclSimple n) {
    printIndent();
    System.out.print("Class " + n.i.s + " ");
    printLineNum(n);

    //Visit Methods
    indent++;
    for (int i = 0; i < n.ml.size(); i++) {
      n.ml.get(i).accept(this);
    }
    indent--;
  }

  public void visit(ClassDeclExtends n) {
    printIndent();
    System.out.print("Class " + n.i.s + " extends " + n.j.s + " ");
    printLineNum(n);

    //Visit Methods
    indent++;
    for (int i = 0; i < n.ml.size(); i++) {
      n.ml.get(i).accept(this);
    }
    indent--;
  }

  public void visit(VarDecl n) {
    printIndent();
    n.t.accept(this);
    System.out.print(" " + n.i.s);
    printLineNum(n);
  }

  public void visit(MethodDecl n) {
    printIndent();
    System.out.print("MethodDecl " + n.i.s + " ");
    printLineNum(n);
    indent++;

    printIndent();
    System.out.print("returns: ");
    n.t.accept(this);//Will print the type
    System.out.println();

    // Visit params
    if (n.fl.size() > 0) {
      printIndent();
      System.out.println("parameters:");
      indent++;
      for (int i =0; i < n.fl.size(); i++) {
        n.fl.get(i).accept(this);
      }
      indent--;
    }

    // Visit Variables
    if (n.vl.size() > 0) {
      printIndent();
      System.out.println("variables: ");
      indent++;
      for (int i =0; i < n.vl.size(); i++) {
        n.vl.get(i).accept(this);
      }
      indent--;
    }

    // Visit Statements
    for (int i =0; i < n.sl.size(); i++) {
      n.sl.get(i).accept(this);
    }

    printIndent();
    System.out.print("Return ");
    n.e.accept(this);
    System.out.println();
    indent--;
  }

  public void visit(Formal n) {
    printIndent();
    System.out.print("formal: ");
    n.t.accept(this);
    System.out.println(" " + n.i.s);
  }

  public void visit(IntArrayType n) {
    System.out.print("int[]");
  }

  public void visit(BooleanType n) {
    System.out.print("boolean");
  }

  public void visit(IntegerType n) {
    System.out.print("int");
  }

  public void visit(IdentifierType n) {
    System.out.print(n.s);
  }

  public void visit(Block n) {
    printIndent();
    System.out.print("Block: ");
    printLineNum(n);
    indent++;
    for (int i = 0; i < n.sl.size(); i++) {
      n.sl.get(i).accept(this);
    }
    indent--;
  }

  public void visit(If n) {
    printIndent();
    System.out.print("If: ");
    printLineNum(n);
    indent++;
    printIndent();
    System.out.print("condition: ");
    n.e.accept(this);
    System.out.println();

    n.s1.accept(this);

    indent--;
    printIndent();
    System.out.println("Else:");
    indent++;
    n.s2.accept(this);
    indent--;
  }

  public void visit(While n) {
    printIndent();
    System.out.print("While: ");
    printLineNum(n);
    indent++;
    printIndent();
    System.out.print("condition: ");
    n.e.accept(this);
    System.out.println();

    n.s.accept(this);
    indent--; // For while statement
  }

  public void visit(Print n) {
    printIndent();
    System.out.print("Print: ");
    printLineNum(n);
    indent++;
    printIndent();
    n.e.accept(this);
    System.out.println();
    indent--;
  }

  public void visit(Assign n) {
    printIndent();
    System.out.print("Assign: ");
    printLineNum(n);
    indent++;

    printIndent();
    System.out.print("LHS: ");
    n.i.accept(this);
    System.out.println();

    printIndent();
    System.out.print("RHS: ");
    n.e.accept(this);
    System.out.println();
    indent--;
  }

  public void visit(ArrayAssign n) {
    printIndent();
    System.out.print("Array Assign: ");
    printLineNum(n);
    indent++;

    printIndent();
    System.out.print("identifier: ");
    n.i.accept(this);
    System.out.println();

    printIndent();
    System.out.print("index: ");
    n.e1.accept(this);
    System.out.println();

    printIndent();
    System.out.print("value: ");
    n.e2.accept(this);
    System.out.println();
    indent--;
  }

  public void visit(And n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" && ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(LessThan n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" < ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Plus n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" + ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Minus n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" - ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(Times n) {
    System.out.print("(");
    n.e1.accept(this);
    System.out.print(" * ");
    n.e2.accept(this);
    System.out.print(")");
  }

  public void visit(ArrayLookup n) {
    n.e1.accept(this);
    System.out.print("[");
    n.e2.accept(this);
    System.out.print("]");
  }

  public void visit(ArrayLength n) {
    n.e.accept(this);
    System.out.print(".length");
  }

  public void visit(Call n) {
    n.e.accept(this);
    System.out.print(".");
    n.i.accept(this);
    System.out.print("(");
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.get(i).accept(this);
        if ( i+1 < n.el.size() ) { System.out.print(", "); }
    }
    System.out.print(")");
  }

  public void visit(IntegerLiteral n) {
    System.out.print(n.i);
  }

  public void visit(True n) {
    System.out.print("true");
  }

  public void visit(False n) {
    System.out.print("false");
  }

  public void visit(IdentifierExp n) {
    System.out.print(n.s);
  }

  public void visit(This n) {
    System.out.print("this");
  }

  public void visit(NewArray n) {
    System.out.print("new int [");
    n.e.accept(this);
    System.out.print("]");
  }

  public void visit(NewObject n) {
    System.out.print("new ");
    System.out.print(n.i.s);
    System.out.print("()");
  }

  public void visit(Not n) {
    System.out.print("!");
    n.e.accept(this);
  }

  public void visit(Identifier n) {
    System.out.print(n.s);
  }
}