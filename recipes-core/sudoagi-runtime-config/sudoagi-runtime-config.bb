SUMMARY = "SudoAGI runtime persistent configuration"
DESCRIPTION = "Initialize private partition layout and bind selected device-level configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = " \
    file://persistent-config.conf \
    file://sudoagi-persistent-config \
    file://sudoagi-persistent-config.service \
"

RDEPENDS:${PN} = "bash util-linux"

do_install() {
    install -d ${D}${sysconfdir}/sudoagi
    install -m 0644 ${WORKDIR}/persistent-config.conf \
        ${D}${sysconfdir}/sudoagi/persistent-config.conf

    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/sudoagi-persistent-config \
        ${D}${sbindir}/sudoagi-persistent-config

    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/sudoagi-persistent-config.service \
        ${D}${systemd_system_unitdir}/sudoagi-persistent-config.service

    install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
    ln -snf ${systemd_system_unitdir}/sudoagi-persistent-config.service \
        ${D}${sysconfdir}/systemd/system/multi-user.target.wants/sudoagi-persistent-config.service

    install -d ${D}/sudoagi/private/overlay/rootfs_a/upper
    install -d ${D}/sudoagi/private/overlay/rootfs_a/work
    install -d ${D}/sudoagi/private/overlay/rootfs_b/upper
    install -d ${D}/sudoagi/private/overlay/rootfs_b/work
    install -d ${D}/sudoagi/private/config
    install -d -m 0700 ${D}/sudoagi/private/mender


    echo "device_type=${MENDER_DEVICE_TYPE}" > ${D}/sudoagi/private/mender/device_type
}

FILES:${PN} += " \
    /sudoagi/private \
    ${sysconfdir}/sudoagi \
    ${sysconfdir}/systemd/system/multi-user.target.wants/sudoagi-persistent-config.service \
    ${systemd_system_unitdir}/sudoagi-persistent-config.service \
    ${sbindir}/sudoagi-persistent-config \
"
