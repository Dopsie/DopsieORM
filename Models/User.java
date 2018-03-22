package Models;

import Core.ORM.*;
import Helpers.Exceptions.*;
import Helpers.Exceptions.ModelException;
import java.util.ArrayList;
/**
 * Test
 */
public class User extends Model {

    public int id;

    @Override
    public String getTableName() {
        return "personne";
    }

    @Override
    public String getPrimaryKeyName() {
        return "uid";
    }

    public ArrayList<Post> posts() throws ModelException{
        try {
            return this.hasMany(Post.class);
        } catch (Exception e) {
            throw new ModelException("Could not find relationship");
        }
    }
}