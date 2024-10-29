package AST.Visitor;
import AST.*;
import ADT.*;
import java.util.*;

public class TypeCheckVisitor implements Visitor {
  Map<String, ClassType> global_table;

  ClassType current_classType;
  MethodType current_methodType;

  TypeNode int_ = new BaseType("int");
  TypeNode int_array_ = new BaseType("int[]");
  TypeNode boolean_ = new BaseType("boolean");
  TypeNode void_ = new BaseType("void");
  TypeNode undefined_ = new UndefinedType();

  private boolean error;

  public boolean error() {
    return error;
  }

  public TypeCheckVisitor(Map<String, ClassType> global_table) {
    this.global_table = global_table;
    this.error = false;
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

    // TODO: A LOT!!!

    // Print the class name
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
  }

  public void visit(ClassDeclSimple n){
    current_classType = global_table.get(n.i.s);

    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      current_variable.accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
      MethodDecl current_method = n.ml.get(i);
      current_method.accept(this);
    }
  }

  public void visit(ClassDeclExtends n){
    current_classType = global_table.get(n.i.s);

    // if (!global_table.containsKey(n.j.s)) {
    //   System.out.println("Error! Extended class does not exist: " + n.j.s + " (line " + n.line_number + ")");
    //   error = true;
    // }

    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      current_variable.accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
      MethodDecl current_method = n.ml.get(i);
      current_method.accept(this);
    }
  }

  public void visit(VarDecl n){}

  public void visit(MethodDecl n){
    current_methodType = current_classType.methods.get(n.i.s);

    for ( int i = 0; i < n.fl.size(); i++ ) {
      Formal current_formal = n.fl.get(i);
      if(current_formal.t instanceof IdentifierType && !global_table.containsKey(current_formal.t.s)) {
        current_methodType.variables.put(current_formal.i.s, undefined_);
        System.out.println("Error! Parameter type is unknown (line " + n.line_number + ")");
        error = true;
      }
      current_formal.accept(this);
    }
    for ( int i = 0; i < n.vl.size(); i++ ) {
      VarDecl current_variable = n.vl.get(i);
      if(current_variable.t instanceof IdentifierType && !global_table.containsKey(current_variable.t.s)) {
        current_methodType.variables.put(current_variable.i.s, undefined_);
        System.out.println("Error! Variable type is unknown (line " + n.line_number + ")");
        error = true;
      }
      current_variable.accept(this);
    }

    for ( int i = 0; i < n.sl.size(); i++ ) {
      n.sl.get(i).accept(this);
    }
    n.e.accept(this);



    // Check that return type is the same as method type
    if(!n.e.type.equals(current_methodType)){
      System.out.println("Error! Return value must have same type as method (line " + n.line_number + ")");
      error = true;
    }
  }

  // Don't need to do anything with these
  public void visit(Formal n){}
  public void visit(IntArrayType n){}
  public void visit(BooleanType n){}
  public void visit(IntegerType n){}
  public void visit(IdentifierType n){}

  public void visit(Block n){
    for ( int i = 0; i < n.sl.size(); i++ ) {
      n.sl.get(i).accept(this);
    }
  }

  public void visit(If n){
    n.e.accept(this);
    // Check that the if condition is a boolean
    if(!n.e.type.equals(boolean_)){
      System.out.println("Error! Using a non-boolean for an if statement condition: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.s1.accept(this);
    n.s2.accept(this);
  }

  public void visit(While n){
    n.e.accept(this);
    // Check that the if condition is a boolean
    if(!n.e.type.equals(boolean_)){
      System.out.println("Error! Using a non-boolean for an while statement condition: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.s.accept(this);
  }
  public void visit(Print n){
    n.e.accept(this);
    if(!n.e.type.equals(int_)) {
      System.out.println("Error! System.out.println can only print integers (line " + n.line_number + ")");
      error = true;
    }
  }
  public void visit(Assign n) {
    n.e.accept(this);

    //Find what table to get the type from
    TypeNode scope = null;
    if (current_methodType.variables.containsKey(n.i.s)) {
      scope = current_methodType.variables.get(n.i.s);
    } else if (current_classType.fields.containsKey(n.i.s)) {
      scope = current_classType.fields.get(n.i.s);
    } else {
      ClassType temp = current_classType;
      while (temp != null) {
        if (temp.fields.containsKey(n.i.s)) {
          scope = temp.fields.get(n.i.s);
          //n.e.type = temp.fields.get(n.i.s);
          break;
        } else {
          temp = global_table.get(temp.parent);
          //System.out.println("here");
        }
      }
    }

    if (scope == null) {
      System.out.println("Error! Undeclared identifier: " + n.i.s + " (line " + n.line_number + ")");
      current_methodType.variables.put(n.i.s, new UndefinedType());
      scope = current_methodType.variables.get(n.i.s);
      error = true;
    }

    if(!(scope.canAssign(n.e.type) || scope.name.equals("UNDEFINED"))) {
      //if both are classes

      
      if (global_table.containsKey(scope.name) && global_table.containsKey(n.e.type.name)) {
        ClassType class1 = global_table.get(scope.name);
        ClassType class2 = global_table.get(n.e.type.name);

        while (class2 != null) {
          if (class1.canAssign(class2)) {
            break;
          } else {
            class2 = global_table.get(class2.parent);
          }
        }
        if (class2 == null) {
          System.out.println("Error! Trying to assign wrong type " + n.e.type.name + " (line " + n.line_number + ")");
          error = true;
        }

      } else {
        System.out.println("Error! Trying to assign wrong type " + n.e.type.name + " (line " + n.line_number + ")");
        error = true;
      }
    }
  }

  public void visit(ArrayAssign n){
    n.e1.accept(this);
    n.e2.accept(this);
    TypeNode scope = null;
    if (current_methodType.variables.containsKey(n.i.s)) {
      scope = current_methodType.variables.get(n.i.s);
    } else if (current_classType.fields.containsKey(n.i.s)) {
      scope = current_classType.fields.get(n.i.s);
    } else {
      ClassType temp = current_classType;
      while (temp != null) {
        if (temp.fields.containsKey(n.i.s)) {
          scope = temp.fields.get(n.i.s);
          //n.type = temp.fields.get(n.i.s);
          break;
        } else {
          temp = global_table.get(temp.parent);
        }
      }
    }

    if (scope == null) {
      System.out.println("Error! Undeclared identifier: " + n.i.s + " (line " + n.line_number + ")");
      current_methodType.variables.put(n.i.s, new UndefinedType());
      //n.type = current_methodType.variables.get(n.i.s);
      error = true;
    }

    if(!int_.canAssign(n.e2.type)) {
      System.out.println("Error! Trying to assign wrong type " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }

    // Check that the first expression is a int[]
    if(!scope.equals(int_array_)) {
      System.out.println("Error! Indexing into a non-array: " + scope.name + " (line " + n.line_number + ")");
      error = true;
    }
    // Check that second expression is an int
    if(!n.e1.type.equals(int_)) {
      System.out.println("Error! Using a non-int as an index: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
  }

  public void visit(And n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that both expressions are ints
    if(!n.e1.type.equals(boolean_)) {
      System.out.println("Error! Trying to and a non-boolean: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    if(!n.e2.type.equals(boolean_)) {
      System.out.println("Error! Trying to and a non-boolean: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = boolean_;
  }

  public void visit(LessThan n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that both expressions are ints
    if(!n.e1.type.equals(int_)) {
      System.out.println("Error! Trying to compare a non-int: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    if(!n.e2.type.equals(int_)) {
      System.out.println("Error! Trying to compare a non-int: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = boolean_;
  }

  public void visit(Plus n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that both expressions are ints
    if(!n.e1.type.equals(int_)) {
      System.out.println("Error! Trying to add a non-int: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    if(!n.e2.type.equals(int_)) {
      System.out.println("Error! Trying to add a non-int: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = int_;
  }

  public void visit(Minus n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that both expressions are ints
    if(!n.e1.type.equals(int_)) {
      System.out.println("Error! Trying to subtract a non-int: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    if(!n.e2.type.equals(int_)) {
      System.out.println("Error! Trying to subtract a non-int: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = int_;
  }

  public void visit(Times n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that both expressions are ints
    if(!n.e1.type.equals(int_)) {
      System.out.println("Error! Trying to multiply a non-int: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    if(!n.e2.type.equals(int_)) {
      System.out.println("Error! Trying to multiply a non-int: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = int_;
  }

  public void visit(ArrayLookup n){
    n.e1.accept(this);
    n.e2.accept(this);
    // Check that the first expression is a int[]
    if(!n.e1.type.equals(int_array_)) {
      System.out.println("Error! Indexing into a non-array: " + n.e1.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    // Check that second expression is an int
    if(!n.e2.type.equals(int_)) {
      System.out.println("Error! Using a non-int as an index: " + n.e2.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    // Set current expression's type
    n.type = int_;
  }

  public void visit(ArrayLength n){
    n.e.accept(this);
    // Check that the expression is an int[]
    if(!n.e.type.equals(int_array_)) {
      System.out.println("Error! Indexing into a non-array: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = int_;
  }

  public void visit(Call n){
    n.e.accept(this);
    List<String> parameters = new ArrayList<String>();
    for ( int i = 0; i < n.el.size(); i++ ) {
      n.el.get(i).accept(this);
      parameters.add(n.el.get(i).type.name);
    }
    // Check that the first expression is a class
    if(!global_table.containsKey(n.e.type.name)){
      System.out.println("Error! Calling method of a class that does not exist: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    ClassType classCalled = global_table.get(n.e.type.name);
    if (!classCalled.methods.containsKey(n.i.s)) {
      n.type = undefined_;
      //classCalled.methods.put(n.s, (MethodType) new UndefinedType());
      System.out.println("Error! Method does not exist in class " + n.e.type.name + ": " + n.i.s +  " (line " + n.line_number + ")");
      error = true;
    } else {
      n.type = classCalled.methods.get(n.i.s);
      if (parameters.size() != classCalled.methods.get(n.i.s).parameterTypes.size()){
        System.out.println("Error! Incorrect number of parameters (line " + n.line_number + ")");
        error = true;
      } else {
        for ( int i = 0; i < parameters.size(); i++ ) {
          // if(!parameters.get(i).equals(classCalled.methods.get(n.i.s).parameterTypes.get(i).name)) {
          //   System.out.println(parameters.get(i));
          //   System.out.println("Error! Incorrect parameter type: " + parameters.get(i)  + " (line " + n.line_number + ")");
          //   error = true;
          // }
          //System.out.println(parameters.get(i) + "/" + classCalled.methods.get(n.i.s).parameterTypes.get(i).name);
          //System.out.println(classCalled.methods.get(n.i.s).parameterTypes.get(i).name);
          //System.out.println(n.el.get(i).type + "/" + global_table.containsKey(classCalled.methods.get(n.i.s).parameterTypes.get(i).name));
          if(!classCalled.methods.get(n.i.s).parameterTypes.get(i).canAssign(n.el.get(i).type)) {
            //System.out.println(parameters.get(i) + "/" + classCalled.methods.get(n.i.s).parameterTypes.get(i).name);
            //System.out.println(global_table.get(classCalled.methods.get(n.i.s).parameterTypes.get(i).name));

            // If we are comparing classes
            if (global_table.containsKey(classCalled.methods.get(n.i.s).parameterTypes.get(i).name) && global_table.containsKey(n.el.get(i).type.name)) {
              ClassType class1 = global_table.get(classCalled.methods.get(n.i.s).parameterTypes.get(i).name);
              ClassType class2 = global_table.get(n.el.get(i).type.name);

              ClassType temp = class2;
              while (temp != null) {
                if (class1.canAssign(temp)) {
                  break;
                } else {
                  temp = global_table.get(temp.parent);
                }
              }

              if (temp == null) {
                //System.out.println(n.el.get(i).type + "/" + classCalled.methods.get(n.i.s).parameterTypes.get(i).name);
                System.out.println("Error! Incorrect parameter type: " + parameters.get(i)  + " (line " + n.line_number + ")");
                error = true;
              }
            } else { //If not a class
              System.out.println("Error! Incorrect parameter type: " + parameters.get(i)  + " (line " + n.line_number + ")");
              error = true;
            }
          }
        }
      }
    }
  }

  public void visit(IntegerLiteral n){
    n.type = int_;
  }

  public void visit(True n){
    n.type = boolean_;
  }

  public void visit(False n){
    n.type = boolean_;
  }

  public void visit(IdentifierExp n){
    TypeNode scope = null;
    if (current_methodType.variables.containsKey(n.s)) {
      scope = current_methodType.variables.get(n.s);
      n.type = current_methodType.variables.get(n.s);
    } else if (current_classType.fields.containsKey(n.s)) {
      scope = current_classType.fields.get(n.s);
      n.type = current_classType.fields.get(n.s);
    } else {
      ClassType temp = current_classType;
      while (temp != null) {
        if (temp.fields.containsKey(n.s)) {
          scope = temp.fields.get(n.s);
          n.type = temp.fields.get(n.s);
          break;
        } else {
          temp = global_table.get(temp.parent);
        }
      }
    }

    if (scope == null) {
      System.out.println("Error! Undeclared identifier: " + n.s + " (line " + n.line_number + ")");
      current_methodType.variables.put(n.s, new UndefinedType());
      n.type = current_methodType.variables.get(n.s);
      error = true;
    }
  }

  public void visit(This n){
    n.type = current_classType;
  }

  public void visit(NewArray n){
    n.e.accept(this);
    // Check that the expression we're trying to use as size is an int
    if(!n.e.type.equals(int_)) {
      System.out.println("Error! Using a non-int for the size of an array: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = int_array_;
  }

  public void visit(NewObject n){
    // Check that the class we're trying to instantiate exists
    if(!global_table.containsKey(n.i.s)) {
      System.out.println("Error! Cannot instantiate a non-existant class: " + n.i.s + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = global_table.get(n.i.s);
  }

  public void visit(Not n){
    n.e.accept(this);
    // Check that the expression we're trying to use ! on is a boolean
    if(!n.e.type.equals(boolean_)) {
      System.out.println("Error! Using ! operator on a non-boolean: " + n.e.type.name + " (line " + n.line_number + ")");
      error = true;
    }
    n.type = boolean_;
  }

  public void visit(Identifier n){}
}