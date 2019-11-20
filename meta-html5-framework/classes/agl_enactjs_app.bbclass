# Copyright (c) 2016-2019 LG Electronics, Inc.
#
# agl_enactjs_app
#
# This class is to be inherited by every recipe whose component is an Enact
# application.
#
# This class uses enact-dev (provided by enyo-dev-native) to build the
# application from source and to stage it.
# Once staged, it relies on standard (inherited) packaging functionality.

# inherit from the generic app .bbclass

inherit agl_enactjs_env

# Dependencies:
#   - ilib-webapp so we can override NPM ilib dependency with device submission
#   - enact-framework to use a shared Enact framework libraries
#   - coreutils-native to use timeout utility to prevent frozen NPM processes
AGL_ENACTJS_APP_DEPENDS = "ilib-webapp enact-framework coreutils-native"
DEPENDS_append = " ${AGL_ENACTJS_APP_DEPENDS}"

# chromium doesn't build for armv[45]*
COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE_aarch64 = "(.*)"
COMPATIBLE_MACHINE_armv6 = "(.*)"
COMPATIBLE_MACHINE_armv7a = "(.*)"
COMPATIBLE_MACHINE_armv7ve = "(.*)"
COMPATIBLE_MACHINE_x86 = "(.*)"
COMPATIBLE_MACHINE_x86-64 = "(.*)"

# The appID of the app
AGL_ENACTJS_APP_ID ??= "${BPN}"

# This variable can be used to specify the path to the directory
# that contains the package.json file for the Enact project.
# This path is relative to the app project's root directory (which is the
# default location for the package.json file).
AGL_ENACTJS_PROJECT_PATH ??= "."

# Enact build tools will output the resources
AGL_LOCALIZATION_INSTALL_RESOURCES = "false"

# Allows a component to override the standard build command with something custom
AGL_ENACTJS_PACK_OVERRIDE ??= ""

# Allows overriding the build arguments for Enact CLI
AGL_ENACTJS_PACK_OPTS ??= "--production --isomorphic --snapshot --locales=webos"

# When true, will force building apps in full CSS modular mode. Otherwise will build CSS in a global
# context with *.module.css in the modular context.
AGL_ENACTJS_FORCE_CSS_MODULES ??= "true"

# When true, will override the app's version of Enact, React, etc. with the build-target versions
# Defaults true. Any apps not compatible will system-wide Enact version standard will need to change this.
AGL_ENACTJS_SHRINKWRAP_OVERRIDE ??= "true"

# Path to javascript override of a build submission ilib
AGL_ENACTJS_ILIB_OVERRIDE ??= "${STAGING_DATADIR}/javascript/ilib"
AGL_ENACTJS_ILIB_OVERRIDE_ALLARCH ??= "${@ '${WEBOS_ENACTJS_ILIB_OVERRIDE}'.replace('${LIB32_PREFIX}', '')}"

# On-device path to ilib json assets
AGL_ENACTJS_ILIB_ASSETS ??= "${datadir}/javascript/ilib"

# Whether to force transpiling to full ES5, rather than ES6 targetted to webOS Chrome version.
AGL_ENACTJS_FORCE_ES5 ??= "false"

# May be provided by machine target; ensure the variable exists for allarch filtering
LIB32_PREFIX ??= ""

# support potential allarch enact framework data path, filtering out "lib32-" prefix
AGL_ENACTJS_FRAMEWORK ??= "${STAGING_DATADIR}/javascript/enact"
AGL_ENACTJS_FRAMEWORK_ALLARCH ??= "${@ '${WEBOS_ENACTJS_FRAMEWORK}'.replace('${LIB32_PREFIX}', '')}"

# Don't need to configure or compile anything for an enyojs app, but don't use
# do_<task>[noexec] = "1" so that recipes that inherit can still override

do_configure() {
    :
}

do_locate_enactjs_appinfo() {
    if [ -d ${S}/webos-meta ] ; then
        cp -fr ${S}/webos-meta/* ${S}
        rm -fr ${S}/webos-meta
    fi
}
addtask do_locate_enactjs_appinfo after do_configure before do_install

do_compile() {
    working=$(pwd)

    bbnote "Using Enact project at ${AGL_ENACTJS_PROJECT_PATH}"
    cd ${S}/${AGL_ENACTJS_PROJECT_PATH}

    # clear local cache prior to each compile
    bbnote "Clearing any existing node_modules"
    rm -fr node_modules

    # ensure an NPM shrinkwrap file exists so app has its dependencies locked in
    if [ ! -f npm-shrinkwrap.json ] ; then
        bberror "NPM shrinkwrap file not found. Ensure a shrinkwrap is included with the app source to lock in dependencies."
        exit 1
    fi

    # apply shrinkwrap override, rerouting to shared enact framework tarballs as needed
    if [ "${AGL_ENACTJS_SHRINKWRAP_OVERRIDE}" = "true" ] ; then
        bbnote "Attempting to use submission enact framework"
        if [ -d ${AGL_ENACTJS_FRAMEWORK} ] ; then
            bbnote "Using system submission Enact framework from ${AGL_ENACTJS_FRAMEWORK}"
            ${ENACT_NODE} "${AGL_ENACTJS_TOOL_PATH}/node_binaries/enact-override.js" "${AGL_ENACTJS_FRAMEWORK}"
        else
            if [ -d ${AGL_ENACTJS_FRAMEWORK_ALLARCH} ] ; then
                bbnote "Using system submission Enact framework from ${AGL_ENACTJS_FRAMEWORK_ALLARCH}"
                ${ENACT_NODE} "${AGL_ENACTJS_TOOL_PATH}/node_binaries/enact-override.js" "${AGL_ENACTJS_FRAMEWORK_ALLARCH}"
            else
                bbwarn "Enact framework submission could not be found"
            fi
        fi
    fi

    NPM_OPTS="--arch=${TARGET_ARCH} --loglevel=verbose install"
    if [ -z "${AGL_ENACTJS_PACK_OVERRIDE}" ] ; then
        # When a standard Enact app, we can safely skip installing any devDependencies
        NPM_OPTS="${NPM_OPTS} --only=production"
    fi

    # compile and install node modules in source directory
    bbnote "Begin NPM install process"
    ATTEMPTS=0
    STATUS=-1
    while [ ${STATUS} -ne 0 ] ; do
        ATTEMPTS=$(expr ${ATTEMPTS} + 1)
        if [ ${ATTEMPTS} -gt 5 ] ; then
            bberror "All attempts to NPM install have failed; exiting!"
            exit ${STATUS}
        fi

        # Remove any errant package locks since we are solely handling shrinkwrap
        rm -f package-lock.json

        # backup the shrinkwrap in the event "npm install" alters it
        if [ -f npm-shrinkwrap.json ] ; then
            cp -f npm-shrinkwrap.json npm-shrinkwrap.json.orig
        fi

        bbnote "NPM install attempt #${ATTEMPTS} (of 5)..." && echo
        STATUS=0
        timeout --kill-after=5m 15m ${ENACT_NPM} ${NPM_OPTS} || eval "STATUS=\$?"
        if [ ${STATUS} -ne 0 ] ; then
            bbwarn "...NPM process failed with status ${STATUS}"
        else
            bbnote "...NPM process succeeded" && echo
        fi
        # restore backup shrinkwrap file
        if [ -f npm-shrinkwrap.json.orig ] ; then
            mv -f npm-shrinkwrap.json.orig npm-shrinkwrap.json
        fi
    done

    if [ ! -z "${AGL_ENACTJS_ILIB_OVERRIDE}" ] ; then
        ## only override ilib if using Enact submission via shrinkwrap override
        if [ "${AGL_ENACTJS_SHRINKWRAP_OVERRIDE}" = "true" ] ; then
            SUB_ILIB=${AGL_ENACTJS_ILIB_OVERRIDE}
            if [ ! -d ${AGL_ENACTJS_ILIB_OVERRIDE} ] ; then
                SUB_ILIB=${AGL_ENACTJS_ILIB_OVERRIDE_ALLARCH}
            fi
            # use ilib submission component rather than one bundled within @enact/i18n
            if [ -d ${SUB_ILIB}/lib ] ; then
                # Support both old and current local ilib locations
                for LOC_ILIB in node_modules/ilib node_modules/@enact/i18n/ilib ; do
                    if [ -d ${LOC_ILIB} ] ; then
                        # override local lib with system-based submission
                        cp -fr ${SUB_ILIB}/lib ${LOC_ILIB}
                        cp -f ${SUB_ILIB}/package.json ${LOC_ILIB}
                        cp -f ${SUB_ILIB}/index.js ${LOC_ILIB}
                    fi
                done
            fi
        fi
    fi

    cd ${working}
}

do_install() {
    working=$(pwd)
    cd ${AGL_ENACTJS_PROJECT_PATH}

    # Support optional transpiling to full ES5 if needed
    export ES5="${AGL_ENACTJS_FORCE_ES5}"

    # Support forcing CSS modules for apps designed for Enact <3.0
    export ENACT_FORCECSSMODULES="${AGL_ENACTJS_FORCE_CSS_MODULES}"

    # Target build polyfills, transpiling, and CSS autoprefixing to Chrome 72
    export BROWSERSLIST="Chrome 68"

    # use local on-device ilib locale assets
    if [ ! -z "${AGL_ENACTJS_ILIB_ASSETS}" ] ; then
        export ILIB_BASE_PATH="${AGL_ENACTJS_ILIB_ASSETS}"
    fi

    # Stage app
    appdir="${D}/opt/${AGL_ENACTJS_APP_ID}"
    mkdir -p "${appdir}"

    if [ ! -z "${AGL_ENACTJS_PACK_OVERRIDE}" ] ; then
        bbnote "Custom App Build for ${AGL_ENACTJS_APP_ID}"
        ${AGL_ENACTJS_PACK_OVERRIDE}
        mv -f -v ./dist/* "${appdir}"
    else
        # Normal App Build
        bbnote "Bundling Enact app to ${appdir}"
        ${ENACT_DEV} pack ${AGL_ENACTJS_PACK_OPTS} -o "${appdir}" --verbose
    fi

    cd ${working}
}

FILES_${PN} += "/opt"
