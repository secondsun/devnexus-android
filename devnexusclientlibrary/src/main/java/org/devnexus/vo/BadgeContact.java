package org.devnexus.vo;

import org.jboss.aerogear.android.core.RecordId;

import java.io.Serializable;

import static org.devnexus.util.StringUtils.compare;

/**
 * Data Stored in a scanned Badge
 */
public class BadgeContact implements Serializable, Comparable<BadgeContact> {

    @RecordId
    private int id;

    private String firstName, lastName, organization, title, email;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BadgeContact that = (BadgeContact) o;

        if (id != that.id) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null)
            return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null)
            return false;
        if (organization != null ? !organization.equals(that.organization) : that.organization != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        return email != null ? email.equals(that.email) : that.email == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (organization != null ? organization.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    public int getId() {
        return hashCode();
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int compareTo(BadgeContact another) {
        if (this.equals(another)) {
            return 0;
        }

        if (another == null) {
            return 1;
        }

        if (compare(this.lastName, another.lastName) == 0) {
            if (compare(this.firstName, another.firstName) == 0) {
                if (compare(this.email, another.email) == 0) {
                    return id - (another.id);
                } else {
                    return compare(this.email, another.email);
                }
            } else {
                return compare(this.firstName, another.firstName);
            }
        } else {
            return compare(this.lastName, another.lastName);
        }

    }
}
