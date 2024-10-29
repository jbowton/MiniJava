package AST.Visitor;
import AST.*;
import ADT.*;
import java.util.*;

public class LocalTablesVisitor implements Visitor {
  Map<String, ClassType> global_table;

  ClassType current_classType;
  MethodType current_methodType;
  public List<String> errors;
  public List<String> symbolTable;

  public LocalTablesVisitor(Map<String, ClassType> global_table) {
    this.global_table = global_table;
    symbolTable = new ArrayList<String>();
    errors = new ArrayList<String>();
  }

  public Map<String, ClassType> getGlobalTable() {
    return global_table;
  }

  public void visit(Display n){}

  public void visit(Program n){
    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
      n.cl.get(i).accept(this);
    }
  }

  public void visit(MainClass n){
    current_classType = global_table.get(n.i1.s);

    // Print the class name
    symbolTable.add(n.i1.s);


    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
  }

  public void visit(ClassDeclSimple n){
    current_classType = global_table.get(n.i.s);

    // Print the class name
    symbolTable.add(n.i.s);

    // Add each field to the Class's fields symbol table
    // Key should be field name, Value should be BaseType
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      // Add current variable to the fields list
      if (!current_classType.fields.containsKey(current_variable.i.s)) {
        current_classType.fields.put(current_variable.i.s, new BaseType(current_variable.t.s));
        symbolTable.add("   " + current_variable.i.s + " : " + current_classType.fields.get(current_variable.i.s).toString());
      } else {
        errors.add("Error! Field name already declared: " + current_variable.i.s + " (line " + current_variable.line_number + ")");
      }
    }

    // Add each method to the Class's methods symbol table
    // Key should be method name, Value should be MethodType
    for ( int i = 0; i < n.ml.size(); i++ ) {
      MethodDecl current_method = n.ml.get(i);
      // Add current method to the methods list
      if (!current_classType.methods.containsKey(current_method.i.s)) {
        current_classType.methods.put(current_method.i.s, new MethodType(current_method.t.s));
        MethodType curr_methType = current_classType.methods.get(current_method.i.s);

        for ( int j = 0; j < current_method.fl.size(); j++ ) {
        Formal current_formal = current_method.fl.get(j);
        // Append the current parameter to the parameter list
        curr_methType.parameterTypes.add(new BaseType(current_formal.t.s));
        }
      } else {
        errors.add("Error! Method name already declared: " + current_method.i.s + " (line " + current_method.line_number + ")");
      }

      symbolTable.add("   " + current_method.i.s + " : " + current_classType.methods.get(current_method.i.s).toString());
      current_method.accept(this);
    }
  }

  public void visit(ClassDeclExtends n){
    if (!global_table.containsKey(n.j.s)) {
      errors.add("Error! Extended class does not exist: " + n.j.s + " (line " + n.line_number + ")");
    } else {
      //Check for cycle
      ClassType classParent = global_table.get(n.j.s);
      while (classParent != null) {
        if (classParent.name == n.i.s) {
          errors.add("Error! Inheritance cycle detected" + " (line " + n.line_number + ")");
          break;
        } else {
          classParent = global_table.get(classParent.parent);
        }
      }
    }

    current_classType = global_table.get(n.i.s);

    // Print the class name
    symbolTable.add(n.i.s);
    // // Check if the inherited class is declared
    // if(!global_table.containsKey(n.j.s)) {
    //   symbolTable.add("ClassDeclExtends Error!");
    // }
    // // Add the inherited class name to the fields of the ClassType
    // global_table.get(n.i.s);

    // Add each field to the Class's fields symbol table
    // Key should be field name, Value should be BaseType
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      // Add current variable to the fields list
      if (!current_classType.fields.containsKey(current_variable.i.s)) {
        current_classType.fields.put(current_variable.i.s, new BaseType(current_variable.t.s));
        symbolTable.add("   " + current_variable.i.s + " : " + current_classType.fields.get(current_variable.i.s).toString());
      } else {
        errors.add("Error! Field name already declared: " + current_variable.i.s + " (line " + current_variable.line_number + ")");
      }
    }
    // Add each method to the Class's methods symbol table
    // Key should be method name, Value should be MethodType
    for ( int i = 0; i < n.ml.size(); i++ ) {
      MethodDecl current_method = n.ml.get(i);
      // Add current method to the methods list
      if (!current_classType.methods.containsKey(current_method.i.s)) {
        current_classType.methods.put(current_method.i.s, new MethodType(current_method.t.s));
        MethodType curr_methType = current_classType.methods.get(current_method.i.s);
        for ( int j = 0; i < current_method.fl.size(); i++ ) {
        Formal current_formal = current_method.fl.get(j);
        // Append the current parameter to the parameter list
        curr_methType.parameterTypes.add(new BaseType(current_formal.t.s));
        }
      } else {
        errors.add("Error! Method name already declared: " + current_method.i.s + " (line " + current_method.line_number + ")");
      }

      symbolTable.add("   " + current_method.i.s + " : " + current_classType.methods.get(current_method.i.s).toString());
      current_method.accept(this);
    }
  }

  public void visit(VarDecl n){}

  public void visit(MethodDecl n){
    current_methodType = current_classType.methods.get(n.i.s);

    // Add each parameter to the Method's symbol table
    for ( int i = 0; i < n.fl.size(); i++ ) {
      Formal current_formal = n.fl.get(i);
      // Add current parameter to the variables list
      current_methodType.variables.put(current_formal.i.s, new BaseType(current_formal.t.s));
      symbolTable.add("      " + current_formal.i.s + " : " + current_methodType.variables.get(current_formal.i.s).toString());
    }
    // Add each local variable to the Method's symbol table
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      // Add current variable to the variables list
      if (!current_methodType.variables.containsKey(current_variable.i.s)) {
        current_methodType.variables.put(current_variable.i.s, new BaseType(current_variable.t.s));
        symbolTable.add("      " + current_variable.i.s + " : " + current_methodType.variables.get(current_variable.i.s).toString());
      } else {
        errors.add("Error! Variable has already been declared: " + current_variable.i.s + " (line " + current_variable.line_number + ")");
      }
    }

    for ( int i = 0; i < n.sl.size(); i++ ) {
      n.sl.get(i).accept(this);
    }
    n.e.accept(this);
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