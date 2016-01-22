package org.devnexus.vo;

import java.io.Serializable;
import java.util.Date;

public class Room implements Serializable {
    public int id;
    public Date createdDate;
    public Date updatedDate;
    public int version;

    public String name;
    public String track;
    public String cssStyleName;
    public int capacity;
    public String description;
    public int roomOrder;
    public String color;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (capacity != room.capacity) return false;
        if (roomOrder != room.roomOrder) return false;
        if (name != null ? !name.equals(room.name) : room.name != null) return false;
        if (track != null ? !track.equals(room.track) : room.track != null) return false;
        if (cssStyleName != null ? !cssStyleName.equals(room.cssStyleName) : room.cssStyleName != null)
            return false;
        if (description != null ? !description.equals(room.description) : room.description != null)
            return false;
        return color != null ? color.equals(room.color) : room.color == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (track != null ? track.hashCode() : 0);
        result = 31 * result + (cssStyleName != null ? cssStyleName.hashCode() : 0);
        result = 31 * result + capacity;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + roomOrder;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}
