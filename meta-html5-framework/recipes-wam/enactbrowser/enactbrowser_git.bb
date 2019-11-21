# Copyright (c) 2018-2019 LG Electronics, Inc.

SUMMARY = "Enact Based Web Browser"
AUTHOR = "Mikyung Kim <mikyung27.kim@lge.com>"
LICENSE = "LicenseRef-EnactBrowser-Evaluation"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6e00eb832d81f89a0f47fac10db717c7"

DEPENDS += "af-binder af-main-native chromium68 ilib-webapp"
RDEPENDS_{PN} += "enact-framework"

inherit agl_enactjs_app

PR = "r0"

AGL_ENACTJS_SHRINKWRAP_OVERRIDE = "false"
AGL_ENACTJS_PACK_OPTS = "--isomorphic --production --snapshot"
AGL_ENACTJS_ILIB_OVERRIDE = ""
AGL_PREFERRED_GFX_IMAGE_FORMAT_ENABLED="0"

SRC_URI = " \
    git://github.com/webosose/com.webos.app.enactbrowser.git;branch=master;protocol=https \
"
S = "${WORKDIR}/git/"
SRCREV = "cb6736250d63b4cd91ac3ac5827a8017e6271b8d"

AGL_ENACTJS_PROJECT_PATH = "./samples/enact-based"
AGL_ENACTJS_PACK_OVERRIDE = "\
    ${ENACT_DEV} pack ${AGL_ENACTJS_PACK_OPTS} && \
    ${ENACT_NODE} resbundler.js dist && \
    rm -fr ./dist/resources && \
    rm -fr ./dist/node_modules/@enact/moonstone/resources && \
    cp -f webos-locale.js dist/webos-locale.js && \
    cp -fr ${RECIPE_SYSROOT}/usr/share/javascript/ilib/localedata/ ./dist/ilibdata && \
    cp -f label.js dist/ && \
    cp -f background.js dist/ && \
    cp -f defaults.js dist/ && \
    cp -f manifest.json dist/ && \
    ${ENACT_NODE} extract-inline.js ./dist \
"

do_compile_prepend() {
    # Portion of do_compile_prepend of webos_enactjs_env.bbclass,
    # since this prepend occurs before that and we need NPM usage
    export HOME=${WORKDIR}
    ${ENACT_NPM} set cache ${TMPDIR}/npm_cache
    ${ENACT_NPM} config set prefer-offline true
    ${ENACT_NPM} install
    ${ENACT_NODE} ./scripts/cli.js transpile
}

FILES_${PN} += "/opt"