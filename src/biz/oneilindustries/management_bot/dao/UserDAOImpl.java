package biz.oneilindustries.management_bot.dao;

import biz.oneilindustries.management_bot.hibrenate.HibernateConfig;
import biz.oneilindustries.management_bot.hibrenate.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserDAOImpl implements UserDAO {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    public UserDAOImpl() {
        sessionFactory = HibernateConfig.getSessionFactory();
        session = sessionFactory.openSession();
    }

    private void openSession() {
        if (sessionFactory.isClosed() || !session.isOpen()) {
            sessionFactory = HibernateConfig.getSessionFactory();
            session = sessionFactory.openSession();
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User getUser(String userSteamID) {
        Query query= session.
                createQuery("from User where steamID=:userSteamID");
        query.setParameter("userSteamID", userSteamID);
        User user = (User) query.uniqueResult();
        return user;
    }

    @Override
    public void saveUser(User user) {
        openSession();

        transaction = session.beginTransaction();

        session.saveOrUpdate(user);
        commit();
    }

    @Override
    public void deleteUser(User user) {
        openSession();

        transaction = session.beginTransaction();

        session.delete(user);

        commit();
    }

    private void commit() {
        transaction.commit();
        close();
    }

    public void close() {
        //sessionFactory.close();
        session.close();
    }
}
