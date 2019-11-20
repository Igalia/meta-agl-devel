SUMMARY = "The software for DEMO platform of AGL IVI profile"
DESCRIPTION = "A set of packages belong to AGL Demo Platform"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-demo-platform-html5 \
    "

ALLOW_EMPTY_${PN} = "1"

RDEPENDS_${PN} += "\
    packagegroup-agl-image-ivi \
    "

# add packages for demo platform (include demo apps) here
RDEPENDS_${PN} += " \
    "

# add packages for WAM
RDEPENDS_${PN} += " \
    chromium-browser-service \
    enactbrowser-service \
    wam \
    "
