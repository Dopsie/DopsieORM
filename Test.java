import Core.ORM.*;
import Models.*;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.util.*;
import Core.DataTypes.*;

/**
 * Test
 */
public class Test {

    public static void main(String[] args) {
        try {
            // Model.fetch(User.class).all().where("hey", "hi", "arg2").where("arg1", "operator", "arg2").execute();
            // User user = new User();
            // user.setAttr("nom", "Kallel");
            // user.setAttr("prenom", "Wassim");
            // user.save();
            // // user.setAttr("Id", 8);
            // user.setAttr("prenom", "mech Wassim");
            // user.save();
            // user.setAttr("nom", "Kallel");
            // user.setAttr("creation_date", new Core.DataTypes.Date());
            // System.out.println(user.getAllAttributes());
            // user.update();
            // ArrayList<User> data = Model.sqlQuery("Select * From personne where prenom = ?" ,
            //                                             Arrays.asList("wassim"), User.class);
            // for (User tmp : data) {
            //     System.out.println(tmp.getAttr("creation_date"));
            // }

            //ArrayList<User> data = Model.fetch(User.class).all().where("prenom", "=", "Wassim").execute();
             
            // for (User user : data) {
            //     System.out.println(user);
            // }
            User u = Model.find(User.class, 9) ;
            System.out.println(u.testManyToMany());
            // System.out.println(data);
            // while (data.next()) {
            //     System.out.println(data.getString(2));
            // }
            // System.out.println(Model.sqlUpdate("insert into personne (nom,prenom) values(?,?)", Arrays.asList("BenFoulen", "Foulen")));
            // System.out.println(Model.find(User.class, 2).posts());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
