package org.vexillum;

import java.util.Objects;
import javax.persistence.*;

@Entity
public class Operator {
    private int operatorID;
    private String name, password;
    private boolean approved, administrator;

    public Operator() {}
    public Operator(String name, String password)
    {
        operatorID = GenerateID();
        this.name = name;
        this.password = password;
        this.approved = false;
        this.administrator = false;
    }

    @Override
    public String toString() {
        return "ID: " + operatorID + "\nName: " + name + "\nPassword: " + password + "\nApproved: " + approved + "\nAdministrator: " + administrator;
    }

    private int GenerateID() {
        return 0;
    }

    public boolean AttemptLogin(String password) {
        return Objects.equals(password, this.password);
    }

    @Id
    public int getOperatorID() {
        return operatorID;
    }
    public void setOperatorID(int operatorID) {
        this.operatorID = operatorID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isAdministrator() {
        return administrator;
    }
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
