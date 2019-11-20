# Copyright (c) 2017-2019 LG Electronics, Inc.

SUMMARY = "iLib code and locale data"
AUTHOR = "Goun Lee <goun.lee@lge.com>"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PR = "r6"

inherit allarch

SRC_URI = "git://github.com/webosose/ilib-webapp.git;branch=master;protocol=https"
S = "${WORKDIR}/git"
SRCREV = "439c6f48b9eba8f83df3020fd6588d047b5a8e10"

# Skip the unwanted tasks
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # ${datadir}/javascript is the standard location where jquery puts its files, so
    # iLib follows suit. iLib is an externally-developed library like jquery.
    install -d ${D}${datadir}/javascript/ilib
    install -v -m 644 ${S}/package.json ${S}/index.js ${D}${datadir}/javascript/ilib
    cp -rv ${S}/js ${S}/lib ${S}/locale ${S}/localedata ${D}${datadir}/javascript/ilib
}

PACKAGES = "${PN}"
FILES_${PN} = "${datadir}/javascript/ilib"
