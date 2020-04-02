SUMMARY = "CEF/Ozone"
AUTHOR = "Alexander Dunaev <adunaev@igalia.com>"

require chromium-gn.inc

SRC_URI += " \
    git://github.com/Igalia/cef.git;protocol=https;destsuffix=${S}/cef;branch=${BRANCH_cef};rev=${SRCREV_cef};name=cef \
    file://0001-ozone-wayland-do-not-use-modifiers-for-linear-buffer.patch \
"

BRANCH_cef = "adunaev@4044"
SRCREV_cef = "36e47ffd89cf3de2f542eef3f823ec85ea4060b0"

REQUIRED_DISTRO_FEATURES = "wayland"

DEPENDS += "\
        libxkbcommon \
        virtual/egl \
        wayland \
        wayland-native \
"

GN_ARGS += "\
        ${PACKAGECONFIG_CONFARGS} \
        use_ozone=true \
        ozone_auto_platforms=false \
        ozone_platform_headless=true \
        ozone_platform_wayland=true \
        ozone_platform_x11=false \
        system_wayland_scanner_path="${STAGING_BINDIR_NATIVE}/wayland-scanner" \
        use_xkbcommon=true \
        use_system_libwayland=true \
        use_system_minigbm=true \
        use_system_libdrm=true \
        use_gtk=false \
        cef_use_gtk=false \
        enable_service_discovery=false \
"

CHROME_TARGETS = "libcef"

do_patch_append() {
    import subprocess
    print("Patching projects with CEF")
    os.chdir(os.path.join(d.getVar('S'), 'cef'))
    subprocess.check_call([sys.executable, '{S}/cef/tools/apply_cef_patches.py'.format(S=d.getVar('S'))])
}

do_configure_append() {
    echo "Generating CEF config header"
    cd ${S}/cef
    python ${S}/cef/tools/make_config_header.py --header include/cef_config.h --cef_gn_config ${B}/args.gn
}

do_install() {
    install -m 0755 -d ${D}${libdir}
    install libcef.so ${D}${libdir}

    # Prepare headers.
    export INCLUDES_DIR="${D}${includedir}/libcef/include"
    install -d ${INCLUDES_DIR}
    cp -r ${S}/cef/include/* ${INCLUDES_DIR}
    # The base/internal/cef_net_error_list.h has to be replaced with the chromium's net/base/net_error_list.h.
    rm ${INCLUDES_DIR}/base/internal/cef_net_error_list.h
    cp ${S}/net/base/net_error_list.h ${INCLUDES_DIR}/base/internal/cef_net_error_list.h

    install -d ${D}${libdir}/libcef
    install -m 0644 *.pak ${D}${libdir}/libcef/

    install -d ${D}${libdir}/libcef/locales
    install -m 0644 locales/*.pak ${D}${libdir}/libcef/locales/
}

# Package unversioned library.
SOLIBS = ".so"
FILES_SOLIBSDEV = ""
FILES_${PN} = " \
    ${libdir}/libcef.so \
    ${libdir}/libcef/* \
"
