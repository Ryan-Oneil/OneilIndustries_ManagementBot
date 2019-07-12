package biz.oneilindustries.management_bot.hibrenate.entity;

import javax.persistence.*;

@Entity
@Table(name = "usernames")
public class UserNames {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "discord_name")
    private String discordName;

    @Column(name = "teamspeak_name")
    private String teamspeakName;

    public UserNames(String discordName, String teamspeakName) {
        this.discordName = discordName;
        this.teamspeakName = teamspeakName;
    }

    public UserNames() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(String discordName) {
        this.discordName = discordName;
    }

    public String getTeamspeakName() {
        return teamspeakName;
    }

    public void setTeamspeakName(String teamspeakName) {
        this.teamspeakName = teamspeakName;
    }

    @Override
    public String toString() {
        return "UserNames{" +
                "id=" + id +
                ", discordName='" + discordName + '\'' +
                ", teamspeakName='" + teamspeakName + '\'' +
                '}';
    }
}
