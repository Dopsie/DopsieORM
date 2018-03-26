package Models;
import java.util.ArrayList;
import Core.ORM.*;

/**
 * Posts
 */
public class Post extends Model {

    public int id;
    public int user_id;

    @Override
    public String getTableName() {
        return "Post";
    }

}