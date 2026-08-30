/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.MaxCapacityReachedException;
import de.bluecolored.bluemap.core.map.hires.RenderSettings;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.map.hires.block.BlockRenderer;
import de.bluecolored.bluemap.core.map.hires.block.ResourceModelRenderer;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.math.Color;
import de.bluecolored.bluemap.core.world.block.BlockNeighborhood;
import io.github.janguenter.bluemap.laserio.activation.AddonRuntime;

/** Installed shell replay plus the stable persisted LaserIO beam subset. */
final class LaserIoRenderer implements BlockRenderer {

    private static final String ADVANCED = "laserio:laser_connector_advanced";
    private static final LaserBeamGeometry.Point CENTER =
            new LaserBeamGeometry.Point(0.5F, 0.5F, 0.5F);

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;
    private final ResourceModelRenderer stock;
    private final InstalledJsonModelEmitter shells;
    private final LaserBeamEmitter beams;
    private final LaserIoSnapshotDecoder decoder = new LaserIoSnapshotDecoder();

    LaserIoRenderer(
            ResourcePack resourcePack,
            TextureGallery textureGallery,
            RenderSettings renderSettings,
            AddonRuntime runtime
    ) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
        this.stock = new ResourceModelRenderer(resourcePack, textureGallery, renderSettings);
        this.shells = new InstalledJsonModelEmitter(resourcePack, textureGallery);
        this.beams = new LaserBeamEmitter(resourcePack, textureGallery);
    }

    @Override
    public void render(
            BlockNeighborhood block,
            Variant original,
            TileModelView target,
            Color mapColor
    ) {
        if (!runtime.active()) {
            renderStock(block, target, mapColor);
            return;
        }

        if (!(block.getBlockEntity() instanceof LaserIoBlockEntityData data)) {
            renderStock(block, target, mapColor);
            return;
        }

        int start = target.getStart();
        Color initialMapColor = new Color().set(mapColor);
        LaserIoSnapshot snapshot;
        try {
            snapshot = decoder.decode(data);
            if (!renderShell(block, target, snapshot.argb(), mapColor)) {
                resetAndRenderStock(block, target, start, mapColor, initialMapColor);
                return;
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            resetAndRenderStock(block, target, start, mapColor, initialMapColor);
            return;
        }

        appendOrdinaryBeams(snapshot, target);
        if (ADVANCED.equals(block.getBlockState().getId().getFormatted())
                && snapshot.advancedPartner()) {
            appendAdvancedPort(block, snapshot.argb(), target);
        }
    }

    private boolean renderShell(
            BlockNeighborhood block,
            TileModelView target,
            int argb,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (state == null) {
            return false;
        }
        boolean[] result = {false, true};
        state.forEach(
                block.getBlockState(),
                block.getX(),
                block.getY(),
                block.getZ(),
                variant -> {
                    boolean emitted = shells.emit(variant, block, target, argb, mapColor);
                    result[0] |= emitted;
                    result[1] &= emitted;
                }
        );
        return result[0] && result[1];
    }

    private void appendOrdinaryBeams(LaserIoSnapshot snapshot, TileModelView target) {
        for (LaserIoSnapshot.Offset offset : snapshot.renderedConnections()) {
            LaserBeamGeometry.Point end = new LaserBeamGeometry.Point(
                    offset.x() + 0.5F,
                    offset.y() + 0.5F,
                    offset.z() + 0.5F
            );
            appendBeam(CENTER, end, snapshot.argb(), target);
        }
    }

    private void appendAdvancedPort(
            BlockNeighborhood block,
            int argb,
            TileModelView target
    ) {
        LaserBeamGeometry.Point end = portEnd(
                block.getBlockState().getProperties().get("facing")
        );
        if (end != null) {
            appendBeam(CENTER, end, argb, target);
        }
    }

    private void appendBeam(
            LaserBeamGeometry.Point start,
            LaserBeamGeometry.Point end,
            int argb,
            TileModelView target
    ) {
        int overlayStart = target.getTileModel().size();
        try {
            if (!beams.emit(start, end, argb, target)) {
                reset(target, overlayStart);
            }
        } catch (MaxCapacityReachedException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            reset(target, overlayStart);
        }
    }

    private static LaserBeamGeometry.Point portEnd(String facing) {
        return switch (facing == null ? "" : facing) {
            case "up" -> new LaserBeamGeometry.Point(0.5F, 0.25F, 0.5F);
            case "down" -> new LaserBeamGeometry.Point(0.5F, 0.75F, 0.5F);
            case "north" -> new LaserBeamGeometry.Point(0.5F, 0.5F, 0.75F);
            case "south" -> new LaserBeamGeometry.Point(0.5F, 0.5F, 0.25F);
            case "east" -> new LaserBeamGeometry.Point(0.25F, 0.5F, 0.5F);
            case "west" -> new LaserBeamGeometry.Point(0.75F, 0.5F, 0.5F);
            default -> null;
        };
    }

    private void resetAndRenderStock(
            BlockNeighborhood block,
            TileModelView target,
            int start,
            Color mapColor,
            Color initialMapColor
    ) {
        reset(target, start);
        mapColor.set(initialMapColor);
        renderStock(block, target, mapColor);
    }

    private static void reset(TileModelView target, int start) {
        target.getTileModel().reset(start);
        target.initialize(start);
    }

    private void renderStock(
            BlockNeighborhood block,
            TileModelView target,
            Color mapColor
    ) {
        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState state =
                resourcePack.getBlockStates().get(block.getBlockState().getId());
        if (state == null) {
            return;
        }
        state.forEach(
                block.getBlockState(),
                block.getX(),
                block.getY(),
                block.getZ(),
                variant -> stock.render(block, variant, target, mapColor)
        );
    }
}
