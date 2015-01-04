package org.devnexus.vo;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents data from the presentation service on devnexus.org
 */
public class PresentationResponse implements Serializable  {

    public PresentationList presentationList;

    public static class PresentationList implements Serializable  {

        public List<Presentation> presentation;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PresentationList)) return false;

            PresentationList that = (PresentationList) o;

            if (!presentation.equals(that.presentation)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return presentation.hashCode();
        }

        @Override
        public String toString() {
            return "PresentationList{" +
                    "presentation=" + presentation +
                    '}';
        }
    }
}
