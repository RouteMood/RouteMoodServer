package ru.hse.routemood.gpt;

import lombok.Getter;

import java.util.List;

public class Route {

    private @Getter List<RouteItem> route;

    public class RouteItem {

        private double latitude;
        private double longitude;

        @Override
        public String toString() {
            return "[latitude = " + latitude + ", longitude = " + longitude + "]";
        }
    }
}