SUMMARY = "WAM"
AUTHOR = "Jani Hautakangas <jani.hautakangas@lge.com>"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit cmake

DEPENDS = "glib-2.0 jsoncpp boost chromium72 wayland-ivi-extension libhomescreen libwindowmanager"

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${prefix} \
    -DPLATFORM_NAME=${@'${DISTRO}'.upper().replace('-', '_')} \
    -DCHROMIUM_SRC_DIR=${STAGING_INCDIR}/chromium72"

PR="r0"

PROVIDES += "virtual/webruntime"
RPROVIDES_${PN} += "virtual/webruntime"

SRC_URI = "\
    git://github.com/igalia/webosose-${PN}.git;branch=@6.agl.chromium72;protocol=https \
    file://WebAppMgr@.service \
    file://WebAppMgr.env \
    file://trunc-webapp-roles.patch \
"
S = "${WORKDIR}/git"

SRCREV = "2fbf59ca45b05912b90e6ce1f8ce1d6686a8ae9b"

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

FILES_${PN} += "${sysconfdir}/init ${sysconfdir}/wam ${libdir}/webappmanager/plugins/*.so ${systemd_system_unitdir}"

CXXFLAGS_append_agl-devel = " -DAGL_DEVEL"

do_install_append_agl-devel() {
    # Enable remote inspector and dev mode
    install -d ${D}${localstatedir}/agl-devel/preferences
    touch ${D}${localstatedir}/agl-devel/preferences/debug_system_apps
    touch ${D}${localstatedir}/agl-devel/preferences/devmode_enabled
}
