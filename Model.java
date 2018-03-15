import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Class;
import java.lang.reflect.*;

/**
 * Model
 */
public class Model {

    private ArrayList<ArrayList<String>> conditionStack;
    
    public Model() {
        conditionStack = new ArrayList<ArrayList<String>>();
    }

    public Model where(String arg1, String operator, String arg2) {
        ArrayList<String> condition = new ArrayList<String>(Arrays.asList(arg1, operator, arg2));
        conditionStack.add(condition);
        return this;
    }

    public static Model fetch(String className) throws ModelException{
        try {
            Class clazz = Class.forName(className);
            return Model.class.cast(clazz.newInstance());
        }
        catch(Exception e) {
            throw new ModelException("Couldn't create Model");
        }
    }

    public Model all() {
        return this;
    }
    
    public void execute() {
        System.out.println("table = " + this.getTableName());
        this.conditionStack.stream().forEach(System.out::println);
    }

    public String getTableName() {
        return this.getClass().getSimpleName();
    }
}