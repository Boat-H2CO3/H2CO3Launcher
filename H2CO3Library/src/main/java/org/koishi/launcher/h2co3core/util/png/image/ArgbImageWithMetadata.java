package org.koishi.launcher.h2co3core.util.png.image;

import org.koishi.launcher.h2co3core.util.png.PNGMetadata;

final class ArgbImageWithMetadata implements ArgbImage {
    private final ArgbImage source;
    private final PNGMetadata metadata;

    ArgbImageWithMetadata(ArgbImage source, PNGMetadata metadata) {
        this.source = source;
        this.metadata = metadata;
    }

    @Override
    public int getWidth() {
        return source.getWidth();
    }

    @Override
    public int getHeight() {
        return source.getHeight();
    }

    @Override
    public int getArgb(int x, int y) {
        return source.getArgb(x, y);
    }

    @Override
    public PNGMetadata getMetadata() {
        return metadata;
    }

    @Override
    public ArgbImage withMetadata(PNGMetadata metadata) {
        return new ArgbImageWithMetadata(source, metadata);
    }
}