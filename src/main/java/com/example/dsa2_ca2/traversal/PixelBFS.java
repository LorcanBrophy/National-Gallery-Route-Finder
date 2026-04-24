package com.example.dsa2_ca2.traversal;

import com.example.dsa2_ca2.model.MyArrayList;
import com.example.dsa2_ca2.model.MyList;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.*;

public class PixelBFS {

    public record Point(int x, int y) {}

    // returns shortest path using BFS
    public static MyList<Point> traverse(BufferedImage image, Point start, Point end) {
        MyList<Point> result = new MyArrayList<>();

        Set<Point> visited = new HashSet<>();
        Queue<Point> queue = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();

        visited.add(start);
        queue.add(start);
        parent.put(start, null);

        boolean found = false;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        // actual bfs traversal
        while (!queue.isEmpty()) {

            Point current = queue.poll(); // returns/removes first element in queue in FIFO order

            if (current.equals(end)) {
                found = true;
                break;
            }


            for (int i = 0; i < 4; i++) {
                int nextX = current.x + dx[i];
                int nextY = current.y + dy[i];

                if (nextX < 0 || nextY < 0 || nextX >= image.getWidth() || nextY >= image.getHeight()) continue;

                int colour = image.getRGB(nextX, nextY);
                int rgb = colour & 0xFFFFFF;

                if (rgb < 0xCCCCCC) continue;

                Point neighbor = new Point(nextX, nextY);

                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                    parent.put(neighbor, current);
                }
            }
        }

        if (!found) return result;


        // now that

        LinkedList<Point> path = new LinkedList<>();

        Point step = end;
        while (step != null) {
            path.addFirst(step);
            step = parent.get(step);
        }

        result.addAll(path);
        return result;
    }
}
