SUMMARY = "Susan initramfs overlay root module"
DESCRIPTION = "Mount active rootfs read-only and use /sudoagi/data as persistent overlay storage"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://susanoverlay"

S = "${WORKDIR}"

RDEPENDS:${PN} = "initramfs-framework-base initramfs-module-rootfs ${VIRTUAL-RUNTIME_base-utils}"

do_install() {
    install -d ${D}/init.d
    install -m 0755 ${WORKDIR}/susanoverlay ${D}/init.d/91-susanoverlay
}

FILES:${PN} = "/init.d/91-susanoverlay"
