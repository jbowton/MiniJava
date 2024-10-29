package ADT;
import java.util.*;

import java_cup.reduce_action;

public class ClassType extends TypeNode{
  public String parent;
  public Map<String, TypeNode> fields; // type info for fields
  public Map<String, MethodType> methods; // type info for methods
  public ClassType(String name) { //need to add parent name?
    super(name);
    // this.parent_name = parent_name;
    this.fields = new HashMap<String, TypeNode>();
    this.methods = new HashMap<String, MethodType>();
  }

  public ClassType(String name, String parent) { //need to add parent name?
    super(name);
    this.parent = parent;
    this.fields = new HashMap<String, TypeNode>();
    this.methods = new HashMap<String, MethodType>();
  }

  public String toString(){
    if(parent != null) {
      return name + " extends " + parent;
    }
    return name;
  }

  @Override
	public boolean canAssign(TypeNode other) {
    if(other instanceof UndefinedType){
      return true;
    }
		// must be a ClassType
		if (!(other instanceof ClassType)) {
      return false;
    }
		ClassType o = (ClassType) other;
		// If same type of class
		if (name.equals(o.name)) {
			return true;
    }



    // if (!this.fields.equals(o.fields) || !this.methods.equals(o.methods)) {
    //   System.out.println("2");
    //   return false;
    // }

    return false;
	}


}
