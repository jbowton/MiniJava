package AST.Visitor;
import AST.*;
import ADT.*;
import java.util.*;

public class PreCodeGenVisitor implements Visitor{

  Map<String, ClassType> global_table;

  ClassType current_classType;
  MethodType current_methodType;

  public PreCodeGenVisitor (Map<String, ClassType> global_table){
    this.global_table = global_table;
  }

  public void visit(Display n){}

  public void visit(Program n){
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }
  }

  public void visit(MainClass n){}

  public void visit(ClassDeclSimple n){
    current_classType = global_table.get(n.i.s);
    // Set offsets for fields
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl curr_var = n.vl.get(i);
      curr_var.accept(this);
      TypeNode curr_var_type = current_classType.fields.get(curr_var.i.s);
      ((BaseType)curr_var_type).offset = -8 * (i+2); // +2 to account for the method table pointer at the top
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
      n.ml.get(i).accept(this);
    }
  }

  public void visit(ClassDeclExtends n){}

  public void visit(VarDecl n){}

  public void visit(MethodDecl n){
    current_methodType = current_classType.methods.get(n.i.s);

    // Add parameters to the stack
    for ( int i = 0; i < n.fl.size(); i++ ) {
      Formal curr_formal = n.fl.get(i);
      curr_formal.accept(this);
      TypeNode curr_formal_type = current_methodType.variables.get(curr_formal.i.s);
      ((BaseType)curr_formal_type).offset = -8 * (i+2); // +2 to account for the hidden "this" parameter
    }
    // Add variables to the stack
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl curr_var = n.vl.get(i);
      curr_var.accept(this);
      TypeNode curr_var_type = current_methodType.variables.get(curr_var.i.s);
      ((BaseType)curr_var_type).offset = -8 * (i+2 + n.fl.size()); // Account for the parameters already added to the stack
    }
  }

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
