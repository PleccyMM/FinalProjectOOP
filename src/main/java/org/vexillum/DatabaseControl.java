package org.vexillum;

import org.hibernate.*;
import org.hibernate.query.*;
import org.hibernate.cfg.Configuration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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

    public static List<Design> getAllDesigns() {
        openDBSession();
        var query = databaseSession.createQuery("from Design order by name asc");
        List<Design> list = query.list();
        closeDBSession();
        return list;
    }

    public static List<Design> searchDesigns(String nameSearch) {
        openDBSession();
        var query = databaseSession.createQuery("from Design where LOWER(name) like '%" + nameSearch.toLowerCase() + "%' order by name asc");
        List<Design> list = query.list();
        closeDBSession();
        return list;
    }

    public static String getIsoName(String isoID) {
        openDBSession();
        var query = databaseSession.createQuery("select name from Design where isoID = '" + isoID + "'");
        List<String> list = query.list();
        closeDBSession();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    public static Flag createFlag(String isoID, FLAG_SIZE size) {
        openDBSession();
        System.out.println("Making Flag");

        var query = databaseSession.createQuery("from Flag where isoID = '" + isoID + "'");
        List<Flag> list = query.list();
        closeDBSession();

        if (list.size() == 0) return null;

        Flag f = list.get(0);
        f.setSizeID(FLAG_SIZE.getSizeId(size));
        return (Flag) setStockData(f);
    }

    public static Cushion createCushion(String isoID, CUSHION_SIZE size) {
        openDBSession();
        System.out.println("Making Cushion");

        var query = databaseSession.createQuery("from Cushion where isoID = '" + isoID + "'");
        List<Cushion> list = query.list();
        closeDBSession();

        if (list.size() == 0) return null;

        Cushion c = list.get(0);
        c.setSizeID(CUSHION_SIZE.getSizeId(size));
        return (Cushion) setStockData(c);
    }

    public static StockItem setStockData(StockItem i) {
        try {
            openDBSession();
            System.out.println("Getting stock data");
            var query = databaseSession.createNativeQuery("select amount from stock_amount where stockid = (:stockid) and sizeid = (:sizeid)")
                    .setParameter("stockid", i.getStockID())
                    .setParameter("sizeid", i.getSizeID());
            List<Integer> list = query.list();
            i.setTotalAmount(list.get(0));

            query = databaseSession.createNativeQuery("select restock from stock_amount where stockid = (:stockid) and sizeid = (:sizeid)")
                    .setParameter("stockid", i.getStockID())
                    .setParameter("sizeid", i.getSizeID());
            list = query.list();
            i.setRestock(list.get(0));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            closeDBSession();
        }
        return i;
    }

    public static String getRegionName(int regionID) {
        openDBSession();
        var query = databaseSession.createNativeQuery("select name from regions where regionid = '" + regionID + "'");
        List<String> list = query.list();
        closeDBSession();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    public static String getTypeName(int typeID) {
        openDBSession();
        var query = databaseSession.createNativeQuery("select name from designtypes where typeid = '" + typeID + "'");
        List<String> list = query.list();
        closeDBSession();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    //SQL used for database setup:


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

            for (int i = 0; i < keys.length (); i++) {
                String iso = keys.getString(i);
                String name = variableList.getString(iso);
                databaseSession.save(new Design(iso, name));
            }
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
        finally {
            closeDBSession();
        }
    }

    public static void AddTags () {
        openDBSession();
        databaseSession.beginTransaction();
        try {
            String loc = new String("src/main/java/org/Assets/flagsTags.json");
            File file = new File(loc);
            String contents = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonFile = new JSONObject(contents);
            JSONObject variableList = jsonFile.getJSONObject("Tags");
            JSONArray keys = variableList.names ();

            int updatedRows = 0;
            for (int i = 0; i < keys.length (); i++) {
                String iso = keys.getString(i);
                JSONArray tags = variableList.getJSONArray(iso);

                String query = "update Design set region = " + tags.get(0) + ", type = " + tags.get(1) + " where isoID = '" + iso + "'";
                databaseSession.createQuery(query).executeUpdate();
                updatedRows ++;
            }
            databaseSession.getTransaction().commit();
            System.out.println("Updated " + updatedRows);
        }
        catch (Exception e) {
            System.out.println("FAILED");
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
        finally {
            closeDBSession();
        }
    }

    public static void AddFlags() {
        openDBSession();
        databaseSession.beginTransaction();
        try {
            String loc = new String("src/main/java/org/Assets/flags.json");
            File file = new File(loc);
            String contents = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonFile = new JSONObject(contents);
            JSONObject variableList = jsonFile.getJSONObject("Designs");
            JSONArray keys = variableList.names ();

            for (int i = 0; i < keys.length (); i++) {
                String iso = keys.getString(i);
                databaseSession.save(new Flag(i, iso, i));
            }
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            System.out.println("FAILED TO ADD FLAGS");
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
        finally {
            closeDBSession();
        }
    }

    public static void AddCushions() {
        openDBSession();
        databaseSession.beginTransaction();
        try {
            String loc = new String("src/main/java/org/Assets/flags.json");
            File file = new File(loc);
            String contents = new String(Files.readAllBytes(Paths.get(file.toURI())));
            JSONObject jsonFile = new JSONObject(contents);
            JSONObject variableList = jsonFile.getJSONObject("Designs");
            JSONArray keys = variableList.names ();

            for (int i = 0; i < keys.length (); i++) {
                String iso = keys.getString(i);
                databaseSession.save(new Cushion(i, iso, i + keys.length()));
            }
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            System.out.println("FAILED TO ADD CUSHIONS");
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
        finally {
            closeDBSession();
        }
    }

    public static void AddStockItem() {
        openDBSession();
        try {
            databaseSession.beginTransaction();

            var query = databaseSession.createQuery("from Design");
            List<Design> list = query.list();

            for (int i = 0; i < list.size() * 2; i++) {
                databaseSession.createNativeQuery("insert into stockmanager (stockid) values (:stockid)" )
                        .setParameter("stockid", i)
                        .executeUpdate();
            }

            databaseSession.getTransaction().commit();
        } catch (Exception e) {
            databaseSession.getTransaction().rollback();
            System.out.println("FAILED TO ADD STOCKID");
            e.printStackTrace();
        }
        finally {
            closeDBSession();
        }
    }

    public static void SetAmounts() {
        openDBSession();
        try {
            databaseSession.beginTransaction();

            var query = databaseSession.createNativeQuery("select * from stockmanager");
            List<Design> list = query.list();

            Random rnd = new Random();

            for (int i = 0; i < list.size(); i++) {
                int runAmount = i <= list.size() / 2 - 1 ? 5 : 9;
                int start = runAmount == 5 ? 0 : 5;

                System.out.println(i);
                for (int j = start; j < runAmount; j++) {
                    int amount = rnd.nextInt(1, 20);
                    int restock = rnd.nextInt(3, 10);

                    databaseSession.createNativeQuery("insert into stock_amount (stockid, sizeid, amount, restock) values (:stockid, :sizeid, :amount, :restock)")
                            .setParameter("stockid", i)
                            .setParameter("sizeid", j)
                            .setParameter("amount", amount)
                            .setParameter("restock", restock)
                            .executeUpdate();
                }
            }
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            databaseSession.getTransaction().rollback();
            System.out.println("FAILED TO ADD STOCKID");
            e.printStackTrace();
        }
        finally {
            closeDBSession();
        }
    }

    public static void TestSQL() {
        openDBSession();
        try {
            String sql = "SELECT * FROM sizes";
            NativeQuery<Object[]> query = databaseSession.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                int id = (int) row[0];
                String name = (String) row[1];

                System.out.println("Size: ID=" + id + ", Name=" + name);
            }
        }
        catch (Exception e) {
            System.out.println("FAILED TO TEST SQL");
            e.printStackTrace();
        }
        finally {
            closeDBSession();
        }
    }
}
