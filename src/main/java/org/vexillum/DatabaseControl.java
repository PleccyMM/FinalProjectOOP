package org.vexillum;

import org.hibernate.*;
import org.hibernate.query.*;
import org.hibernate.cfg.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.json.*;

public class DatabaseControl {
    private Session databaseSession = null;
    private SessionFactory sessionFactory;

    public DatabaseControl() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void openDBSession() {
        if (databaseSession == null || !databaseSession.isOpen()) {
            System.out.println("Opening");
            databaseSession = sessionFactory.openSession();
            System.out.println("Opened");
        }
    }

    public boolean isOpen() {
        return databaseSession.isOpen();
    }

    public void closeDBSession() {
        if (databaseSession != null && databaseSession.isOpen()) {
            System.out.println("Closing session");
            databaseSession.close();
            databaseSession = null;
            System.out.println("Closed session");
        }
    }

    public void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            System.out.println("Closing session factory");
            sessionFactory.close();
            sessionFactory = null;
            System.out.println("Closed session factory");
        }
    }

    public List<Operator> getSpecificOperator(String nameSearch) {
        var query = databaseSession.createQuery("from Operator where name = '" + nameSearch + "'");
        List<Operator> list = query.list();
        return list;
    }
    public List<Operator> getOperatorsByID(Integer[] ids) {
        var query = databaseSession.createQuery("from Operator where id in (:ids) order by id asc")
                .setParameter("ids", Arrays.asList(ids));
        List<Operator> list = query.list();
        return list;
    }

    public void acceptOperator(int id) {
        Transaction transaction = null;
        try {
            transaction = databaseSession.beginTransaction();
            var q1 = databaseSession.createNativeQuery("delete from operator_approvals where operatorid = (:id)")
                    .setParameter("id", id);
            var q2 = databaseSession.createNativeQuery("update operators set approved = 1 where operatorid = (:id)")
                    .setParameter("id", id);
            q1.executeUpdate();
            q2.executeUpdate();
            transaction.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
    }

    public void denyOperator(int id) {
        Transaction transaction = null;
        try {
            transaction = databaseSession.beginTransaction();
            var q1 = databaseSession.createNativeQuery("delete from operator_approvals where operatorid = (:id)")
                    .setParameter("id", id);
            var q2 = databaseSession.createNativeQuery("delete from operators where operatorid = (:id)")
                    .setParameter("id", id);
            q1.executeUpdate();
            q2.executeUpdate();
            transaction.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        }
    }

    public List<Integer> getExistentIDs() {
        var query = databaseSession.createQuery("select operatorID from Operator");
        List<Integer> list = query.list();
        return list;
    }

    public void addRequest(int id, String name, String password, Date applicationTime) {
        databaseSession.beginTransaction();
        databaseSession.createNativeQuery("insert into operators values ((:id), (:name), (:password), 0, 0)")
                .setParameter("id", id)
                .setParameter("name", name)
                .setParameter("password", password)
                .executeUpdate();
        databaseSession.createNativeQuery("insert into operator_approvals values ((:id), (:time))")
                .setParameter("id", id)
                .setParameter("time", applicationTime)
                .executeUpdate();
        databaseSession.getTransaction().commit();
    }

    public HashMap<Date, Integer> getApprovals() {
        var query = databaseSession.createNativeQuery("select operatorid from operator_approvals");
        List<Integer> listID = query.list();
        query = databaseSession.createNativeQuery("select time_submitted from operator_approvals");
        List<Date> listTime = query.list();

        HashMap<Date, Integer> map = new HashMap<>();
        for (int i = 0; i < listID.size(); i++) {
            map.put(listTime.get(i), listID.get(i));
        }
        return map;
    }

    public Design getDeignFromIso(String isoID) {
        var query = databaseSession.createQuery("from Design where isoID = (:isoid)")
                .setParameter("isoid", isoID);
        List<Design> list = query.list();

        if (list.size() != 0) return list.get(0);
        return null;
    }

    public List<Design> searchDesigns(SearchConditions nameSearch) {
        System.out.println("SEARCHING DESIGNS");
        String name = nameSearch.getSearch();
        String initial1 = nameSearch.getStartLetters()[0];
        String initial2 = nameSearch.getStartLetters()[1];
        Integer region = nameSearch.getRegion();
        Integer type = nameSearch.getType();

        var query = databaseSession.createQuery("from Design where LOWER(name) like (:name) and (LOWER(name) >= (:initial1) and LOWER(name) <= (:initial2) or LOWER(name) " +
                                                   "like (:initial3)) and (region = :region or :region is null) and (type = :type or :type is null) order by name asc")
                .setParameter("name", name == null ? "%" : "%" + name.toLowerCase() + "%")
                .setParameter("initial1", initial1 == null ? "a" : initial1)
                .setParameter("initial2", initial2 == null ? "z" : initial2)
                .setParameter("initial3", initial2 == null ? "z%" : initial2 + "%")
                .setParameter("region", region)
                .setParameter("type", type);
        List<Design> list = query.list();
        return list;
    }

    public Flag createFlag(String isoID, FLAG_SIZE size) {
        System.out.println("Making Flag");

        var query = databaseSession.createQuery("from Flag where isoID = '" + isoID + "'");
        List<Flag> listFlag = query.list();
        if (listFlag.size() == 0) return null;

        Flag f = listFlag.get(0);

        query = databaseSession.createQuery("select name from Design where isoID = '" + isoID + "'");
        List<String> listName = query.list();
        if (listName.size() == 0) return null;

        f.setName(listName.get(0));

        f.setSizeID(FLAG_SIZE.getSizeId(size));
        f.setSize(size);
        setStockData(f);
        return f;
    }

    public Cushion createCushion(String isoID, CUSHION_SIZE size, CUSHION_MATERIAL material) {
        System.out.println("Making Cushion");

        var query = databaseSession.createQuery("from Cushion where isoID = '" + isoID + "'");
        List<Cushion> listCushion = query.list();
        if (listCushion.size() == 0) return null;

        Cushion c = listCushion.get(0);

        query = databaseSession.createQuery("select name from Design where isoID = '" + isoID + "'");
        List<String> listName = query.list();
        if (listName.size() == 0) return null;

        c.setName(listName.get(0));

        c.setSizeID(CUSHION_SIZE.getSizeId(size));
        c.setSize(size);
        c.setMaterial(material);
        setStockData(c);
        return c;
    }

    public StockItem setStockData(StockItem i) {
        try {
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

            query = databaseSession.createNativeQuery("select price from sizes where sizeid = (:sizeid)")
                    .setParameter("sizeid", i.getSizeID());
            List<Double> price = query.list();
            i.setCostToProduce(price.get(0));

            System.out.println("Got stock data");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    public String getRegionName(int regionID) {
        var query = databaseSession.createNativeQuery("select name from regions where regionid = '" + regionID + "'");
        List<String> list = query.list();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    public String getTypeName(int typeID) {
        var query = databaseSession.createNativeQuery("select name from designtypes where typeid = '" + typeID + "'");
        List<String> list = query.list();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    public Integer getTypeId(String name) {
        var query = databaseSession.createNativeQuery("select typeid from designtypes where LOWER(name) = (:name)")
                .setParameter("name", name.toLowerCase());
        List<Integer> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public Integer getRegionId(String name) {
        var query = databaseSession.createNativeQuery("select regionid from regions where LOWER(name) = (:name)")
                .setParameter("name", name.toLowerCase());
        List<Integer> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public boolean[] restockList(int stockID) {
        var queryAmount = "select amount from stock_amount where stockid = " + stockID;
        var queryRestock = "select restock from stock_amount where stockid = " + stockID;

        List<Integer> listAmount = databaseSession.createNativeQuery(queryAmount).list();
        List<Integer> listRestock = databaseSession.createNativeQuery(queryRestock).list();

        boolean[] b = new boolean[listAmount.size()];
        for (int i = 0; i < b.length; i++) {
            b[i] = listRestock.get(i) >= listAmount.get(i);
        }
        return b;
    }

    public void updateAmountAndRestock(int stockID, int sizeID, int newAmount, int newRestock) {
        databaseSession.beginTransaction();
        try {
            databaseSession.createNativeQuery("update stock_amount set amount = (:amount), restock = (:restock) where stockid = (:stockid) and sizeid = (:sizeid)")
                    .setParameter("amount", newAmount)
                    .setParameter("restock", newRestock)
                    .setParameter("stockid", stockID)
                    .setParameter("sizeid", sizeID)
                    .executeUpdate();
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
    }

    public Double getPrice(int sizeID) {
        var query = databaseSession.createNativeQuery("select price from sizes where sizeid = (:sizeid)")
                .setParameter("sizeid", sizeID);
        List<Double> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public HashMap<String, Flag> getAllFlags() {
        String sql = "select f.flagid, f.isoid, f.stockid, sa.sizeid, sa.amount, sa.restock, sz.price " +
                "from flags f " +
                "join stock_amount sa on f.stockid = sa.stockid " +
                "join sizes sz on sa.sizeid = sz.sizeid";

        var query = databaseSession.createNativeQuery(sql);
        List<Object[]> list = query.getResultList();

        HashMap<String, Flag> map = new HashMap<>();
        for (Object[] o : list) {
            Flag f = new Flag();
            f.setFlagID((int)o[0]);
            f.setIsoID((String) o[1]);
            f.setStockID((int) o[2]);
            f.setSizeID((int)o[3]);
            f.setSize(FLAG_SIZE.fromSizeId(f.getSizeID()));
            f.setTotalAmount((int)o[4]);
            f.setRestock((int)o[5]);
            f.setCostToProduce((double)o[6]);

            map.put(f.getIsoID() + "_" + f.getSizeID(), f);
        }
        return map;
    }

    public HashMap<String, Cushion> getAllCushions() {
        String sql = "select c.cushionid, c.isoid, c.stockid, sa.sizeid, sa.amount, sa.restock, sz.price " +
                "from cushions c " +
                "join stock_amount sa on c.stockid = sa.stockid " +
                "join sizes sz on sa.sizeid = sz.sizeid";

        var query = databaseSession.createNativeQuery(sql);
        List<Object[]> list = query.getResultList();

        HashMap<String, Cushion> map = new HashMap<>();
        for (Object[] o : list) {
            Cushion c = new Cushion();
            c.setCushionID((int)o[0]);
            c.setIsoID((String) o[1]);
            c.setStockID((int) o[2]);
            c.setSizeID((int)o[3]);
            c.setSize(CUSHION_SIZE.fromSizeId(c.getSizeID()));
            c.setTotalAmount((int)o[4]);
            c.setRestock((int)o[5]);
            c.setCostToProduce((double)o[6]);
            c.setMaterial(CUSHION_MATERIAL.EMPTY);

            map.put(c.getIsoID() + "_" + c.getSizeID(), c);
        }
        return map;
    }

    //SQL used for database setup:


    public void AddDesigns () {
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
    }

    public void AddTags () {
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
    }

    public void AddFlags() {
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
    }

    public void AddCushions() {
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
    }

    public void AddStockItem() {
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
    }

    public void SetAmounts() {
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
    }

    public void TestSQL() {
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
    }
}
