package org.koishi.launcher.h2co3library.skin.cube.head;

import org.koishi.launcher.h2co3library.skin.cube.MainCube;

public class Hat extends MainCube {

    protected float[] hatTexCoordinates;
    
    public Hat(float scale) {
        super(9.0f * scale, 9.0f * scale, 9.0f * scale, 0.0f * scale, 12.0f * scale, 0.0f * scale);
        this.hatTexCoordinates = new float[] {
                0.625f, 0.25f, 0.625f, 0.125f, 0.75f, 0.125f, 0.75f, 0.25f,
                0.625f, 0.125f, 0.625f, 0.0f, 0.75f, 0.0f, 0.75f, 0.125f,
                0.75f, 0.125f, 0.75f, 0.0f, 0.875f, 0.0f, 0.875f, 0.125f,
                0.75f, 0.25f, 0.75f, 0.125f, 0.875f, 0.125f, 0.875f, 0.25f,
                0.5f, 0.25f, 0.5f, 0.125f, 0.625f, 0.125f, 0.625f, 0.25f,
                0.875f, 0.25f, 0.875f, 0.125f, 1.0f, 0.125f, 1.0f, 0.25f
        };
        addTextures(this.hatTexCoordinates);
    }
}
