package org.koishi.launcher.h2co3.core.utils.io;

import org.koishi.launcher.h2co3.core.utils.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;

public final class ChecksumMismatchException extends ArtifactMalformedException {

    private final String algorithm;
    private final String expectedChecksum;
    private final String actualChecksum;

    public ChecksumMismatchException(String algorithm, String expectedChecksum, String actualChecksum) {
        super("Incorrect checksum (" + algorithm + "), expected: " + expectedChecksum + ", actual: " + actualChecksum);
        this.algorithm = algorithm;
        this.expectedChecksum = expectedChecksum;
        this.actualChecksum = actualChecksum;
    }

    public static void verifyChecksum(Path file, String algorithm, String expectedChecksum) throws IOException {
        String checksum = DigestUtils.digestToString(algorithm, file);
        if (!checksum.equalsIgnoreCase(expectedChecksum)) {
            throw new ChecksumMismatchException(algorithm, expectedChecksum, checksum);
        }
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getExpectedChecksum() {
        return expectedChecksum;
    }

    public String getActualChecksum() {
        return actualChecksum;
    }
}