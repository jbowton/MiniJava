package AST;

import ADT.*;
import java.util.*;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class MethodDecl extends ASTNode {
  public Map<String, TypeNode> variables;
  public Type t;
  public Identifier i;
  public FormalList fl;
  public VarDeclList vl;
  public StatementList sl;
  public Exp e;

  public MethodDecl(Type at, Identifier ai, FormalList afl, VarDeclList avl, 
                    StatementList asl, Exp ae, Location pos) {
    super(pos);
    t=at; i=ai; fl=afl; vl=avl; sl=asl; e=ae;
    variables = new HashMap<String, TypeNode>();
  }
 
  public void accept(Visitor v) {
    v.visit(this);
  }
}
