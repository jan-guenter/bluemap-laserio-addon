/*
 * SPDX-License-Identifier: MIT
 */

package io.github.janguenter.bluemap.laserio.adapter.bluemap523;

import de.bluecolored.bluemap.core.map.TextureGallery;
import de.bluecolored.bluemap.core.map.hires.TileModel;
import de.bluecolored.bluemap.core.map.hires.TileModelView;
import de.bluecolored.bluemap.core.resources.pack.resourcepack.ResourcePack;
import de.bluecolored.bluemap.core.util.Key;

import java.util.List;

/** Emits deterministic full-bright crossed ribbons using LaserIO's installed texture. */
final class LaserBeamEmitter {

    static final Key LASER_TEXTURE = Key.parse("laserio:misc/laser");

    private final ResourcePack resourcePack;
    private final TextureGallery textures;

    LaserBeamEmitter(ResourcePack resourcePack, TextureGallery textures) {
        this.resourcePack = resourcePack;
        this.textures = textures;
    }

    boolean emit(
            LaserBeamGeometry.Point start,
            LaserBeamGeometry.Point end,
            int argb,
            TileModelView target
    ) {
        if (resourcePack.getTextures().get(LASER_TEXTURE) == null) {
            return false;
        }
        List<LaserBeamGeometry.Triangle> triangles = LaserBeamGeometry.between(start, end);
        if (triangles.isEmpty()) {
            return false;
        }
        int first = target.add(triangles.size());
        TileModel mesh = target.getTileModel();
        int material = textures.get(LASER_TEXTURE);
        float red = ((argb >>> 16) & 0xFF) / 255F;
        float green = ((argb >>> 8) & 0xFF) / 255F;
        float blue = (argb & 0xFF) / 255F;
        for (int index = 0; index < triangles.size(); index++) {
            int triangleIndex = first + index;
            LaserBeamGeometry.Triangle triangle = triangles.get(index);
            setVertexData(mesh, triangleIndex, triangle);
            mesh.setMaterialIndex(triangleIndex, material);
            mesh.setColor(triangleIndex, red, green, blue);
            mesh.setAOs(triangleIndex, 1F, 1F, 1F);
            mesh.setSunlight(triangleIndex, 15);
            mesh.setBlocklight(triangleIndex, 15);
        }
        return true;
    }

    private static void setVertexData(
            TileModel mesh,
            int index,
            LaserBeamGeometry.Triangle triangle
    ) {
        LaserBeamGeometry.Vertex first = triangle.first();
        LaserBeamGeometry.Vertex second = triangle.second();
        LaserBeamGeometry.Vertex third = triangle.third();
        mesh.setPositions(
                index,
                first.point().x(), first.point().y(), first.point().z(),
                second.point().x(), second.point().y(), second.point().z(),
                third.point().x(), third.point().y(), third.point().z()
        );
        mesh.setUvs(
                index,
                first.u(), first.v(),
                second.u(), second.v(),
                third.u(), third.v()
        );
    }
}
