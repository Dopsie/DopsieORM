import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Class;
import java.lang.reflect.*;

/**
 * Model
 */
public class Model {

    private ArrayList<ArrayList<String>> conditionStack;
    protected String tableName = "";

    public Model() {
        conditionStack = new ArrayList<ArrayList<String>>();
    }

    public Model where(String arg1, String operator, String arg2) {
        ArrayList<String> condition = new ArrayList<String>(Arrays.asList(arg1, operator, arg2));
        conditionStack.add(condition);
        return this;
    }

    public static Model fetch(String className) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Class clazz = null;
        try {
            clazz = Class.forName(className);
        }
        catch(Exception e) {
            System.out.println(" Error Creating Model ");
        }
        return Model.class.cast(clazz.newInstance());
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

class CurrentClassGetter extends SecurityManager {
    public String getClassName() {
        return getClassContext()[1].getName();
    }
}