package biz.oneilindustries.management_bot.hibrenate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {

    private static Configuration configuration;

    static {
        configuration = new Configuration().configure();
        configuration.addAnnotatedClass(biz.oneilindustries.management_bot.hibrenate.entity.User.class);
        configuration.addAnnotatedClass(biz.oneilindustries.management_bot.hibrenate.entity.UserRoles.class);
        configuration.addAnnotatedClass(biz.oneilindustries.management_bot.hibrenate.entity.UserNames.class);
    }

    public static SessionFactory getSessionFactory() {
        return configuration.buildSessionFactory();
    }

    private HibernateConfig() {
    }
}
