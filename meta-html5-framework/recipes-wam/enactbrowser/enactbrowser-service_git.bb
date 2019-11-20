SUMMARY = "Enact browser widget"
DESCRIPTION = "Wgt packaging for running enact browser"
HOMEPAGE = "https://webosose.org"
SECTION = "apps"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "gitsm://auto-gitlab.lgsvl.net/jose.dapena/${PN}.git;branch=flounder;protocol=https"
SRCREV = "97989b650abf8d93d7ff62dfbd3549cd4e008a39"

PV = "1.0+git${SRCPV}"
S = "${WORKDIR}/git"

#build-time dependencies
DEPENDS += "af-binder af-main-native chromium68"

inherit cmake aglwgt

RDEPENDS_${PN} += "app-shell runxdg enactbrowser"
