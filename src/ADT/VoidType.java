package ADT;

public class VoidType extends TypeNode{
  public static final VoidType VOID = new VoidType("void");
	private String type;

	private VoidType(String type) {
    super(type);
		this.type = type;
	}


  @Override
  public boolean canAssign(TypeNode other) {
    if(other instanceof UndefinedType){
      return true;
    }
    return this == other;
  }
}
