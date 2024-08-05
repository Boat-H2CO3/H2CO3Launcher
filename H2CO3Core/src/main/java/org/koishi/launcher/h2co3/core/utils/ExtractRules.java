package org.koishi.launcher.h2co3.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ExtractRules {

    public static final ExtractRules EMPTY = new ExtractRules();

    private final List<String> exclude;

    public ExtractRules() {
        this.exclude = Collections.emptyList();
    }

    public ExtractRules(List<String> exclude) {
        this.exclude = new ArrayList<>(exclude);
    }

    public List<String> getExclude() {
        return Collections.unmodifiableList(exclude);
    }

    public boolean shouldExtract(String path) {
        return exclude.stream().noneMatch(path::startsWith);
    }

}