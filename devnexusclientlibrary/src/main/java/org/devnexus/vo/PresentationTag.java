package org.devnexus.vo;

import java.io.Serializable;

public class PresentationTag implements Comparable<PresentationTag>, Serializable{

    public String name;

    public int id;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public int compareTo(PresentationTag o) {
        return name.compareTo(o.name);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PresentationTag other = (PresentationTag) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "PresentationTag [name=" + name + "]";
    }
}
