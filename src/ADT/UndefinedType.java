package ADT;

public class UndefinedType extends TypeNode{

  public UndefinedType() {
    super("!UNDEFINED");
  }

  public boolean canAssign(TypeNode other) {
    return true;
  }

  public boolean equals(TypeNode other) {
    return true;
  }
}
