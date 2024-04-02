package org.vexillum;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.List;

public class DatabaseControl {
    static Session databaseSession = null;
    static SessionFactory sessionFactory = null;
    private static void openDBSession()
    {
        System.out.println("Opening");
        sessionFactory = new Configuration().configure().buildSessionFactory();
        databaseSession = sessionFactory.openSession();
        System.out.println("Opened");
    }
    private static void closeDBSession()
    {
        System.out.println("Closing");
        sessionFactory.close();
        databaseSession.close();
        sessionFactory = null;
        databaseSession = null;
        System.out.println("Closed");
    }

    public static List<Operator> getOperators() {
        openDBSession();
        Query query = databaseSession.createQuery("from Operator");
        List<Operator> list = query.list();
        closeDBSession();
        return list;
    }

    public static List<Operator> getSpecificOperator(String nameSearch) {
        openDBSession();
        Query query = databaseSession.createQuery("from Operator where name = '" + nameSearch + "'");
        List<Operator> list = query.list();
        closeDBSession();
        return list;
    }
}
