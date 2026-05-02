package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.graph.Vertex;
import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.*;

public class PixelBFS {

    public record Point(int x, int y) {}

    // returns shortest path using BFS
    public static MyList<Point> traverse(BufferedImage image, Point start, Point end) {

        // final correct path to be returned
        MyList<Point> correctPath = new MyArrayList<>();

        // tracks visited nodes, can do if (!visited.contains(current)) to see if node is visited
        Set<Point> visited = new HashSet<>();
        visited.add(start);

        // queue to process neighbours
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        // stores the parents, used to reconstruct path later
        // key = child, value = parent
        Map<Point, Point> parent = new HashMap<>();
        parent.put(start, null);

        // flag to know if end has been found
        boolean found = false;

        // used to move to adjacent pixels
        // row = x, column = y
        int[][] directions = {
                {1, 0}, // down (row + 1)
                {-1, 0}, // up (row - 1)
                {0, 1}, // right (col + 1)
                {0, -1} // left (col - 1)
        };

        // actual bfs traversal
        while (!queue.isEmpty()) {

            // .poll() remove and returns the first element in the queue
            // queue = [A, B, C]
            // current = queue.poll()
            // queue = [B, C]
            // current = A
            Point current = queue.poll();

            if (current.equals(end)) {
                found = true;
                break;
            }

            // check adjacent pixels to current (C)
            // # # #
            // # C #
            // # # #
            for (int[] dir : directions) { // checks {1,0}, then {-1,0}, etc
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];

                // bound checks (not really neccessary tbh cause the gallery is within 0 and width/height
                if (nextX < 0 || nextY < 0 || nextX >= image.getWidth() || nextY >= image.getHeight()) continue;

                // gets colour of current pixel
                // returns 32 bit int
                // i.e. rgb = aaaaaaaa rrrrrrrr gggggggg bbbbbbbb
                //      rgb = 11111111 11111111 11111111 11111111
                int argb = image.getRGB(nextX, nextY);

                // remove alpha channels
                //          argb = 11111111 11111111 11111111 11111111
                // bitwise AND : & 00000000 11111111 11111111 11111111
                //           rgb = 00000000 11111111 11111111 11111111  = 00000000 rrrrrrrr gggggggg bbbbbbbb
                int rgb = argb & 0xFFFFFF;

                // checks if rgb is dark
                if (rgb < 0x808080) continue;

                // creates neighbour
                Point neighbour = new Point(nextX, nextY);

                // if the pixel is unvisited, add it to the queue
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                    parent.put(neighbour, current);
                }
            }
        }

        if (!found) return correctPath;


        // now that the target was found, the path can be reconstructed, starting from the end
        MyList<Point> reversePath = new MyArrayList<>();

        // parent = [null:A, A:B, B:C, D:E]
        // iterate until current = null, at the start, and add current to reverseList
        Point current = end;
        while (current != null) {
            reversePath.add(current);
            current = parent.get(current);
        }

        // reversePath = [E, D, C, B, A]
        // now need reverse it
        for (int i = reversePath.size() - 1; i >= 0; i--) {
            correctPath.add(reversePath.get(i));
        }

        return correctPath;
    }
}
