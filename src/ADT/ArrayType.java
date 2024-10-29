package ADT;

class ArrayType extends TypeNode {
  private int nDims;
  TypeNode elementType;

  public ArrayType(int nDims, TypeNode elementType) {
    super("Nothing");
    this.nDims = nDims;
    this.elementType = elementType;
  }

  // Just to keep the field private
  public int getDims() {
    return nDims;
  }

  @Override
	public boolean canAssign(TypeNode other) {
    if(other instanceof UndefinedType){
      return true;
    }
		if (!(other instanceof ArrayType))
			return false;
		ArrayType o = (ArrayType) other;
    // If dimensions arent the same
		if (nDims != o.getDims())
			return false;
    // Checks return types are the same
		return elementType.canAssign(o.elementType);
	}
}