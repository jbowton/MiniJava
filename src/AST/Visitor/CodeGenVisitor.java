package AST.Visitor;
import AST.*;
import ADT.*;
import java.util.*;

public class CodeGenVisitor implements Visitor {

  String[] arg_regs = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};

  Map<String, ClassType> global_table;

  ClassType current_classType;
  MethodType current_methodType;

  int global_id = 0;

  public CodeGenVisitor (Map<String, ClassType> global_table){
    this.global_table = global_table;
  }

  // Adds 4 spaces before the argument string
  // Purpose is to cut down the amount of typing required
  private void gen(String s){
    System.out.println("    " + s);
  }

  // Generate a new number that nobody else is using
  private int new_id(){
    global_id++;
    return global_id;
  }

  public void visit(Display n){}

  public void visit(Program n){
    gen(".text");
    gen(".globl  asm_main");

    n.m.accept(this);
    for ( int i = 0; i < n.cl.size(); i++ ) {
        n.cl.get(i).accept(this);
    }

    System.out.println();
  }

  public void visit(MainClass n){
    current_classType = global_table.get(n.i1.s);
    // Prologue
    System.out.println("asm_main:");
    gen("pushq   %rbp");
    gen("movq    %rsp,%rbp");
    // Should also subtract to make room for parameters
    n.i1.accept(this);
    n.i2.accept(this);
    n.s.accept(this);
    // Epilogue
    gen("movq    %rbp,%rsp");
    gen("popq    %rbp");
    gen("ret");
  }

  public void visit(ClassDeclSimple n){
    current_classType = global_table.get(n.i.s);
    n.i.accept(this);
    for ( int i = 0; i < n.vl.size(); i++ ) {
        n.vl.get(i).accept(this);
    }
    for ( int i = 0; i < n.ml.size(); i++ ) {
      // We do the method name here so that we know the class involved
      System.out.println(n.i + "$" + n.ml.get(i).i + ":");
      n.ml.get(i).accept(this);
    }
  }

  public void visit(ClassDeclExtends n){}
  public void visit(VarDecl n){}

  public void visit(MethodDecl n){
    current_methodType = current_classType.methods.get(n.i.s);
    // Prologue
    // We do the method name during the class visit
    gen("pushq   %rbp    # Prologue");
    gen("movq    %rsp,%rbp    # Prologue");

    gen("subq    $8,%rsp    # Make space for parameters");
    gen("movq    " + arg_regs[0] + ",-8(%rbp)    # Copy 'this' reference to memory");
    // Parameter declarations
    for ( int i = 0; i < n.fl.size(); i++ ) {
      Formal curr_formal = n.fl.get(i);
      gen("subq    $8,%rsp    # Make space for parameters");
      TypeNode curr_type = current_methodType.variables.get(curr_formal.i.s);
      gen("movq    " + arg_regs[i+1] + "," + ((BaseType)curr_type).offset + "(%rbp)    # Copy parameters to memory");
      curr_formal.accept(this);
    }
    // Variable declarations
    for ( int i = 0; i < n.vl.size(); i++ ) {
      gen("subq    $8,%rsp    # Make space for local variables");
      n.vl.get(i).accept(this);
    }
    for ( int i = 0; i < n.sl.size(); i++ ) {
      n.sl.get(i).accept(this);
    }
    n.e.accept(this);
    
    // Epilogue
    gen("movq    %rbp,%rsp    # Epilogue");
    gen("popq    %rbp    # Epilogue");
    gen("ret");
  }

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
    int id = new_id();
    n.e.accept(this);
    gen("cmpq    $0,%rax    # See if the 'if' condition is false");
    gen("je    else" + id + "    # Jump to the else branch if the condition is false");
    System.out.println("if" + id + ":");
    n.s1.accept(this);
    gen("jmp    end" + id);
    System.out.println("else" + id + ":");
    n.s2.accept(this);
    System.out.println("end" + id + ":");
  }

  public void visit(While n){
    int id = new_id();
    gen("jmp    test" + id);
    System.out.println("body" + id + ":");
    n.s.accept(this);
    System.out.println("test" + id + ":");
    n.e.accept(this);
    gen("cmpq    $1,%rax    # See if the 'while' condition is true");
    gen("je    body" + id);
  }

  public void visit(Print n){
    n.e.accept(this);
    gen("movq    %rax,%rdi");
    gen("call    put    # Call the Print method");
  }

  public void visit(Assign n){
    n.e.accept(this);
    TypeNode curr_type = current_methodType.variables.get(n.i.s);
    if(curr_type == null){ // It must be a field, not a local variable
      curr_type = current_classType.fields.get(n.i.s);
      gen("movq    %rax," + ((BaseType)curr_type).offset + "(%rdi)    # Assign");
    } else {
      gen("movq    %rax," + ((BaseType)curr_type).offset + "(%rbp)    # Assign");
    }
  }

  public void visit(ArrayAssign n){
    TypeNode curr_type = current_methodType.variables.get(n.i.s);
    if(curr_type == null){ // It must be a field, not a local variable
      curr_type = current_classType.fields.get(n.i.s);
      gen("pushq    " + ((BaseType)curr_type).offset + "(%rdi)"); // Push the pointer to the array
    } else {
      gen("pushq    " + ((BaseType)curr_type).offset + "(%rbp)");
    }

    n.e1.accept(this);
    gen("pushq    %rax    # Save the index");
    n.e2.accept(this);
    gen("popq    %rbx"); // %rbx now represents the index
    gen("addq    $1,%rbx"); // Account for the length stored at the beginning
    gen("imulq    $8,%rbx"); // Multiply by 8 because of the size of the elements
    gen("popq    %rcx"); // %rcx now represents the pointer to the array
    gen("movq    (%rcx),%rcx");
    gen("movq    %rax,(%rbx,%rcx)");
  }

  public void visit(And n){
    int id = new_id();
    n.e1.accept(this);
    gen("cmpq    $0,%rax");
    gen("je    false" + id);
    n.e2.accept(this);
    gen("cmpq    $0,%rax");
    gen("je    false" + id);
    System.out.println("true" + id + ":");
    gen("movq    $1,%rax");
    gen("jmp    end" + id);
    System.out.println("false" + id + ":");
    gen("movq    $0,%rax");
    System.out.println("end" + id + ":");
  }

  public void visit(LessThan n){
    int id = new_id();
    n.e1.accept(this);
    gen("pushq    %rax    # Save the first operand");
    n.e2.accept(this);
    gen("popq    %rbx");
    gen("cmpq    %rax,%rbx");
    gen("jl    true" + id);
    System.out.println("false" + id + ":");
    gen("movq    $0,%rax");
    gen("jmp    end" + id);
    System.out.println("true" + id + ":");
    gen("movq    $1,%rax");
    System.out.println("end" + id + ":");
  }

  public void visit(Plus n){
    n.e1.accept(this);
    gen("pushq    %rax    # Save the first operand");
    n.e2.accept(this);
    gen("popq    %rbx");
    gen("addq    %rbx,%rax");
  }

  public void visit(Minus n){
    n.e1.accept(this);
    gen("pushq    %rax    # Save the first operand");
    n.e2.accept(this);
    gen("popq    %rbx");
    gen("subq    %rax,%rbx");
    gen("movq    %rbx,%rax");
  }

  public void visit(Times n){
    n.e1.accept(this);
    gen("pushq    %rax    # Save the first operand");
    n.e2.accept(this);
    gen("popq    %rbx");
    gen("imulq    %rbx,%rax");
  }

  public void visit(ArrayLookup n){
    n.e1.accept(this);
    gen("pushq    %rax    # Save the array reference");
    n.e2.accept(this);
    // %rax now represents the index
    gen("addq    $1,%rax"); // Account for the length stored at the beginning
    gen("imulq    $8,%rax"); // Multiply by 8 because of the size of the elements
    gen("popq    %rbx");
    gen("movq    (%rax,%rbx),%rax");
  }

  public void visit(ArrayLength n){
    n.e.accept(this);
    gen("movq    (%rax),%rax");
  }

  public void visit(Call n){
    // Visit object and leave reference to it in %rax
    n.e.accept(this);
    gen("movq    %rax,%rdi");
    
    for ( int i = 0; i < n.el.size(); i++ ) {
      n.el.get(i).accept(this);
      // Put each one in the next argument register
      gen("movq    %rax," + arg_regs[i+1]);
    }
    gen("call    " + n.e.type.name + "$" + n.i); // Might have to change this?
  }

  public void visit(IntegerLiteral n){
    gen("movq    $" + n.i + ",%rax");
  }

  public void visit(True n){
    gen("movq    $1,%rax    # Set to true");
  }

  public void visit(False n){
    gen("movq    $0,%rax    # Set to false");
  }

  public void visit(IdentifierExp n){
    TypeNode curr_type = current_methodType.variables.get(n.s);
    if(curr_type == null){ // It must be a field, not a local variable
      curr_type = current_classType.fields.get(n.s);
      gen("movq    " + ((BaseType)curr_type).offset + "(%rdi),%rax");
    } else {
      gen("movq    " + ((BaseType)curr_type).offset + "(%rbp),%rax    # Move identifier to %rax");
    }
  }

  public void visit(This n){
    gen("movq    -8(%rbp),%rax    # this");
  }

  public void visit(NewArray n){
    // Should store the elements of the array and an int for the length
    n.e.accept(this);
    gen("movq    %rax,%rdi"); // Space for the elements
    gen("pushq    %rax");
    gen("imulq    $8,%rdi");
    gen("addq    $8,%rdi"); // Space for the length
    gen("call    mjcalloc");
    // Pointer to this space should now be in %rax
    gen("popq    %rbx");
    gen("movq    %rbx,(%rax)");
  }

  public void visit(NewObject n){
    // Create a new object
    // Use malloc to create space for the object
    // Object consists of fields and pointer to the vtable
    gen("movq    $8,%rdi"); // Allocate 8 bytes of space, enough for a pointer
    gen("addq    $" + (8 * global_table.get(n.i.s).fields.size()) + ",%rdi"); // Also allocate 8 bytes of space for every field
    gen("call    mjcalloc");
    // Pointer to this space should now be in %rax
  }

  public void visit(Not n){
    int id = new_id();
    n.e.accept(this);
    gen("cmpq    $0,%rax");
    gen("je    true" + id);
    System.out.println("false" + id + ":");
    gen("movq    $0,%rax");
    gen("jmp    end" + id);
    System.out.println("true" + id + ":");
    gen("movq    $1,%rax");
    System.out.println("end" + id + ":");
  }

  public void visit(Identifier n){}

}
