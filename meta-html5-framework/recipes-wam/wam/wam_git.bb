SUMMARY = "WAM"
AUTHOR = "Jani Hautakangas <jani.hautakangas@lge.com>"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit cmake

DEPENDS = "glib-2.0 jsoncpp boost chromium68 wayland-ivi-extension libhomescreen libwindowmanager"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${prefix} \
    -DPLATFORM_NAME=${@'${DISTRO}'.upper().replace('-', '_')} \
    -DCHROMIUM_SRC_DIR=${STAGING_INCDIR}/chromium68"

PR="r0"

PROVIDES += "virtual/webruntime"
RPROVIDES_${PN} += "virtual/webruntime"

SRC_URI = "\
    git://github.com/webosose/${PN}.git;branch=@6.agl.guppy;protocol=https \
    file://0001-agl-Hard-code-windowmanager-roles-for-certain-app-id.patch \
    file://0002-WIP-disable-tinyproxy-integration.patch \
    file://WebAppMgr@.service \
    file://WebAppMgr.env \
"
S = "${WORKDIR}/git"
SRCREV = "23427000917d2a00f1eaa175893773c0702d48b4"

do_install_append() {
    install -d ${D}${sysconfdir}/wam
    install -v -m 644 ${S}/files/launch/security_policy.conf ${D}${sysconfdir}/wam/security_policy.conf
    install -d ${D}${systemd_system_unitdir}
    install -v -m 644 ${WORKDIR}/WebAppMgr@.service ${D}${systemd_system_unitdir}/WebAppMgr@.service
    install -d ${D}${sysconfdir}/default/
    install -v -m 644 ${WORKDIR}/WebAppMgr.env ${D}${sysconfdir}/default/WebAppMgr.env
    ln -snf WebAppMgr ${D}${bindir}/web-runtime
    install -d ${D}${systemd_system_unitdir}/afm-user-session@.target.wants
    ln -sf ../WebAppMgr@.service ${D}${systemd_system_unitdir}/afm-user-session@.target.wants/
}

RDEPENDS_${PN} += "wam-tinyproxy"
FILES_${PN} += "${sysconfdir}/init ${sysconfdir}/wam ${libdir}/webappmanager/plugins/*.so ${systemd_system_unitdir}"

CXXFLAGS_append_agl-devel = " -DAGL_DEVEL"

do_install_append_agl-devel() {
    # Enable remote inspector and dev mode
    install -d ${D}${localstatedir}/agl-devel/preferences
    touch ${D}${localstatedir}/agl-devel/preferences/debug_system_apps
    touch ${D}${localstatedir}/agl-devel/preferences/devmode_enabled
}