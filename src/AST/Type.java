package AST;

import AST.Visitor.Visitor;
import java_cup.runtime.ComplexSymbolFactory.Location;

public abstract class Type extends ASTNode {
    public String s = "unknown";
    public Type(Location pos) {
        super(pos);
    }
    public abstract void accept(Visitor v);
}
