/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import de.bluecolored.bluemap.core.map.hires.block.BlockRendererType;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.Variant;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.mca.blockentity.BlockEntityType;
import io.github.janguenter.bluemap.laserio.activation.AddonRuntime;
import io.github.janguenter.bluemap.laserio.profile.LaserIo1911Profile;

import java.util.ArrayList;
import java.util.List;

/** BlueMap 5.22 registration boundary. Family renderer registrations go here. */
public final class BlueMap522Adapter {

    private static final AddonRuntime RUNTIME = AddonRuntime.INSTANCE;
    private static final Key RENDERER_KEY = Key.parse("bluemap_laserio:laserio_shape");
    private static final BlockRendererType RENDERER = new BlockRendererType.Impl(
            RENDERER_KEY,
            (pack, gallery, settings) -> new LaserIoRenderer(
                    pack, gallery, settings, RUNTIME
            )
    );
    private static final ResourcePack.Extension<ProfileResourceExtension> EXTENSION =
            new ProfileResourceExtensionType(RUNTIME);

    private BlueMap522Adapter() {
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

    static boolean isExpectedDispatch(Variant variant) {
        return variant != null
                && variant.getRenderer() == RENDERER
                && ResourcePack.MISSING_BLOCK_MODEL.equals(variant.getModel())
                && !variant.isTransformed()
                && !variant.isUvlock()
                && Double.compare(variant.getWeight(), 1D) == 0;
    }
}
