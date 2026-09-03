SUMMARY = "SudoAGI recovery tools"
DESCRIPTION = "Factory restore helpers for Susan x86 recovery system"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
    file://sudoagi-factory-restore \
    file://sudoagi-recovery-shell \
"

RDEPENDS:${PN} = " \
    bash \
    coreutils \
    util-linux \
    e2fsprogs \
    parted \
    tar \
    gzip \
    xz \
    zstd \
    grub-mender-grubenv \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/sudoagi-factory-restore ${D}${sbindir}/sudoagi-factory-restore
    install -m 0755 ${WORKDIR}/sudoagi-recovery-shell ${D}${sbindir}/sudoagi-recovery-shell
}

FILES:${PN} += "${sbindir}/sudoagi-factory-restore ${sbindir}/sudoagi-recovery-shell"
