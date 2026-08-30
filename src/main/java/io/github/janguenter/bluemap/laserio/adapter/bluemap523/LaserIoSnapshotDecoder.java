/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;

/** Strict decoder for the stable subset rendered by this add-on. */
final class LaserIoSnapshotDecoder {

    static final int DEFAULT_COLOR = 0x54FF_0000;
    private static final Pattern DIMENSION = Pattern.compile(
            "[a-z0-9_.-]+:[a-z0-9/._-]+"
    );

    LaserIoSnapshot decode(LaserIoBlockEntityData data) {
        Objects.requireNonNull(data, "data");
        int color = data.laserColor() == null ? DEFAULT_COLOR : data.laserColor();
        TreeSet<LaserIoSnapshot.Offset> offsets = new TreeSet<>();
        for (LaserIoBlockEntityData.RenderedConnection entry :
                data.renderedConnections()) {
            LaserIoSnapshot.Offset offset = decodePosition(
                    entry == null ? null : entry.position()
            );
            if (normalConnection(offset)) {
                offsets.add(offset);
            }
        }
        return new LaserIoSnapshot(
                color,
                new ArrayList<>(offsets),
                validPartner(data.partnerDimPos())
        );
    }

    private static LaserIoSnapshot.Offset decodePosition(int[] position) {
        if (position == null || position.length != 3) {
            return null;
        }
        return new LaserIoSnapshot.Offset(
                position[0], position[1], position[2]
        );
    }

    private static boolean normalConnection(LaserIoSnapshot.Offset offset) {
        if (offset == null || offset.x() == 0 && offset.y() == 0 && offset.z() == 0
                || Math.abs((long) offset.x()) >= 8L
                || Math.abs((long) offset.y()) >= 8L
                || Math.abs((long) offset.z()) >= 8L) {
            return false;
        }
        long distanceSquared = (long) offset.x() * offset.x()
                + (long) offset.y() * offset.y()
                + (long) offset.z() * offset.z();
        return distanceSquared < 64L;
    }

    private static boolean validPartner(LaserIoBlockEntityData.PartnerPosition partner) {
        if (partner == null || partner.dimension() == null
                || !DIMENSION.matcher(partner.dimension()).matches()) {
            return false;
        }
        LaserIoSnapshot.Offset position = decodePosition(partner.blockPosition());
        return position != null
                && (position.x() != 0 || position.y() != 0 || position.z() != 0);
    }
}
