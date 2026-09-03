FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://daemon.json file://data-root.conf"

do_install:append() {
    install -d ${D}${sysconfdir}/docker
    install -m 0644 ${WORKDIR}/daemon.json ${D}${sysconfdir}/docker/daemon.json
    install -d ${D}${systemd_unitdir}/system/docker.service.d
    install -m 0644 ${WORKDIR}/data-root.conf ${D}${systemd_unitdir}/system/docker.service.d/data-root.conf
}

FILES:${PN} += "${sysconfdir}/docker/daemon.json ${systemd_unitdir}/system/docker.service.d/*.conf"
