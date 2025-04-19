package org.koishi.launcher.h2co3library.skin.cube.body;

import org.koishi.launcher.h2co3library.skin.cube.MainCube;

public class Body extends MainCube {

    protected float[] bodyTexCoordinates;
    
    public Body(float scale) {
        super(8.0f * scale, 12.0f * scale, 4.0f * scale, 0.0f * scale, 2.0f * scale, 0.0f * scale);
        this.bodyTexCoordinates = new float[] {
                0.3125f, 0.5f, 0.3125f, 0.3125f, 0.4375f, 0.3125f, 0.4375f, 0.5f,
                0.3125f, 0.3125f, 0.3125f, 0.25f, 0.4375f, 0.25f, 0.4375f, 0.3125f,
                0.4375f, 0.3125f, 0.4375f, 0.25f, 0.5625f, 0.25f, 0.5625f, 0.3125f,
                0.4375f, 0.5f, 0.4375f, 0.3125f, 0.5f, 0.3125f, 0.5f, 0.5f,
                0.25f, 0.5f, 0.25f, 0.3125f, 0.3125f, 0.3125f, 0.3125f, 0.5f,
                0.5f, 0.5f, 0.5f, 0.3125f, 0.625f, 0.3125f, 0.625f, 0.5f
        };
        addTextures(this.bodyTexCoordinates);
    }
}
