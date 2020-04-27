SUMMARY = "Chromium browser widget"
DESCRIPTION = "Wgt packaging for running chromium installed browser"
HOMEPAGE = "https://webosose.org"
SECTION = "apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "gitsm://github.com/webosose/${PN}.git;branch=halibut;protocol=https"
SRCREV = "7dabb46483ff6a3a4fa5cfe532b91412f185554d"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

#build-time dependencies
DEPENDS += "af-binder af-main-native chromium72"

inherit cmake aglwgt

RDEPENDS_${PN} += "chromium72-browser runxdg"
