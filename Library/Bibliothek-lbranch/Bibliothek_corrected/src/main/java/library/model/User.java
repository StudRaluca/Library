package library.model;
import javax.persistence.*;

//@Entity
//@Inheritance(strategy = InheritanceType.JOINED)
@MappedSuperclass
public abstract class User extends Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int userId;
    public String username;
    public String password;

    public User(String firstName, String lastName, String password, String username) {
        super(firstName, lastName);
        this.username = username;
        this.password = password;
    }

    public User() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}