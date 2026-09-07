SUMMARY = "SusanOS persistent data directory initialization"
DESCRIPTION = "Prepare persistent containerd and workspace directories on the SusanOS data partition"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://sudoagi-data-init \
    file://sudoagi-data-init.service \
"

S = "${WORKDIR}"

inherit systemd

RDEPENDS:${PN} += "gzip tar"

SYSTEMD_SERVICE:${PN} = "sudoagi-data-init.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${libexecdir}
    install -m 0755 ${WORKDIR}/sudoagi-data-init \
        ${D}${libexecdir}/sudoagi-data-init

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sudoagi-data-init.service \
        ${D}${systemd_system_unitdir}/sudoagi-data-init.service
}

FILES:${PN} += " \
    ${libexecdir}/sudoagi-data-init \
    ${systemd_system_unitdir}/sudoagi-data-init.service \
"
