package examples;
import java.util.ArrayList;
import dopsie.core.*;

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