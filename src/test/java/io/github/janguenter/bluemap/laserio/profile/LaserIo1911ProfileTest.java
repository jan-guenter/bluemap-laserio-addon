/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.profile;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LaserIo1911ProfileTest {

    @Test
    void exactProfileOwnsOnlyThreeHostsAndThreeBlockEntities() {
        assertEquals(3, LaserIo1911Profile.ROUTED_BLOCKS.size());
        assertEquals(3, LaserIo1911Profile.BLOCK_ENTITY_IDS.size());
        assertEquals(3, LaserIo1911Profile.REQUIRED_MODELS.size());
        assertEquals(4, LaserIo1911Profile.REQUIRED_TEXTURES.size());
        assertEquals(
                "03e8537d75bc2f4ced2fc214d3409753e684d1056ee63b26db7a2b9e199ef4df",
                LaserIo1911Profile.ARTIFACTS.getFirst().sha256()
        );
    }
}
