package org.vexillum;

import javax.persistence.*;

/**
 * Object used to store the design of a flag, which is then stored in StockItems
 * <p>
 * Utilises hibernate mapping
 */
@Entity
public class Design {
    private String isoID, name;
    //Integer over int must be used as it has to be capable of storing null value
    private Integer type, region;

    /**
     * Empty constructor only for hibernate
     */
    public Design() {}

    /**
     * Constructor primarily used for creation of a new version of the object
     */
    public Design(String isoID, String name) {
        this.isoID = isoID;
        this.name = name;
        type = null;
        region = null;
    }

    @Id
    public String getIsoID() {
        return isoID;
    }

    public void setIsoID(String isoID) {
        this.isoID = isoID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }
}
