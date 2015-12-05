package org.devnexus.vo;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents data from the presentations service on devnexus.org
 */
public class PresentationResponse implements Serializable  {


        public List<Presentation> presentations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PresentationResponse that = (PresentationResponse) o;

        return !(presentations != null ? !presentations.equals(that.presentations) : that.presentations != null);

    }

    @Override
        public int hashCode() {
            return presentations.hashCode();
        }

        @Override
        public String toString() {
            return "PresentationList{" +
                    "presentations=" + presentations +
                    '}';
        }
    }
