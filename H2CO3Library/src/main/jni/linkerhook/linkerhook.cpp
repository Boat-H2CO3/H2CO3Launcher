//
// Created by Vera-Firefly on 17.01.2025.
//

#include <android/dlext.h>
#include <cstring>
#include <cstdio>
#include <atomic>
#include <array>
#include "linkerhook.h"

static void* (*dlopen_ext_impl)(const char* filename, int flags, const android_dlextinfo* extinfo, const void* caller_addr) = nullptr;
static struct android_namespace_t* (*get_exported_namespace_impl)(const char* name) = nullptr;

static void* ready_handle = nullptr;
static std::atomic<void*> global_ready_handle{nullptr};

static const std::array<const char*, 3> supported_namespaces = {"sphal", "vendor", "default"};

void set_handles(void* handle, void* dlopen_ext, void* get_namespace) {
    ready_handle = handle;
    global_ready_handle.store(handle);
    dlopen_ext_impl = reinterpret_cast<decltype(dlopen_ext_impl)>(dlopen_ext);
    get_exported_namespace_impl = reinterpret_cast<decltype(get_exported_namespace_impl)>(get_namespace);
}

static void* checkIfGlobalReadyHandle() {
    void* handle = global_ready_handle.load();
    if (handle == nullptr) {
        fprintf(stderr, "Global ready handle is null, falling back to ready_handle.\n");
        return ready_handle;
    }
    return handle;
}

void* dlopen_ext(const char* filename, int flags, const android_dlextinfo* extinfo) {
    if (strstr(filename, "vulkan.")) {
        return checkIfGlobalReadyHandle();
    }

    if (dlopen_ext_impl) {
        return dlopen_ext_impl(filename, flags, extinfo, reinterpret_cast<const void*>(&dlopen_ext));
    }
    return nullptr; // Handle the case where dlopen_ext_impl is not set
}

void* load_sphal_library(const char* filename, int flags) {
    if (strstr(filename, "vulkan.")) {
        return checkIfGlobalReadyHandle();
    }

    struct android_namespace_t* androidNamespace = nullptr;
    for (const auto& namespace_name : supported_namespaces) {
        androidNamespace = get_exported_namespace_impl(namespace_name);
        if (androidNamespace != nullptr) break;
    }

    android_dlextinfo extinfo = {
            .flags = ANDROID_DLEXT_USE_NAMESPACE,
            .library_namespace = androidNamespace
    };

    if (dlopen_ext_impl) {
        return dlopen_ext_impl(filename, flags, &extinfo, reinterpret_cast<const void*>(&dlopen_ext));
    }
    return nullptr; // Handle the case where dlopen_ext_impl is not set
}

uint64_t hook_atrace_get_enabled_tags() {
    return 0;
}