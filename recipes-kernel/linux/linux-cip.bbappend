FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://touchpad.cfg \
    file://wifi.cfg \
    file://docker-virt.cfg \
    file://igb.cfg \
    file://ipu6.cfg \
    file://watchdog.cfg \
"
