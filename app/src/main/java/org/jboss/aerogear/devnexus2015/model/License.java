package org.jboss.aerogear.devnexus2015.model;

/**
 * Created by summers on 1/10/16.
 */
public class License implements Comparable<License> {
    public String project, link, text;

    @Override
    public int compareTo(License another) {
        return project.compareTo(another.project);
    }
}
