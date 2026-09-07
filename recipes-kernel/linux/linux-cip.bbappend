FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://touchpad.cfg \
    file://wifi.cfg \
    file://docker-virt.cfg \
    file://igb.cfg \
    file://vlan.cfg \
    file://fuse.cfg \
    file://ipu6.cfg \
    file://watchdog.cfg \
    file://0001-media-ipu6-build-psys.patch \
    git://github.com/intel/ipu6-drivers.git;protocol=https;branch=master;name=ipu6;destsuffix=ipu6-drivers \
"

# Keep the Intel IPU6 driver source reproducible.
SRCREV_ipu6 = "71bddb5158fb7f0bd244ad7aa6b2efab024531a7"

# linux-cip.bb already uses name=machine/SRCREV_machine.
SRCREV_FORMAT = "machine_ipu6"

do_patch:append() {
    # Linux >= 6.10 already contains IPU6 Core + ISYS.
    # Only import the external PSYS implementation.
    rm -rf ${S}/drivers/media/pci/intel/ipu6/psys

    cp -a \
        ${WORKDIR}/ipu6-drivers/drivers/media/pci/intel/ipu6/psys \
        ${S}/drivers/media/pci/intel/ipu6/

    # PSYS userspace ABI.
    install -D -m 0644 \
        ${WORKDIR}/ipu6-drivers/include/uapi/linux/ipu-psys.h \
        ${S}/include/uapi/linux/ipu-psys.h
}

# Ship the PSYS userspace ABI separately so applications can build against
# the same header as the running kernel without installing all kernel sources.
PACKAGES =+ "${PN}-ipu6-uapi"

do_install:append() {
    install -D -m 0644 \
        ${S}/include/uapi/linux/ipu-psys.h \
        ${D}${includedir}/linux/ipu-psys.h

    # The external header uses kernel annotations and ioctl macros when
    # included by userspace applications. Make that branch self-contained.
    sed -i \
        '/#include <stdint.h>/a #include <linux/ioctl.h>' \
        ${D}${includedir}/linux/ipu-psys.h
    sed -i \
        '/#include <linux\/ioctl.h>/a #ifndef __user\n#define __user\n#endif' \
        ${D}${includedir}/linux/ipu-psys.h

}

FILES:${PN}-ipu6-uapi = "${includedir}/linux/ipu-psys.h"
