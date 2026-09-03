SUMMARY = "Shared Miniforge3 runtime for SusanOS users"
DESCRIPTION = "Installs Miniforge3 once into persistent storage on first boot"
HOMEPAGE = "https://github.com/conda-forge/miniforge"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI = " \
    file://${DL_DIR}/Miniforge3-Linux-x86_64.sh;name=installer;unpack=0 \
    file://install-miniforge3 \
    file://miniforge3-install.service \
    file://miniforge3.sh \
"
SRC_URI[installer.sha256sum] = "848194851a98903134187fbb4ab50efe87b003e0c0f808f97644b7524a62bf2c"

S = "${WORKDIR}"
PACKAGE_ARCH = "${MACHINE_ARCH}"
COMPATIBLE_HOST = "x86_64.*-linux"

inherit systemd

RDEPENDS:${PN} = "bash coreutils ldd"
SYSTEMD_SERVICE:${PN} = "miniforge3-install.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${libexecdir}/miniforge3
    install -m 0755 ${DL_DIR}/Miniforge3-Linux-x86_64.sh \
        ${D}${libexecdir}/miniforge3/Miniforge3-Linux-x86_64.sh
    install -m 0755 ${WORKDIR}/install-miniforge3 \
        ${D}${libexecdir}/miniforge3/install-miniforge3

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/miniforge3-install.service \
        ${D}${systemd_system_unitdir}/miniforge3-install.service

    install -d ${D}${sysconfdir}/profile.d
    install -m 0644 ${WORKDIR}/miniforge3.sh \
        ${D}${sysconfdir}/profile.d/miniforge3.sh

    install -d ${D}/opt
    ln -s /data/miniforge3 ${D}/opt/miniforge3
}

FILES:${PN} += " \
    ${libexecdir}/miniforge3 \
    ${systemd_system_unitdir}/miniforge3-install.service \
    ${sysconfdir}/profile.d/miniforge3.sh \
    /opt/miniforge3 \
"
