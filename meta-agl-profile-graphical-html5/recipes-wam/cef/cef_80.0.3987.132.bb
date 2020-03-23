# Copyright (c) 2020 Igalia S. L.

SUMMARY = "CEF"
AUTHOR = "Alexander Dunaev <adunaev@igalia.com>"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

# DEPENDS = "dbus glib-2.0 gtk+3 nss"

# SRC_URI = "\
#     git://chromium.googlesource.com/chromium/tools/depot_tools.git;protocol=https;destsuffix=git/depot_tools;rev=master \
#     git://chromium.googlesource.com/chromium/src.git;protocol=https;destsuffix=git/chromium;tag=${SRCTAG_chromium} \
#     git://chromium.googlesource.com/v8/v8.git;protocol=https;destsuffix=git/chromium/src/v8;rev=${SRCREV_v8} \
#     git://github.com/Igalia/cef.git;protocol=https;destsuffix=git/chromium/src/cef;branch=${BRANCH_cef};rev=${SRCREV_cef} \
# "

#     git://chromium.googlesource.com/chromium/src.git;protocol=https;destsuffix=${SOURCE_DIR};rev=${SRCREV_chromium} \
#     git://chromium.googlesource.com/v8/v8.git;protocol=https;destsuffix=${SOURCE_DIR}/v8;rev=${SRCREV_v8}


SRC_URI = "\
    https://commondatastorage.googleapis.com/chromium-browser-official/${CHROMIUM_NAME}.tar.xz;name=chromium \
    git://github.com/Igalia/cef.git;protocol=https;destsuffix=${SOURCE_DIR}/cef;branch=${BRANCH_cef};rev=${SRCREV_cef};name=cef \
    git://chromium.googlesource.com/chromium/tools/depot_tools.git;protocol=https;destsuffix=depot_tools;rev=master;name=depot_tools \
"

SRC_URI[chromium.md5sum] = "207403b796cd38eeec38af03a22b10c8"
SRC_URI[chromium.sha256sum] = "2c0012059046a5a7e2bf6e9502f1898f1953226d63b724b82fc18226e285c201"

BRANCH_cef = "adunaev@3987"
SRCREV_cef = "14acbb12936432bc3ee74c8c19b77f3edc7ab301"

FILESEXTRAPATHS_prepend = "${S}/cef/patch/patches:"

# SRCTAG_chromium = "80.0.3987.132"
# The git fetcher doesn't want to fetch by tag :-(
# SRCREV_chromium = "78e2bc9c2def358a0745878ffc66ce85ee5221d8"

# SRCREV_v8 = "2ad0a63d4a25377f3dc5eae52ef87505518867e8"

CHROMIUM_NAME = "chromium-${PV}"
SOURCE_DIR = "${CHROMIUM_NAME}"

OUT_DIR = "${WORKDIR}/build"
BUILD_TYPE = "Release"

# do_fetch[vardeps] += "SRCREV_cef SRCREV_v8 SRCTAG_chromium"

# clang_base_path = "/xdt/build/tmp/work/corei7-64-agl-linux/chromium72/git-r0/recipe-sysroot-native/usr/bin"
# clang_use_chrome_plugins = false
# gold_path = ""
# is_agl = true
# is_chrome_cbe = true
# use_cbe = true
# use_glib = true
# use_neva_media = false
# use_webos_gpu_info_collector = false

GN_ARGS = "\
    closure_compile = false\
    custom_toolchain = "//build/toolchain/yocto:yocto_target"\
    disable_ftp_support = true\
    enable_memorymanager_webapi = false\
    enable_nacl = false\
    enable_print_preview = false\
    enable_remoting = false\
    ffmpeg_branding = "Chrome"\
    host_os = "linux"\
    host_toolchain = "//build/toolchain/yocto:yocto_native"\
    is_cfi = false\
    is_clang = false\
    is_debug = false\
    is_official_build = true\
    jumbo_file_merge_limit = 8\
    linux_use_bundled_binutils = false\
    ozone_auto_platforms = false\
    ozone_platform_wayland = true\
    ozone_platform_wayland_external = false\
    proprietary_codecs = true\
    remove_webcore_debug_symbols = true\
    symbol_level = 0\
    target_cpu = "x64"\
    target_os = "linux"\
    treat_warnings_as_errors = false\
    use_cups = false\
    use_custom_libcxx = false\
    use_gnome_keyring = false\
    use_gold = false\
    use_jumbo_build = true\
    use_kerberos = false\
    use_lttng = false\
    use_ozone = true\
    use_pmlog = false\
    use_pulseaudio = false\
    use_sysroot = false\
    use_system_debugger_abort = true\
    use_system_libdrm = true\
    use_system_minigbm = true\
    use_xkbcommon = true\
    v8_snapshot_toolchain = "//build/toolchain/yocto:yocto_target"\
"

do_configure_append () {
    echo "Creating projects"
    export PATH="${WORKDIR}/depot_tools:$PATH"
    cd ${WORKDIR}/${SOURCE_DIR}/cef/
    ./cef_create_projects.sh
    # -I ${BUILD_TARGET_ARCH}
    cd -

    gn gen --args="${GN_ARGS}" "${OUT_DIR}/${BUILD_TYPE}"
}
#
# do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
# do_compile() {
#     echo "Compiling"
#     export PATH="${S}/depot_tools:$PATH"
#     cd ${S}/chromium_git/chromium/src
#     ninja -v -C ${OUT_DIR}/${BUILD_TYPE} cef
# }
