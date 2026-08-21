/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap522;

import de.bluecolored.bluemap.core.world.mca.MCAUtil;
import de.bluecolored.bluenbt.BlueNBT;
import de.bluecolored.bluenbt.NBTWriter;
import de.bluecolored.bluenbt.TagType;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LaserIoBlockEntityDataTest {

    private final LaserIoSnapshotDecoder decoder = new LaserIoSnapshotDecoder();

    @Test
    void exactNestedFieldsDecodeToDeterministicStableSnapshot() throws IOException {
        LaserIoBlockEntityData data = read(writer -> {
            writer.name("laserColor").value(0x7F33_66CC);
            writer.name("renderedConnections").beginList(8, TagType.COMPOUND);
            connection(writer, 3, 0, 0);
            connection(writer, -2, 1, 0);
            connection(writer, 3, 0, 0);
            connection(writer, 7, 0, 0);
            connection(writer, 8, 0, 0);
            connection(writer, Integer.MIN_VALUE, Integer.MIN_VALUE, 0);
            connection(writer, Integer.MAX_VALUE, 0, 0);
            writer.beginCompound();
            writer.name("pos").beginCompound();
            writer.name("X").value(1);
            writer.name("Y").value(2);
            writer.endCompound();
            writer.endCompound();
            writer.endList();
            writer.name("partnerDimPos").beginCompound();
            writer.name("dimension").value("minecraft:the_nether");
            writer.name("blockpos").beginCompound();
            position(writer, 100, 64, -20);
            writer.endCompound();
            writer.endCompound();

            writer.name("connections").beginList(1, TagType.COMPOUND);
            connection(writer, 1, 0, 0);
            writer.endList();
            writer.name("wrenchAlpha").value(255);
            writer.name("showParticles").value((byte) 1);
        });

        LaserIoSnapshot snapshot = decoder.decode(data);
        assertEquals(0x7F33_66CC, snapshot.argb());
        assertEquals(List.of(
                new LaserIoSnapshot.Offset(-2, 1, 0),
                new LaserIoSnapshot.Offset(3, 0, 0),
                new LaserIoSnapshot.Offset(7, 0, 0)
        ), snapshot.renderedConnections());
        assertTrue(snapshot.advancedPartner());
    }

    @Test
    void missingVisualFieldsUseExactDefaultAndNoOverlays() throws IOException {
        LaserIoSnapshot snapshot = decoder.decode(read(writer -> {
            writer.name("partnerDimPos").beginCompound();
            writer.name("dimension").value("not a dimension");
            writer.name("blockpos").beginCompound();
            position(writer, 0, 0, 0);
            writer.endCompound();
            writer.endCompound();
        }));

        assertEquals(LaserIoSnapshotDecoder.DEFAULT_COLOR, snapshot.argb());
        assertTrue(snapshot.renderedConnections().isEmpty());
        assertFalse(snapshot.advancedPartner());
    }

    @Test
    void missingOrWrongBlockEntityCannotBecomeAColoredShellSnapshot() {
        assertThrows(NullPointerException.class, () -> decoder.decode(null));
    }

    private static LaserIoBlockEntityData read(WriterAction body) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (NBTWriter writer = new NBTWriter(bytes)) {
            writer.beginCompound();
            writer.name("id").value("laserio:lasernode");
            writer.name("x").value(10);
            writer.name("y").value(100);
            writer.name("z").value(10);
            body.write(writer);
            writer.endCompound();
        }
        return MCAUtil.addCommonNbtSettings(new BlueNBT()).read(
                new ByteArrayInputStream(bytes.toByteArray()),
                LaserIoBlockEntityData.class
        );
    }

    private static void connection(NBTWriter writer, int x, int y, int z)
            throws IOException {
        writer.beginCompound();
        connectionBody(writer, x, y, z);
        writer.endCompound();
    }

    private static void connectionBody(NBTWriter writer, int x, int y, int z)
            throws IOException {
        writer.name("pos").beginCompound();
        position(writer, x, y, z);
        writer.endCompound();
    }

    private static void position(NBTWriter writer, int x, int y, int z) throws IOException {
        writer.name("X").value(x);
        writer.name("Y").value(y);
        writer.name("Z").value(z);
    }

    @FunctionalInterface
    private interface WriterAction {
        void write(NBTWriter writer) throws IOException;
    }
}
