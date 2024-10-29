package ADT;

public class BaseType extends TypeNode{
  public static final BaseType INTEGER = new BaseType("int");
	// represents the 'boolean' type
	public static final BaseType BOOLEAN = new BaseType("boolean");
	// string representation of the type
	private String type;

  public int offset;

  // We can check if two types are equal by checking that the strings are equal
	public BaseType(String name) {
		super(name);
	}

  // Should maybe implement the auxillary typechecking methods here?
  //public boolean sameType(BaseType other) {
  //  return this.name.equals(other.name);
  //}
  public String toString() {
    return "variable->" + name;
  }

  public boolean canAssign(TypeNode other) {
    return other.name.equals(this.name) || (other instanceof UndefinedType);
  }
}
