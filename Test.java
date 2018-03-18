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
            User user =  new User();
            // user.setAttr("nom", "Kallel");
            // user.setAttr("prenom", "Wassim");
            // user.setAttr("creation_date", new Core.DataTypes.Date());
            // user.save();
            user.setAttr("Id", 8);
            user.setAttr("prenom", "mech Wassim");
            user.setAttr("nom", "Kallel");
            user.setAttr("creation_date", new Core.DataTypes.Date());
            System.out.println(user.getAllAttributes());
            user.update();
            // ResultSet data = (ResultSet)Model.sqlQuery("Select * From personne where prenom = ?" ,
            //                                             Arrays.asList("xxxxx"));
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


