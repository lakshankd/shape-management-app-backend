package com.spillabs.shape_management_appliaction_api.util;

import com.spillabs.shape_management_appliaction_api.model.Point;
import com.spillabs.shape_management_appliaction_api.model.Shape;
import com.spillabs.shape_management_appliaction_api.model.ShapeGeometry;

import java.util.Arrays;
import java.util.List;

public class ShapeUtils {

    public static ShapeGeometry toGeometry(Shape shape) {
        String type = String.valueOf(shape.getType());

        if (shape.getCoordinates() == null || shape.getCoordinates().isBlank()) {
            throw new IllegalArgumentException("Coordinates are missing for shape ID: " + shape.getId());
        }

        if (type.equals("CIRCLE")) {
            String[] center = shape.getCoordinates().split(",");
            if (center.length != 2) {
                throw new IllegalArgumentException("Invalid circle coordinates for shape ID: " + shape.getId());
            }
            Point centerPoint = new Point(Double.parseDouble(center[0]), Double.parseDouble(center[1]));
            return new ShapeGeometry(shape.getId(), type, null, shape.getRadius(), centerPoint);
        } else {
            List<Point> points = Arrays.stream(shape.getCoordinates().split(";"))
                    .map(pair -> {
                        String[] coords = pair.split(",");
                        if (coords.length != 2) {
                            throw new IllegalArgumentException("Invalid coordinate format in shape ID: " + shape.getId());
                        }
                        return new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
                    }).toList();
            return new ShapeGeometry(shape.getId(), type, points, null, null);
        }
    }

    public static boolean doOverlap(ShapeGeometry s1, ShapeGeometry s2) {
        if (s1.isCircle() && s2.isCircle()) {
            return s1.getCenter().distance(s2.getCenter()) < (s1.getRadius() + s2.getRadius());
        }

        if (!s1.isCircle() && !s2.isCircle()) {
            return polygonOverlap(s1.getPoints(), s2.getPoints());
        }

        ShapeGeometry circle = s1.isCircle() ? s1 : s2;
        ShapeGeometry polygon = s1.isCircle() ? s2 : s1;
        return circlePolygonOverlap(circle, polygon);
    }

    public static boolean polygonOverlap(List<Point> a, List<Point> b) {
        for (Point p : a) if (pointInPolygon(p, b)) return true;
        for (Point p : b) if (pointInPolygon(p, a)) return true;
        return false;
    }

    public static boolean circlePolygonOverlap(ShapeGeometry circle, ShapeGeometry polygon) {
        for (Point vertex : polygon.getPoints()) {
            if (circle.getCenter().distance(vertex) <= circle.getRadius()) return true;
        }
        return pointInPolygon(circle.getCenter(), polygon.getPoints());
    }

    public static boolean pointInPolygon(Point point, List<Point> polygon) {
        int crossings = 0;
        for (int i = 0; i < polygon.size(); i++) {
            Point a = polygon.get(i);
            Point b = polygon.get((i + 1) % polygon.size());
            if (((a.getY() > point.getY()) != (b.getY() > point.getY())) &&
                    (point.getX() < (b.getX() - a.getX()) * (point.getY() - a.getY()) / (b.getY() - a.getY()) + a.getX())) {
                crossings++;
            }
        }
        return (crossings % 2 == 1);
    }
}
