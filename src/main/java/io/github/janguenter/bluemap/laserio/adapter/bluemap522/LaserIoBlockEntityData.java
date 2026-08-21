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
        private int[] position;

        public RenderedConnection() {
        }

        int[] position() {
            return position == null ? null : position.clone();
        }
    }

    /** Exact advanced-connector global-position compound. */
    public static final class PartnerPosition {

        @NBTName("dimension")
        private String dimension;

        @NBTName("blockpos")
        private int[] blockPosition;

        public PartnerPosition() {
        }

        String dimension() {
            return dimension;
        }

        int[] blockPosition() {
            return blockPosition == null ? null : blockPosition.clone();
        }
    }
}
