package org.vexillum;

import org.hibernate.*;
import org.hibernate.query.*;
import org.hibernate.cfg.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.json.*;

/**
 * This class is to be used for all calls and access to the database, if a new piece of functionality for database
 * access is needed it should be added as a method to this class
 */
public class DatabaseControl {
    private Session databaseSession = null;
    private SessionFactory sessionFactory;

    public DatabaseControl() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    /**
     * Opens the session, only if needed
     */
    public void openDBSession() {
        if (databaseSession == null || !databaseSession.isOpen()) {
            System.out.println("Opening");
            databaseSession = sessionFactory.openSession();
            System.out.println("Opened");
        }
    }

    public boolean isOpen() {
        return databaseSession != null;
    }

    /**
     * Closes the session, but not the connection
     */
    public void closeDBSession() {
        if (databaseSession != null && databaseSession.isOpen()) {
            System.out.println("Closing session");
            databaseSession.close();
            databaseSession = null;
            System.out.println("Closed session");
        }
    }

    /**
     * Returns a list of operators with the given name, this should only ever be 1 since operators cannot have the same name
     * @param nameSearch the name to find
     * @return a list of operators matching that name, which will be at most 1 or 0
     */
    public List<Operator> getSpecificOperator(String nameSearch) {
        var query = databaseSession.createQuery("from Operator where name = (:name)")
                .setParameter("name", nameSearch);
        List<Operator> list = query.list();
        return list;
    }
    /**
     * Returns a list of operators who have an id from the provided array
     * @param ids an array of ids that are searched for inside the database
     * @return a list of operators in order of their arrays
     */
    public List<Operator> getOperatorsByID(Integer[] ids) {
        var query = databaseSession.createQuery("from Operator where id in (:ids) order by id asc")
                .setParameter("ids", Arrays.asList(ids));
        List<Operator> list = query.list();
        return list;
    }
    /**
     * Returns a list of operators who are not yet approved by the system and who have an id from the provided array
     * @param ids an array of ids that are searched for inside the database
     * @return a list of operators in order of their arrays
     */
    public List<Operator> getOperatorsByIDAwaitingApproval(Integer[] ids) {
        var query = databaseSession.createQuery("from Operator where id in (:ids) and approved = false order by id asc")
                .setParameter("ids", Arrays.asList(ids));
        List<Operator> list = query.list();
        return list;
    }

    /**
     * Creates a new operator, who is unapproved, and adds that operator to the awaiting_approvals table
     * @param id the new id of the operator, this must be unique
     * @param name the new name of the operator, this must be unique
     * @param password the password of the operator
     * @param applicationTime the time the application was submitted
     */
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

    /**
     * Accepts the operator, setting them to approved, and removes them from the awaiting_approvals table
     * @param id the id of the operator who is being approved
     */
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

    /**
     * Denys the operator, removing them from both the awaiting_approvals and general operators table
     * @param id the id of the operator who is being denied
     */
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

    /**
     * Promotes an operator from a regular user to an admin, only used in testing
     * @param id the id of the operator being promoted
     */
    public void promoteOperator(int id) {
        databaseSession.beginTransaction();
        try {
            databaseSession.createNativeQuery("update operators set administrator = true where operatorid = (:id)")
                    .setParameter("id", id)
                    .executeUpdate();
            databaseSession.getTransaction().commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
    }

    /**
     * Returns all the existing ids for operators
     * @return a list of integers, for each operator
     */
    public List<Integer> getExistentIDs() {
        var query = databaseSession.createQuery("select operatorID from Operator");
        List<Integer> list = query.list();
        return list;
    }

    /**
     * Used to get all the operators awaiting approval, by only returning their IDs
     * @return a HashMap with the Date as a key and the operator's id as a value, in ascending order of the date
     */
    public HashMap<Date, Integer> getApprovals() {
        var query = databaseSession.createNativeQuery("select operatorid from operator_approvals order by time_submitted asc");
        List<Integer> listID = query.list();
        query = databaseSession.createNativeQuery("select time_submitted from operator_approvals order by time_submitted asc");
        List<Date> listTime = query.list();

        HashMap<Date, Integer> map = new HashMap<>();
        for (int i = 0; i < listID.size(); i++) {
            map.put(listTime.get(i), listID.get(i));
        }

        return map;
    }

    /**
     * Gets a design from the designs table from an ID
     * @param isoID the primary key of the table
     * @return a single design which has the same ID as the one provided
     */
    public Design getDeignFromIso(String isoID) {
        var query = databaseSession.createQuery("from Design where isoID = (:isoid)")
                .setParameter("isoid", isoID);
        List<Design> list = query.list();

        if (list.size() != 0) return list.get(0);
        return null;
    }

    /**
     * Used to search the designs table under several different conditions. There can either be no conditions present
     * all every single one and the search will still work
     * @param nameSearch a {@code SearchCondition} which contains the selected search conditions by the user
     * @return a list of all designs that match the given search conditions, this can be empty
     */
    public List<Design> searchDesigns(SearchConditions nameSearch) {
        if (nameSearch == null) return Collections.emptyList();
        String name = nameSearch.getSearch();
        String initial1 = nameSearch.getStartLetters()[0];
        String initial2 = nameSearch.getStartLetters()[1];
        Integer region = nameSearch.getRegion();
        Integer type = nameSearch.getType();

        //initial3 is necessary as although >= works in mySQL to get a string that is starts with a letter equal to or above the provided letter, <= does not, it only provides
        //ones that are less. If no initials are present, just setting them to a and z respectively works
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

    /**
     * Loads a flag from the database and fills it with the relevant information
     * @param isoID the ID for the design of the flag wanted
     * @param size the size of the flag, as an enum
     * @return a flag with an amount of 1 with all attributes filled
     */
    public Flag createFlag(String isoID, FLAG_SIZE size) {
        var query = databaseSession.createQuery("from Flag where isoID = (:isoID)")
                .setParameter("isoID", isoID);
        List<Flag> listFlag = query.list();
        if (listFlag.size() == 0) return null;

        Flag f = listFlag.get(0);

        query = databaseSession.createQuery("select name from Design where isoID = (:isoID)")
                .setParameter("isoID", isoID);
        List<String> listName = query.list();
        if (listName.size() == 0) return null;

        f.setName(listName.get(0));

        f.setSizeID(FLAG_SIZE.getSizeId(size));
        f.setSize(size);
        setStockData(f);
        return f;
    }

    /**
     * Loads a cushion from the database and fills it with the relevant information
     * @param isoID the ID for the design of the cushion wanted
     * @param size the size of cushion flag, as an enum
     * @return a cushion with an amount of 1 with all attributes filled
     */
    public Cushion createCushion(String isoID, CUSHION_SIZE size, CUSHION_MATERIAL material) {
        var query = databaseSession.createQuery("from Cushion where isoID = (:isoID)")
                .setParameter("isoID", isoID);
        List<Cushion> listCushion = query.list();
        if (listCushion.size() == 0) return null;

        Cushion c = listCushion.get(0);

        query = databaseSession.createQuery("select name from Design where isoID = (:isoID)")
                .setParameter("isoID", isoID);
        List<String> listName = query.list();
        if (listName.size() == 0) return null;

        c.setName(listName.get(0));

        c.setSizeID(CUSHION_SIZE.getSizeId(size));
        c.setSize(size);
        c.setMaterial(material);
        setStockData(c);
        return c;
    }

    /**
     * Gets information from the stock_amount table to correctly fill the {@code restock} and {@code totalAmount} attributes of {@code StockItems}
     * Also access the sizes and designs table to fill the size and national attributes
     * @param i the item to be modified
     * @return the provided item with the {@code StockItem} updated
     */
    public StockItem setStockData(StockItem i) {
        try {
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

            query = databaseSession.createNativeQuery("select typeid from designs where isoid = (:isoid)")
                    .setParameter("isoid", i.getIsoID());
            List<Integer> tags = query.list();
            i.setNational(tags.get(0).equals(TYPE.NATIONAL.getValue()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return i;
    }

    /**
     * Gets the name of the region from the database
     * @param regionID the ID of the region
     * @return the name of the relevant region
     */
    public String getRegionName(int regionID) {
        var query = databaseSession.createNativeQuery("select name from regions where regionid = (:regionID)")
                .setParameter("regionID", regionID);
        List<String> list = query.list();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    /**
     * Gets the name of the type from the database
     * @param typeID the ID of the type
     * @return the name of the relevant type
     */
    public String getTypeName(int typeID) {
        var query = databaseSession.createNativeQuery("select name from designtypes where typeid = (:typeID)")
                .setParameter("typeID", typeID);
        List<String> list = query.list();
        if (list.size() > 0) return list.get(0);
        return "";
    }

    /**
     * Gets the id of a type from its name
     * @param name the name of the type
     * @return the id of the relevant type
     */
    public Integer getTypeId(String name) {
        var query = databaseSession.createNativeQuery("select typeid from designtypes where LOWER(name) = (:name)")
                .setParameter("name", name.toLowerCase());
        List<Integer> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    /**
     * Gets the id of a region from its name
     * @param name the name of the region
     * @return the id of the relevant region
     */
    public Integer getRegionId(String name) {
        var query = databaseSession.createNativeQuery("select regionid from regions where LOWER(name) = (:name)")
                .setParameter("name", name.toLowerCase());
        List<Integer> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    /**
     * Checks each size of a stock and returns an array saying if the item needs restocking or not
     * @param stockID the ID of the stock that is having its sizes checked
     * @return a boolean in order with true relating to items that are in need of restock and false if they don't
     */
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

    /**
     * Sets the restock limit and the amount in stock to the values provided
     * @param stockID the ID of the stock being modified
     * @param sizeID the ID of the size
     * @param newAmount the amount that should now be in stock
     * @param newRestock the restock limit for warnings
     */
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

    /**
     * Gets the cost to produce for an item
     * @param sizeID the size of the item
     * @return a double that is the price to produce
     */
    public Double getPrice(int sizeID) {
        var query = databaseSession.createNativeQuery("select price from sizes where sizeid = (:sizeid)")
                .setParameter("sizeid", sizeID);
        List<Double> list = query.list();
        if (list.size() > 0) return list.get(0);
        return null;
    }

    /**
     * Returns all flags in a hashmap, this isn't just designs but also every single size for every design
     * @return a hashmap with the key being the isoID_sizeID and the value being the flag itself
     */
    public HashMap<String, Flag> getAllFlags() {
        String sql = "select f.flagid, f.isoid, f.stockid, sa.sizeid, sa.amount, sa.restock, sz.price, d.name, d.typeid " +
                "from flags f " +
                "join stock_amount sa on f.stockid = sa.stockid " +
                "join sizes sz on sa.sizeid = sz.sizeid " +
                "join designs d on f.isoid = d.isoid";

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
            f.setName((String)o[7]);
            f.setNational((int)o[8] == TYPE.NATIONAL.getValue());

            map.put(f.getIsoID() + "_" + f.getSizeID(), f);
        }
        return map;
    }

    /**
     * Returns all cushions in a hashmap, this isn't just designs but also every single size for every design
     * @return a hashmap with the key being the isoID_sizeID and the value being the cushion itself
     */
    public HashMap<String, Cushion> getAllCushions() {
        String sql = "select c.cushionid, c.isoid, c.stockid, sa.sizeid, sa.amount, sa.restock, sz.price, d.name, d.typeid " +
                "from cushions c " +
                "join stock_amount sa on c.stockid = sa.stockid " +
                "join sizes sz on sa.sizeid = sz.sizeid " +
                "join designs d on c.isoid = d.isoid";

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
            c.setName((String)o[7]);
            c.setNational((int)o[8] == TYPE.NATIONAL.getValue());

            map.put(c.getIsoID() + "_" + c.getSizeID(), c);
        }
        return map;
    }


    /*
    Below this point are methods only used for the creation of the database, they are completely defunct and not commented
    since they no longer serve a purpose inside the system
     */


    @Deprecated
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

    @Deprecated
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
        }
        catch (Exception e) {
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
    }

    @Deprecated
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
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
    }

    @Deprecated
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
            e.printStackTrace();
            databaseSession.getTransaction().rollback();
        }
    }

    @Deprecated
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
            e.printStackTrace();
        }
    }

    @Deprecated
    public void SetAmounts() {
        try {
            databaseSession.beginTransaction();

            var query = databaseSession.createNativeQuery("select * from stockmanager");
            List<Design> list = query.list();

            Random rnd = new Random();

            for (int i = 0; i < list.size(); i++) {
                int runAmount = i <= list.size() / 2 - 1 ? 5 : 9;
                int start = runAmount == 5 ? 0 : 5;
;
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
            e.printStackTrace();
        }
    }

    @Deprecated
    public void TestSQL() {
        try {
            String sql = "SELECT * FROM sizes";
            NativeQuery<Object[]> query = databaseSession.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                int id = (int) row[0];
                String name = (String) row[1];
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
