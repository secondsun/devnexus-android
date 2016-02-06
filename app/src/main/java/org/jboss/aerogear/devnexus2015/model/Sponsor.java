package org.jboss.aerogear.devnexus2015.model;

import java.io.Serializable;

public class Sponsor implements Serializable, Comparable<Sponsor> {

    public String id, name, sponsorLevel, link;

    public Level getLevelEnum() {
        return Level.fromLevel(sponsorLevel);
    }

    @Override
    public int compareTo(Sponsor another) {
        if (sponsorLevel.equals(another.sponsorLevel)) {
            return name.compareTo(another.name);
        } else {
            return ((Integer) getLevelEnum().ordinal()).compareTo(another.getLevelEnum().ordinal());
        }

    }

    public enum Level {
        PLATINUM("Platinum Sponsor"), GOLD("Gold Sponsor"), SILVER("Silver Sponsor"), BADGE("Badge Sponsor"), COCKTAIL_HOUR("Cocktail Hour"), MEDIA_PARTNER("Media Partner"), DEV_LOUNGE("Dev Lounge"), LANYARD("Lanyard");


        public final String title;

        Level(String title) {
            this.title = title;
        }

        public static Level fromLevel(String levelName) {
            for(Level level : values()) {
                if (level.name().equalsIgnoreCase(levelName)) {
                    return level;
                }
            }
            throw new IllegalArgumentException("No level " + levelName);
        }

    }
}
