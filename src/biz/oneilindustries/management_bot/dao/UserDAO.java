package biz.oneilindustries.management_bot.dao;

import biz.oneilindustries.management_bot.hibrenate.entity.User;

import java.util.List;

public interface UserDAO {

    List<User> getUsers();
    User getUser(String steamID);
    void saveUser(User user);
    void deleteUser(User user);
    boolean checkIfUUIDExists(String uuid);
    void close();
}
