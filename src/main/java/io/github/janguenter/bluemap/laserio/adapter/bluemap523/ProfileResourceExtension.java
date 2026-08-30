/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePackExtension;
import de.bluecolored.bluemap.core.util.Key;
import de.bluecolored.bluemap.core.world.BlockProperties;
import de.bluecolored.bluemap.core.world.BlockState;
import io.github.janguenter.bluemap.addon.adapter.api.bluemap523.SyntheticDispatch;
import io.github.janguenter.bluemap.addon.runtime.artifact.ExactArtifactDetector;
import io.github.janguenter.bluemap.laserio.activation.AddonRuntime;
import io.github.janguenter.bluemap.laserio.profile.LaserIo1911Profile;

import java.nio.file.Path;
import java.util.Set;

/** Exact-artifact admission, installed-resource validation and three-host routing. */
final class ProfileResourceExtension implements ResourcePackExtension {

    private static final Key SYNTHETIC = Key.parse("bluemap_laserio:laserio_shape");

    private final ResourcePack resourcePack;
    private final AddonRuntime runtime;

    ProfileResourceExtension(ResourcePack resourcePack, AddonRuntime runtime) {
        this.resourcePack = resourcePack;
        this.runtime = runtime;
    }

    @Override
    public void loadResources(Iterable<Path> roots) {
        if (Boolean.getBoolean("bluemap.laserio.disabled")) {
            runtime.inactive("operator-disabled");
            return;
        }
        if (!ExactArtifactDetector.matchesAll(roots, LaserIo1911Profile.ARTIFACTS)) {
            runtime.inactive("exact-artifact-missing-or-duplicate");
            return;
        }

        de.bluecolored.bluemap.core.resources.pack.resourcepack.blockstate.BlockState dispatch =
                resourcePack.getBlockStates().get(SYNTHETIC);
        if (!SyntheticDispatch.matches(dispatch, BlueMap523Adapter.renderer())) {
            runtime.inactive("synthetic-dispatch-invalid");
            return;
        }
        runtime.activate();
    }

    @Override
    public Set<Key> collectUsedTextureKeys() {
        return runtime.active() ? LaserIo1911Profile.REQUIRED_TEXTURES : Set.of();
    }

    @Override
    public void bake() {
        if (!runtime.active()) {
            return;
        }
        for (Key key : LaserIo1911Profile.REQUIRED_MODELS) {
            if (resourcePack.getModels().get(key) == null) {
                runtime.inactive("required-model-missing");
                return;
            }
        }
        for (Key key : LaserIo1911Profile.REQUIRED_TEXTURES) {
            if (resourcePack.getTextures().get(key) == null) {
                runtime.inactive("required-texture-missing");
                return;
            }
        }
        for (String id : LaserIo1911Profile.ROUTED_BLOCKS) {
            if (resourcePack.getBlockStates().get(Key.parse(id)) == null) {
                runtime.inactive("required-blockstate-missing");
                return;
            }
        }
    }

    @Override
    public Key getBlockStateKey(Key key) {
        return runtime.active() && LaserIo1911Profile.ROUTED_BLOCKS.contains(key.getFormatted())
                ? SYNTHETIC : key;
    }

    @Override
    public void getBlockProperties(BlockState blockState, BlockProperties.Builder builder) {
        if (runtime.active()
                && LaserIo1911Profile.ROUTED_BLOCKS.contains(
                        blockState.getId().getFormatted()
                )) {
            builder.culling(false).occluding(false).cullingIdentical(false);
        }
    }

}
