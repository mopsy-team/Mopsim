package mopsim.utils;

import org.matsim.api.core.v01.Coord;

public class NewRoadInfo {
    private final String name;
    private final Coord begin;
    private final Coord end;
    private final double length;

    public NewRoadInfo(String name, double xBegin, double yBegin, double xEnd, double yEnd, double length) {
        this.name = name;
        this.begin = new Coord(xBegin, yBegin);
        this.end = new Coord(xEnd, yEnd);
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public Coord getBegin() {
        return begin;
    }

    public Coord getEnd() {
        return end;
    }

}
