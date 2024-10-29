package AST.Visitor;
import AST.*;
import ADT.*;
import java.util.*;

public class GlobalTableVisitor implements Visitor {
  Map<String, ClassType> global_table;
  public List<String> errors;

  public GlobalTableVisitor() {
    global_table = new HashMap<String, ClassType>();
    errors = new ArrayList<String>();
  }

  public void visit(Display n){}
  public void visit(Program n){
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
  }
  public void visit(MainClass n){
    global_table.put(n.i1.s, new ClassType(n.i1.s));
  }
  public void visit(ClassDeclSimple n){
    if (!global_table.containsKey(n.i.s)) {
      global_table.put(n.i.s, new ClassType(n.i.s));
    } else {
      errors.add("Error! Class name has already been defined: " + n.i.s + " (line " + n.line_number + ")");
      //Could just print it here but would be in middle of the symbol table
    }
  }
  public void visit(ClassDeclExtends n){
    if (!global_table.containsKey(n.i.s)) {
      global_table.put(n.i.s, new ClassType(n.i.s, n.j.s));
    } else {
      errors.add("Error! Class name has already been defined: " + n.i.s + " (line " + n.line_number + ")");
      //Could just print it here but would be in middle of the symbol table
    }
    //Add a list for children
  }

  //Maybe need if we don't pass it in as parameter
  public Map<String, ClassType> getGlobalTable() {
    return global_table;
  }

  public void visit(VarDecl n){}
  public void visit(MethodDecl n){}
  public void visit(Formal n){}
  public void visit(IntArrayType n){}
  public void visit(BooleanType n){}
  public void visit(IntegerType n){}
  public void visit(IdentifierType n){}
  public void visit(Block n){}
  public void visit(If n){}
  public void visit(While n){}
  public void visit(Print n){}
  public void visit(Assign n){}
  public void visit(ArrayAssign n){}
  public void visit(And n){}
  public void visit(LessThan n){}
  public void visit(Plus n){}
  public void visit(Minus n){}
  public void visit(Times n){}
  public void visit(ArrayLookup n){}
  public void visit(ArrayLength n){}
  public void visit(Call n){}
  public void visit(IntegerLiteral n){}
  public void visit(True n){}
  public void visit(False n){}
  public void visit(IdentifierExp n){}
  public void visit(This n){}
  public void visit(NewArray n){}
  public void visit(NewObject n){}
  public void visit(Not n){}
  public void visit(Identifier n){}
}
