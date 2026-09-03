FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://config.toml \
    file://data-root.conf \
"

do_install:append() {
    install -d ${D}${sysconfdir}/containerd
    install -m 0644 ${WORKDIR}/config.toml \
        ${D}${sysconfdir}/containerd/config.toml

    install -d ${D}${systemd_system_unitdir}/containerd.service.d
    install -m 0644 ${WORKDIR}/data-root.conf \
        ${D}${systemd_system_unitdir}/containerd.service.d/data-root.conf
}

FILES:${PN}:append = " \
    ${sysconfdir}/containerd/config.toml \
    ${systemd_system_unitdir}/containerd.service.d/data-root.conf \
"
