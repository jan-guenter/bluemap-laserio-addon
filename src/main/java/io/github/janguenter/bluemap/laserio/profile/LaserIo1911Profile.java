/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.profile;

import java.util.List;

/** Exact All the Mons 1.2.0 profile `laserio-1.9.11`. */
public final class LaserIo1911Profile {

    public static final String PROFILE_ID = "laserio-1.9.11";
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
