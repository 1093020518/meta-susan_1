SUMMARY = "SusanOS user policy"
DESCRIPTION = "Project sudo policy for image-level login accounts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://susan-admins.sudoers"

RDEPENDS:${PN} = "bash sudo"

do_install() {
    install -d ${D}${sysconfdir}/sudoers.d
    install -m 0440 ${WORKDIR}/susan-admins.sudoers \
        ${D}${sysconfdir}/sudoers.d/susan-admins
}

FILES:${PN} += "${sysconfdir}/sudoers.d/susan-admins"
