import Core.ORM.*;
import Models.*;
/**
 * Test
 */
public class Test {

    public static void main(String[] args) {
        try {
            Model.fetch("User").all().where("hey", "hi", "arg2").where("arg1", "operator", "arg2").execute();
            User user =  new User();
            user.getAttributes();
            Model.sql("Select * From personne");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }       
    }
}


