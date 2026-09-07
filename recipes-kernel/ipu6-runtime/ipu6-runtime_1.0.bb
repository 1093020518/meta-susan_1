SUMMARY = "Intel IPU6 runtime device permissions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://99-ipu6-psys.rules"

S = "${WORKDIR}"

do_install() {
    install -D -m 0644 \
        ${WORKDIR}/99-ipu6-psys.rules \
        ${D}${nonarch_base_libdir}/udev/rules.d/99-ipu6-psys.rules
}

FILES:${PN} = "${nonarch_base_libdir}/udev/rules.d/99-ipu6-psys.rules"
RDEPENDS:${PN} = "udev"
