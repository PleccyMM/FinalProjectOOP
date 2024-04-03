package org.vexillum;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

public class DatabaseControl {
    static Session databaseSession = null;
    static SessionFactory sessionFactory = null;
    public static void openDBSession()
    {
        System.out.println("Opening");
        sessionFactory = new Configuration().configure().buildSessionFactory();
        databaseSession = sessionFactory.openSession();
        System.out.println("Opened");
    }
    public static void closeDBSession()
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
        var query = databaseSession.createQuery("from Operator");
        List<Operator> list = query.list();
        closeDBSession();
        return list;
    }

    public static List<Operator> getSpecificOperator(String nameSearch) {
        openDBSession();
        var query = databaseSession.createQuery("from Operator where name = '" + nameSearch + "'");
        List<Operator> list = query.list();
        closeDBSession();
        return list;
    }
    public static void AddDesigns () {
        openDBSession();
        databaseSession.beginTransaction();
        try {
            String loc = new String("src/main/java/org/Assets/flags.json");
            File file = new File(loc);
            String contents = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonFile = new JSONObject(contents);
            JSONObject variableList = jsonFile.getJSONObject("Designs");
            JSONArray keys = variableList.names ();

            for (int i = 0; i < keys.length (); ++i) {
                String iso = keys.getString(i);
                String name = variableList.getString(iso);
                databaseSession.save(new Design(iso, name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        databaseSession.getTransaction().commit();
        closeDBSession();
/*
        databaseSession.beginTransaction();
        databaseSession.save(d);

        */
    }
}
