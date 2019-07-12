package biz.oneilindustries.management_bot.hibrenate.entity;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "steam_id")
    private String steamID;

    @Column(name = "user_added_by")
    private String userAddedBy;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "userID", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    //@JoinColumn(name = "role")
    private List<UserRoles> userRoles;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usernames_id")
    private UserNames userNames;

    public User() {
    }

    public User(String steamID, String userAddedBy, String status, UserNames userNames) {
        this.steamID = steamID;
        this.userAddedBy = userAddedBy;
        this.status = status;
        this.userNames = userNames;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSteamID() {
        return steamID;
    }

    public void setSteamID(String steamID) {
        this.steamID = steamID;
    }

    public String getUserAddedBy() {
        return userAddedBy;
    }

    public void setUserAddedBy(String userAddedBy) {
        this.userAddedBy = userAddedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserNames getUserNames() {
        return userNames;
    }

    public void setUserNames(UserNames userNames) {
        this.userNames = userNames;
    }

    public List<UserRoles> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoles> userRoles) {
        this.userRoles = userRoles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", steamID='" + steamID + '\'' +
                ", userAddedBy='" + userAddedBy + '\'' +
                ", status='" + status + '\'' +
                ", userRoles=" + userRoles +
                ", userNames=" + userNames +
                '}';
    }

    public void addUserRole(UserRoles userRoles) {
        if (this.userRoles == null) {
            this.userRoles = new ArrayList<>();
        }
        userRoles.setUserID(this);
        this.userRoles.add(userRoles);
    }
}
