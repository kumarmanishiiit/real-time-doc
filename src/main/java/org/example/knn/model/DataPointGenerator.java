package org.example.knn.model;

import java.text.DecimalFormat;
import java.util.Random;

public class DataPointGenerator {

    // Class representing a 2D point
    static class Point {
        double x;
        double y;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public static void main(String[] args) {
        int numPoints = 10; // Number of sample points to generate
        double xRange = 100; // Maximum x value
        double yRange = 100; // Maximum y value

        Point[] points = generate2DPoints(numPoints, xRange, yRange);
        for (Point point : points) {
            System.out.println(point);
        }
    }

    // Method to generate random 2D points
    public static Point[] generate2DPoints(int numPoints, double xRange, double yRange) {
        Random rand = new Random();
        Point[] points = new Point[numPoints];

        DecimalFormat df=new DecimalFormat("0.00");
        for (int i = 0; i < numPoints; i++) {
            double x = xRange * rand.nextDouble();
            double y = yRange * rand.nextDouble() ;
            points[i] = new Point(Double.valueOf(df.format(x)), Double.valueOf(df.format(y)));
        }

        return points;
    }
}
