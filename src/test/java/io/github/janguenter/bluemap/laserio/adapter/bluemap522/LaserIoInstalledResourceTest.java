/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import de.bluecolored.bluemap.core.resources.adapter.ResourcesGson;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Element;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Face;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.model.Model;
import de.bluecolored.bluemap.core.util.Key;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class LaserIoInstalledResourceTest {

    @Test
    void exactInstalledModelsExposeExpectedTintAndSkippableMissingFaces()
            throws IOException {
        String artifact = System.getProperty("laserIoJar");
        assumeTrue(artifact != null, "exact LaserIO artifact is a prototype-only input");
        try (ZipFile zip = new ZipFile(Path.of(artifact).toFile())) {
            assertModel(zip, "laser_node", 25, 6, false);
            assertModel(zip, "laser_connector", 18, 11, true);
            assertModel(zip, "laser_connector_advanced", 18, 11, true);
            assertNotNull(zip.getEntry("assets/laserio/textures/misc/laser.png"));
        }
    }

    private static void assertModel(
            ZipFile zip,
            String name,
            int elementCount,
            int tintedFaceCount,
            boolean expectUnresolved
    ) throws IOException {
        String path = "assets/laserio/models/block/" + name + ".json";
        ZipEntry entry = zip.getEntry(path);
        assertNotNull(entry, path);
        Model model;
        try (InputStreamReader reader = new InputStreamReader(
                zip.getInputStream(entry), StandardCharsets.UTF_8
        )) {
            model = ResourcesGson.INSTANCE.fromJson(reader, Model.class);
        }
        assertEquals(elementCount, model.getElements().length, path);
        int tinted = 0;
        int unresolved = 0;
        int resolved = 0;
        for (Element element : model.getElements()) {
            for (Map.Entry<de.bluecolored.bluemap.core.util.Direction, Face> face :
                    element.getFaces().entrySet()) {
                if (face.getValue().getTintindex() == 1) {
                    tinted++;
                }
                if (face.getValue().getTexture().getTexturePath(
                        model.getTextures()::get
                ) == null) {
                    unresolved++;
                } else {
                    resolved++;
                }
            }
        }
        assertEquals(tintedFaceCount, tinted, path);
        assertTrue(resolved > 0, path);
        assertEquals(expectUnresolved, unresolved > 0, path);
        assertTrue(
                model.getTextures().containsKey("0"),
                Key.parse("laserio:block/" + name).getFormatted()
        );
    }
}
