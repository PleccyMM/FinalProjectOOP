package org.vexillum;

import java.util.Objects;
import javax.persistence.*;

@Entity
public class Design {
    private String isoID, name;
    private TYPE type;
    private REGION region;

    public Design() {}
    public Design(String isoID, String name) {
        this.isoID = isoID;
        this.name = name;
        type = null;
        region = null;
    }
    public Design(String isoID, String name, TYPE type, REGION region) {
        this.isoID = isoID;
        this.name = name;
        this.type = type;
        this.region = region;
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

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public REGION getRegion() {
        return region;
    }

    public void setRegion(REGION region) {
        this.region = region;
    }
}
