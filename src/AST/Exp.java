package AST;

import ADT.*;
import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Exp extends ASTNode {
  public TypeNode type;
  public Exp(Location pos) {
      super(pos);
  }
  public abstract void accept(Visitor v);
}
