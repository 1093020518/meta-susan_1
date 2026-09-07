FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://70-lidar.network \
    file://71-control-uplink.network \
    file://72-control-vlan10.netdev \
    file://72-control-vlan10.network \
    file://73-debug.network \
    file://74-ec0.network \
    file://75-ec1.network \
    file://76-ec2.network \
"

do_install:append() {
    install -d ${D}${systemd_unitdir}/network
    for network_file in \
        70-lidar.network \
        71-control-uplink.network \
        72-control-vlan10.network \
        73-debug.network \
        74-ec0.network \
        75-ec1.network \
        76-ec2.network; do
        install -m 0644 ${WORKDIR}/${network_file} ${D}${systemd_unitdir}/network/${network_file}
    done
    install -m 0644 ${WORKDIR}/72-control-vlan10.netdev ${D}${systemd_unitdir}/network/72-control-vlan10.netdev

    # All SusanOS Ethernet ports are independently configured and optional at
    # boot. Waiting for an online link would add a 120-second boot timeout when
    # the device starts without network cables attached.
    install -d ${D}${sysconfdir}/systemd/system
    ln -snf /dev/null \
        ${D}${sysconfdir}/systemd/system/systemd-networkd-wait-online.service
}

FILES:${PN} += "${sysconfdir}/systemd/system/systemd-networkd-wait-online.service"
