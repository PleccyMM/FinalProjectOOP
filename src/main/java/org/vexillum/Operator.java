package org.vexillum;

import java.util.Objects;
import javax.persistence.*;

/**
 * Object used to store the current logged-in user
 * <p>
 * Utilises hibernate mapping
 */
@Entity
public class Operator {
    private int operatorID;
    private String name, password;
    private boolean approved, administrator;


    /**
     * Empty constructor only for hibernate
     */
    public Operator() { }

    /**
     * Constructor primarily used for creation of a new version of the object
     */
    public Operator(String name, String password)
    {
        operatorID = generateID();
        this.name = name;
        this.password = password;
        this.approved = false;
        this.administrator = false;
    }

    @Override
    public String toString() {
        return "ID: " + operatorID + "\nName: " + name + "\nPassword: " + password + "\nApproved: " + approved + "\nAdministrator: " + administrator;
    }

    private int generateID() {
        return 0;
    }

    /**
     * Used to perform the login logic check for the {@code LoginController}
     * @param password a string to be compared against the already loaded password attribute
     * @return a true value if the password provided matches the operator's one, and the operator is approved
     */
    public boolean attemptLogin(String password) {
        return Objects.equals(password, this.password) && approved;
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
