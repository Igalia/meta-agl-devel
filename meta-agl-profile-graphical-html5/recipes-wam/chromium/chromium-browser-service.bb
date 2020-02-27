SUMMARY = "Chromium browser widget"
DESCRIPTION = "Wgt packaging for running chromium installed browser"
HOMEPAGE = "https://webosose.org"
SECTION = "apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "gitsm://github.com/igalia/${PN}.git;branch=adunaev@chromium72@icefish;protocol=https"
SRCREV = "9875561abfd7ef04a73ef2d60dc6178a01c69e94"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

#build-time dependencies
DEPENDS += "af-binder af-main-native chromium72"

inherit cmake aglwgt

RDEPENDS_${PN} += "chromium72-browser runxdg"
