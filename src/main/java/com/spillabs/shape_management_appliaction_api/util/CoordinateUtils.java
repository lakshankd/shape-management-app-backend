package com.spillabs.shape_management_appliaction_api.util;

import com.spillabs.shape_management_appliaction_api.exception.InvalidShapeException;

import java.util.ArrayList;
import java.util.List;

public class CoordinateUtils {

    public static List<double[]> parseCoordinates(String coordString) {
        List<double[]> points = new ArrayList<>();

        if (coordString == null || coordString.trim().isEmpty()) {
            return points;
        }

        String[] pairs = coordString.split(";");
        for (String pair : pairs) {
            String[] xy = pair.trim().split(",");
            if (xy.length != 2) {
                throw new InvalidShapeException("Invalid coordinate format. Use 'x1,y1;x2,y2;...'");
            }

            try {
                double x = Double.parseDouble(xy[0].trim());
                double y = Double.parseDouble(xy[1].trim());
                points.add(new double[]{x, y});
            } catch (NumberFormatException e) {
                throw new InvalidShapeException("Coordinates must be valid numbers");
            }
        }

        return points;
    }
}
