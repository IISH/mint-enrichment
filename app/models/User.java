package models;

import org.apache.commons.codec.digest.DigestUtils;
import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity(name = "users")
@Table(name = "users")
public class User extends Model {
    @Id
    @Column(name = "users_id")
    private Integer id;

    @Column(name = "login")
    private String login;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "md5_password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    public static Model.Finder<Integer, User> find = new Model.Finder<>(User.class);

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Find a user by its login.
     *
     * @param login The login name.
     * @return The user, if found.
     */
    public static User findByLogin(String login) {
        return find.where().eq("login", login).findUnique();
    }

    /**
     * Authenticate a User, from a login and password.
     *
     * @param login    The login.
     * @param password The password.
     * @return User if authenticated, null otherwise.
     */
    public static User authenticate(String login, String password) {
        User user = findByLogin(login);
        if (user != null) {
            String md5Password = DigestUtils.md5Hex(login + password);
            if (user.password.equalsIgnoreCase(md5Password)) {
                return user;
            }
        }
        return null;
    }
}
