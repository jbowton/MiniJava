package ADT;
import java.util.*;

public class MethodType extends TypeNode {
  public List<TypeNode> parameterTypes;
  public Map<String, TypeNode> variables;

  public MethodType(String name) {// , TypeNode type, List<TypeNode> parameterTypes) {
    super(name);
    this.variables = new HashMap<String, TypeNode>();
    this.parameterTypes = new ArrayList<TypeNode>();
    // this.resultType = type;
    // this.parameterTypes = parameterTypes;
  }

  public void addParameter(TypeNode type) {
    parameterTypes.add(type);
  }

  public String toString() {
    String result = "method->" + name;
    if(parameterTypes.size() > 0){
      result = result + " parameters->";
    }
    for(int i = 0; i < parameterTypes.size(); i++){
      result = result + parameterTypes.get(i);
    }

    return result;
  }

  // Checks to see if other can be asigned to this.
  // Returns true if other is assignable to this, the types of parameters are the same,
  //        the return type of other is assignable to return type of this.
  @Override
  public boolean canAssign(TypeNode other) {
    if(other instanceof UndefinedType){
      return true;
    }
    //If not same type
    if (!(other instanceof MethodType)) {
			return false;
    }
		MethodType o = (MethodType) other;
    // If parameters have different sizes
		if (parameterTypes.size() != o.parameterTypes.size()){
			return false;
    }
    //If parameter types dont match up
		for (int i = 0; i < parameterTypes.size(); i++) {
			if (!parameterTypes.get(i).canAssign(o.parameterTypes.get(i)))
				return false;
		}
		return true;
  }


}