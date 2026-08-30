/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.RegistryGuard;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.ResourceExtensionType;
import io.github.janguenter.bluemap.laserio.activation.AddonRuntime;
import io.github.janguenter.bluemap.laserio.profile.LaserIo1911Profile;

import java.util.ArrayList;
import java.util.List;

/** Exact BlueMap 5.23 feature-backport registration boundary. */
public final class BlueMap523Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final Key RENDERER_KEY = Key.parse("bluemap_laserio:laserio_shape");
    private static final Key EXTENSION_KEY = Key.parse("bluemap_laserio:exact_profile");
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            RENDERER_KEY,
            (pack, gallery, settings) -> new LaserIoRenderer(
                    pack, gallery, settings, RUNTIME
            )
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ResourceExtensionType<>(
                    EXTENSION_KEY,
                    pack -> new ProfileResourceExtension(pack, RUNTIME)
            );

    private BlueMap523Adapter() {
    }

    /** Registers the exact-profile extension, renderer and three LaserIO block entities. */
    public static synchronized boolean install() {
        List<BlockEntityType> entities = new ArrayList<>();
        for (String id : LaserIo1911Profile.BLOCK_ENTITY_IDS) {
            entities.add(new BlockEntityType.Impl(
                    Key.parse(id), LaserIoBlockEntityData.class
            ));
        }

        if (!RegistryGuard.canRegister(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.canRegister(ResourcePack.Extension.REGISTRY, EXTENSION)
                || entities.stream().anyMatch(
                        entity -> !RegistryGuard.canRegister(BlockEntityType.REGISTRY, entity)
                )) {
            RUNTIME.fail("registry-collision");
            return false;
        }
        if (!RegistryGuard.register(BlockRendererType.REGISTRY, RENDERER)
                || !RegistryGuard.register(ResourcePack.Extension.REGISTRY, EXTENSION)) {
            RUNTIME.fail("registry-registration-failed");
            return false;
        }
        for (BlockEntityType entity : entities) {
            if (!RegistryGuard.register(BlockEntityType.REGISTRY, entity)) {
                RUNTIME.fail("block-entity-registry-collision");
                return false;
            }
        }
        return true;
    }

    static BlockRendererType renderer() {
        return RENDERER;
    }
}
