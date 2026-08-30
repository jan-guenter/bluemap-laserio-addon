/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.profile;

import de.bluecolored.bluemap.core.util.Key;
import io.github.janguenter.bluemap.addon.runtime.artifact.ArtifactPin;

import java.util.List;
import java.util.Set;

/** Exact All the Mons 1.2.0 profile `laserio-1.9.11`. */
public final class LaserIo1911Profile {

    public static final String PROFILE_ID = "laserio-1.9.11";
    public static final Set<String> ROUTED_BLOCKS = Set.of(
            "laserio:laser_node",
            "laserio:laser_connector",
            "laserio:laser_connector_advanced"
    );
    public static final Set<String> BLOCK_ENTITY_IDS = Set.of(
            "laserio:lasernode",
            "laserio:laserconnector",
            "laserio:laserconnectoradv"
    );
    public static final Set<Key> REQUIRED_MODELS = Set.of(
            Key.parse("laserio:block/laser_node"),
            Key.parse("laserio:block/laser_connector"),
            Key.parse("laserio:block/laser_connector_advanced")
    );
    public static final Set<Key> REQUIRED_TEXTURES = Set.of(
            Key.parse("laserio:block/laser_node"),
            Key.parse("laserio:block/laser_connector"),
            Key.parse("laserio:block/laser_connector_advanced"),
            Key.parse("laserio:misc/laser")
    );
    public static final List<ArtifactPin> ARTIFACTS = List.of(
            new ArtifactPin(
                    "laserio",
                    "laserio",
                    "1.9.11",
                    "laserio-1.9.11.jar",
                    1_305_285L,
                    "03e8537d75bc2f4ced2fc214d3409753e684d1056ee63b26db7a2b9e199ef4df"
            )
    );

    private LaserIo1911Profile() {
    }
}
