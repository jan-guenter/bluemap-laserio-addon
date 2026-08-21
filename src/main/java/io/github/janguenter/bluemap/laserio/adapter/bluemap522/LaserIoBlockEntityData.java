/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.blockentity.MCABlockEntity;
import de.bluecolored.bluenbt.NBTName;

import java.util.List;

/** Exact BlueNBT projection of LaserIO's stable visual fields. */
public final class LaserIoBlockEntityData extends MCABlockEntity {

    @NBTName("laserColor")
    private Integer laserColor;

    @NBTName("renderedConnections")
    private List<RenderedConnection> renderedConnections;

    @NBTName("partnerDimPos")
    private PartnerPosition partnerDimPos;

    public LaserIoBlockEntityData() {
    }

    Integer laserColor() {
        return laserColor;
    }

    List<RenderedConnection> renderedConnections() {
        return renderedConnections == null ? List.of() : List.copyOf(renderedConnections);
    }

    PartnerPosition partnerDimPos() {
        return partnerDimPos;
    }

    /** One entry in the one-sided persisted render-owner set. */
    public static final class RenderedConnection {

        @NBTName("pos")
        private Position position;

        public RenderedConnection() {
        }

        Position position() {
            return position;
        }
    }

    /** Minecraft's exact compound representation written by NbtUtils. */
    public static final class Position {

        @NBTName("X")
        private Integer x;

        @NBTName("Y")
        private Integer y;

        @NBTName("Z")
        private Integer z;

        public Position() {
        }

        Integer x() {
            return x;
        }

        Integer y() {
            return y;
        }

        Integer z() {
            return z;
        }
    }

    /** Exact advanced-connector global-position compound. */
    public static final class PartnerPosition {

        @NBTName("dimension")
        private String dimension;

        @NBTName("blockpos")
        private Position blockPosition;

        public PartnerPosition() {
        }

        String dimension() {
            return dimension;
        }

        Position blockPosition() {
            return blockPosition;
        }
    }
}
