package ru.hse.routemood.gpt;

public class RouteItem {

    private double latitude;
    private double longitude;

    @Override
    public String toString() {
        return "[latitude = " + latitude + ", longitude = " + longitude + "]";
    }
}