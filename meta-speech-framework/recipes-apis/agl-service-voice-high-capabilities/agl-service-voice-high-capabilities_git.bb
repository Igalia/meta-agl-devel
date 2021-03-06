SUMMARY = "agl-service-voice-high"
DESCRIPTION = "AGL High Level Voice service"
HOMEPAGE = "https://git.automotivelinux.org/apps/agl-service-voice-high-capabilities"
SECTION = "apps"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

inherit cmake pkgconfig aglwgt

DEPENDS += "lua lua-native"
RDEPENDS_${PN} += "lua"

SRC_URI = "gitsm://gerrit.automotivelinux.org/gerrit/apps/agl-service-voice-high-capabilities.git;protocol=https;branch=${AGL_BRANCH}"
SRCREV = "${AGL_APP_REVISION}"

PV = "0.1+git${SRCPV}"
S = "${WORKDIR}/git"

