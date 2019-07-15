package biz.oneilindustries.management_bot.hibrenate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "role")
public class UserRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userID;

    @Column(name = "role_name")
    private String roleName;

    public UserRoles(String roleName) {
        this.roleName = roleName;
    }

    public UserRoles() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUserID() {
        return userID;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "UserRoles{" +
                "id=" + id +
                ", userID=" + userID +
                ", roleName='" + roleName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoles userRoles = (UserRoles) o;
        return getRoleName().equals(userRoles.getRoleName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoleName());
    }
}
