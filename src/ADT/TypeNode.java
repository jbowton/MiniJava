package ADT;

abstract public class TypeNode {
  public String name;
  public TypeNode(String name) {
    this.name = name;
  }

  public String toString() {
    return name;
  }

  // Should maybe implement the auxillary typechecking methods here?
  // Maybe add sameType?, toString?
  // public boolean canAssign(TypeNode other) {
  //   System.out.println("here");
  //   return other.name.equals(this.name) || (other instanceof UndefinedType);
  // }
  public abstract boolean canAssign(TypeNode other);

  public boolean equals(TypeNode other) {
    // Returns true when the type names are equal or the other type is Undefined
    return other.name.equals(this.name) || (other instanceof UndefinedType);
  }
}
