/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import java.util.ArrayList;
import java.util.List;

/** Camera-independent approximation using two crossed, double-sided laser ribbons. */
final class LaserBeamGeometry {

    static final float HALF_WIDTH = 0.025F;

    private LaserBeamGeometry() {
    }

    static List<Triangle> between(Point start, Point end) {
        if (start == null || end == null || !start.finite() || !end.finite()) {
            return List.of();
        }
        Point direction = end.subtract(start);
        float length = direction.length();
        if (!(length > 0F) || !Float.isFinite(length)) {
            return List.of();
        }
        Point normalized = direction.scale(1F / length);
        Point helper = leastParallelAxis(normalized);
        Point first = normalized.cross(helper).normalized().scale(HALF_WIDTH);
        Point second = normalized.cross(first.normalized()).normalized().scale(HALF_WIDTH);
        if (!first.finite() || !second.finite()) {
            return List.of();
        }

        float endV = (end.y() - start.y()) * 1.5F;
        ArrayList<Triangle> triangles = new ArrayList<>(8);
        plane(triangles, start, end, first, endV);
        plane(triangles, start, end, second, endV);
        return List.copyOf(triangles);
    }

    private static Point leastParallelAxis(Point direction) {
        float x = Math.abs(direction.x());
        float y = Math.abs(direction.y());
        float z = Math.abs(direction.z());
        if (x <= y && x <= z) {
            return new Point(1F, 0F, 0F);
        }
        if (y <= z) {
            return new Point(0F, 1F, 0F);
        }
        return new Point(0F, 0F, 1F);
    }

    private static void plane(
            List<Triangle> triangles,
            Point start,
            Point end,
            Point offset,
            float endV
    ) {
        Vertex startA = new Vertex(start.add(offset), 1F, 0F);
        Vertex startB = new Vertex(start.subtract(offset), 0F, 0F);
        Vertex endB = new Vertex(end.subtract(offset), 0F, endV);
        Vertex endA = new Vertex(end.add(offset), 1F, endV);

        triangles.add(new Triangle(startA, startB, endB));
        triangles.add(new Triangle(startA, endB, endA));
        triangles.add(new Triangle(startA, endB, startB));
        triangles.add(new Triangle(startA, endA, endB));
    }

    record Point(float x, float y, float z) {

        Point add(Point other) {
            return new Point(x + other.x, y + other.y, z + other.z);
        }

        Point subtract(Point other) {
            return new Point(x - other.x, y - other.y, z - other.z);
        }

        Point scale(float factor) {
            return new Point(x * factor, y * factor, z * factor);
        }

        Point cross(Point other) {
            return new Point(
                    y * other.z - z * other.y,
                    z * other.x - x * other.z,
                    x * other.y - y * other.x
            );
        }

        float length() {
            return (float) Math.sqrt((double) x * x + (double) y * y + (double) z * z);
        }

        Point normalized() {
            float magnitude = length();
            return magnitude > 0F && Float.isFinite(magnitude)
                    ? scale(1F / magnitude) : new Point(Float.NaN, 0F, 0F);
        }

        boolean finite() {
            return Float.isFinite(x) && Float.isFinite(y) && Float.isFinite(z);
        }
    }

    record Vertex(Point point, float u, float v) {
    }

    record Triangle(Vertex first, Vertex second, Vertex third) {
    }
}
