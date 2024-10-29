package AST;

import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public class BooleanType extends Type {
  public BooleanType(Location pos) {
    super(pos);
    s = "boolean";
  }
  public void accept(Visitor v) {
    v.visit(this);
  }
}
