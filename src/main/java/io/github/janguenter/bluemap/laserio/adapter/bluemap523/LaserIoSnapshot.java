/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import java.util.List;

/** Deterministic stable visual state decoded from one LaserIO block entity. */
record LaserIoSnapshot(
        int argb,
        List<Offset> renderedConnections,
        boolean advancedPartner
) {

    LaserIoSnapshot {
        renderedConnections = List.copyOf(renderedConnections);
    }

    float red() {
        return ((argb >>> 16) & 0xFF) / 255F;
    }

    float green() {
        return ((argb >>> 8) & 0xFF) / 255F;
    }

    float blue() {
        return (argb & 0xFF) / 255F;
    }

    /** Relative target position persisted by BaseLaserBE. */
    record Offset(int x, int y, int z) implements Comparable<Offset> {

        @Override
        public int compareTo(Offset other) {
            int result = Integer.compare(x, other.x);
            if (result == 0) {
                result = Integer.compare(y, other.y);
            }
            if (result == 0) {
                result = Integer.compare(z, other.z);
            }
            return result;
        }
    }
}
