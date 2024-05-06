package org.koishi.launcher.h2co3.core.download;

import static org.koishi.launcher.h2co3.core.utils.Lang.mapOf;
import static org.koishi.launcher.h2co3.core.utils.Pair.pair;

import android.content.Context;

import org.koishi.launcher.h2co3.core.H2CO3Settings;
import org.koishi.launcher.h2co3.core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3.core.utils.AndroidUtils;
import org.koishi.launcher.h2co3.core.utils.StringUtils;
import org.koishi.launcher.h2co3.core.utils.io.ArtifactMalformedException;
import org.koishi.launcher.h2co3.core.utils.io.DownloadException;
import org.koishi.launcher.h2co3.core.utils.io.ResponseCodeException;
import org.koishi.launcher.h2co3.core.utils.task.FetchTask;

import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.SSLHandshakeException;

public final class DownloadProviders {
    public DownloadProviders() {
        init();
    }

    public static final Map<String, DownloadProvider> providersById;
    public static final Map<String, DownloadProvider> rawProviders;
    public static final String DEFAULT_PROVIDER_ID = "balanced";
    public static final String DEFAULT_RAW_PROVIDER_ID = "bmclapi";
    private static final AdaptedDownloadProvider fileDownloadProvider = new AdaptedDownloadProvider();
    private static final MojangDownloadProvider MOJANG;
    private static final BMCLAPIDownloadProvider BMCLAPI;
    private static DownloadProvider currentDownloadProvider;

    static {
        String bmclapiRoot = "https://bmclapi2.bangbang93.com";

        MOJANG = new MojangDownloadProvider();
        BMCLAPI = new BMCLAPIDownloadProvider(bmclapiRoot);
        rawProviders = mapOf(
                pair("mojang", MOJANG),
                pair("bmclapi", BMCLAPI)
        );

        AdaptedDownloadProvider fileProvider = new AdaptedDownloadProvider();
        fileProvider.setDownloadProviderCandidates(Arrays.asList(BMCLAPI, MOJANG));
        BalancedDownloadProvider balanced = new BalancedDownloadProvider(MOJANG, BMCLAPI);

        providersById = mapOf(
                pair("official", new AutoDownloadProvider(MOJANG, fileProvider)),
                pair("balanced", new AutoDownloadProvider(balanced, fileProvider)),
                pair("mirror", new AutoDownloadProvider(BMCLAPI, fileProvider)));

        FetchTask.setDownloadExecutorConcurrency(64);
    }

    static void init() {
        if (!providersById.containsKey(H2CO3Settings.getDownloadSource())) {
            H2CO3Settings.setDownloadSource(DEFAULT_PROVIDER_ID);
            return;
        }

        currentDownloadProvider = Optional.ofNullable(providersById.get(H2CO3Settings.getDownloadSource()))
                .orElse(providersById.get(DEFAULT_PROVIDER_ID));

        if (!rawProviders.containsKey(H2CO3Settings.getDownloadType())) {
            H2CO3Settings.setDownloadType(DEFAULT_RAW_PROVIDER_ID);
            return;
        }

        DownloadProvider primary = Optional.ofNullable(rawProviders.get(H2CO3Settings.getDownloadType()))
                .orElse(rawProviders.get(DEFAULT_RAW_PROVIDER_ID));
        fileDownloadProvider.setDownloadProviderCandidates(
                Stream.concat(
                        Stream.of(primary),
                        rawProviders.values().stream().filter(x -> x != primary)
                ).collect(Collectors.toList())
        );
    }

    private static boolean isAutoChooseDownloadType() {
        return true;
    }

    public String getPrimaryDownloadProviderId() {
        return new SimpleStringProperty(DownloadProviders.DEFAULT_RAW_PROVIDER_ID).get();
    }

    public DownloadProvider getDownloadProviderByPrimaryId(String primaryId) {
        return Optional.ofNullable(providersById.get(primaryId))
                .orElse(providersById.get(DEFAULT_PROVIDER_ID));
    }

    /**
     * Get current primary preferred download provider
     */
    public DownloadProvider getDownloadProvider() {
        return isAutoChooseDownloadType() ? currentDownloadProvider : fileDownloadProvider;
    }

    public String localizeErrorMessage(Context context, Throwable exception) {
        if (exception instanceof DownloadException) {
            URL url = ((DownloadException) exception).getUrl();
            if (exception.getCause() instanceof SocketTimeoutException) {
                return AndroidUtils.getLocalizedText(context, "install_failed_downloading_timeout", url);
            } else if (exception.getCause() instanceof ResponseCodeException responseCodeException) {
                if (AndroidUtils.hasStringId(context, "download_code_" + responseCodeException.getResponseCode())) {
                    return AndroidUtils.getLocalizedText(context, "download_code_" + responseCodeException.getResponseCode(), url);
                } else {
                    return AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + StringUtils.getStackTrace(exception.getCause());
                }
            } else if (exception.getCause() instanceof FileNotFoundException) {
                return AndroidUtils.getLocalizedText(context, "download_code_404", url);
            } else if (exception.getCause() instanceof AccessDeniedException) {
                return AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + AndroidUtils.getLocalizedText(context, "exception_access_denied", ((AccessDeniedException) exception.getCause()).getFile());
            } else if (exception.getCause() instanceof ArtifactMalformedException) {
                return AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + "exception_artifact_malformed";
            } else if (exception.getCause() instanceof SSLHandshakeException) {
                return AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + "exception_ssl_handshake";
            } else {
                return AndroidUtils.getLocalizedText(context, "install_failed_downloading_detail", url) + "\n" + StringUtils.getStackTrace(Objects.requireNonNull(exception.getCause()));
            }
        } else if (exception instanceof ArtifactMalformedException) {
            return "exception_artifact_malformed";
        } else if (exception instanceof CancellationException) {
            return "message_cancelled";
        }
        return StringUtils.getStackTrace(exception);
    }
}