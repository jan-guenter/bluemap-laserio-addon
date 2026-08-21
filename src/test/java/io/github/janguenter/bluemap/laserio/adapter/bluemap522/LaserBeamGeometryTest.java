/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LaserBeamGeometryTest {

    @Test
    void horizontalBeamProducesTwoCrossedDoubleSidedPlanes() {
        LaserBeamGeometry.Point start = new LaserBeamGeometry.Point(0.5F, 0.5F, 0.5F);
        LaserBeamGeometry.Point end = new LaserBeamGeometry.Point(4.5F, 0.5F, 0.5F);
        List<LaserBeamGeometry.Triangle> triangles =
                LaserBeamGeometry.between(start, end);

        assertEquals(8, triangles.size());
        for (LaserBeamGeometry.Triangle triangle : triangles) {
            for (LaserBeamGeometry.Vertex vertex : vertices(triangle)) {
                assertTrue(vertex.point().finite());
                assertTrue(Math.abs(vertex.point().x() - 0.5F) < 0.0001F
                        || Math.abs(vertex.point().x() - 4.5F) < 0.0001F);
                double radial = Math.hypot(
                        vertex.point().y() - 0.5F,
                        vertex.point().z() - 0.5F
                );
                assertEquals(LaserBeamGeometry.HALF_WIDTH, radial, 0.00001F);
            }
        }
        assertTrue(dot(normal(triangles.get(0)), normal(triangles.get(2))) < 0F);
        assertTrue(dot(normal(triangles.get(4)), normal(triangles.get(6))) < 0F);
        assertTrue(Math.abs(dot(normal(triangles.get(0)), normal(triangles.get(4))))
                < 0.00001F);
    }

    @Test
    void verticalAndDiagonalBeamsRemainFinite() {
        LaserBeamGeometry.Point start = new LaserBeamGeometry.Point(0.5F, 0.5F, 0.5F);
        for (LaserBeamGeometry.Point end : List.of(
                new LaserBeamGeometry.Point(0.5F, 5.5F, 0.5F),
                new LaserBeamGeometry.Point(-2.5F, 2.5F, 4.5F)
        )) {
            List<LaserBeamGeometry.Triangle> triangles =
                    LaserBeamGeometry.between(start, end);
            assertEquals(8, triangles.size());
            assertTrue(triangles.stream().flatMap(triangle -> vertices(triangle).stream())
                    .allMatch(vertex -> vertex.point().finite()));
        }
    }

    @Test
    void degenerateOrNonFiniteBeamIsRejected() {
        LaserBeamGeometry.Point point = new LaserBeamGeometry.Point(0.5F, 0.5F, 0.5F);
        assertTrue(LaserBeamGeometry.between(point, point).isEmpty());
        assertTrue(LaserBeamGeometry.between(
                point, new LaserBeamGeometry.Point(Float.NaN, 0F, 0F)
        ).isEmpty());
    }

    private static List<LaserBeamGeometry.Vertex> vertices(
            LaserBeamGeometry.Triangle triangle
    ) {
        return List.of(triangle.first(), triangle.second(), triangle.third());
    }

    private static LaserBeamGeometry.Point normal(LaserBeamGeometry.Triangle triangle) {
        LaserBeamGeometry.Point first = triangle.second().point()
                .subtract(triangle.first().point());
        LaserBeamGeometry.Point second = triangle.third().point()
                .subtract(triangle.first().point());
        return first.cross(second).normalized();
    }

    private static float dot(LaserBeamGeometry.Point first, LaserBeamGeometry.Point second) {
        return first.x() * second.x() + first.y() * second.y() + first.z() * second.z();
    }
}
