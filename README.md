#DopsieORM
Lightweight ORM for Java / SQL developers.

##Usage

First, you should define SQL credentials found in the file: 

```
cp Config/Defines.java.sample Config/Defines.java
```

### Creating a Model
Create a Model inside `Models`by extending the Model class from Models package:

``` 
package Models;

import Core.ORM.*;

/**
* User Model
*/
public class User extends Model {

}
```

### Retrieving Data
After you will be able to retrieve data as Model from the database:

To retrieve a User by its id :

```
User user = Model.find(User.class, 1);
```

To retrieve all users:

```
ArrayList<User> allUsersList = Model.fetch(User.class).all().execute();
```

You are able to apply filters on the data using the `where` method:


```
ArrayList<User> allUsersList = Model.fetch(User.class)
				  .all()
				  .where("prenom", "=", "john")
				  .execute();
```

You are also able to order the data using the `orderBy` method:

```
ArrayList<User> allUsersList = Model.fetch(User.class)
				  .all()
				  .where("prenom", "=", "john")
				  .orderBy("prenom", "DESC")
				  .execute();
```

You can get an attribute using `getAttr` method:

```
String prenom = user.getAttr("prenom");
```

###Updating Data
You can set an attribute in an existing Model object using:

```
user.setAttr("prenom", "john");
```

After creating new model object or updating a retrieved object you can push updates to the database using `save`:

```
user.save();
```


###Defining Relationships


#### hasMany

```
public class User extends Model {
    public ArrayList<Post> posts() throws ModelException{
        return this.hasMany(Post.class);
    }
}
```

##Go Further
###Custom table name

```
public class User extends Model {
    @Override
    public String getTableName() {
        return "person";
    }
}

```
###Custom Primary Key column name

```
public class User extends Model {
    @Override
    public String getPrimaryKeyName() {
        return "user_id";
    }

```


###Executing proper SQL
####Queries
You are able to execute SQL queries by calling `sqlQuery` and it will return a `ResultSet`:

```
Model.sqlQuery("SELECT * FROM users");
```

Or make it into a collection of Model objects by:

```
Model.sqlQuery("SELECT * FROM users", User.class);
```
####Updates
For Updates you should provide the SQL statment along to arguments:

```
ArrayList args = new ArrayList(Arrays.asList("Mike", 1));
sqlUpdate("UPDATE users SET prenom = ? WHERE id = ?", args)
```














