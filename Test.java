import Core.ORM.*;
import Models.*;
import java.util.ArrayList;
import java.util.*;
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
            
            System.out.println(((User)Model.fetch("User").find(2)).posts());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }       
    }
}


