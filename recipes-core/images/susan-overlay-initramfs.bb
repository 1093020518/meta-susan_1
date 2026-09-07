SUMMARY = "Susan x86 overlay root initramfs"
DESCRIPTION = "Initramfs for mounting read-only A/B rootfs with a persistent overlay"
LICENSE = "MIT"

INITRAMFS_SCRIPTS = " \
    initramfs-framework-base \
    initramfs-module-udev \
    initramfs-module-rootfs \
    initramfs-module-susanoverlay \
"

PACKAGE_INSTALL = "${INITRAMFS_SCRIPTS} ${VIRTUAL-RUNTIME_base-utils} base-passwd"

# Built-in i915 requests GuC/HuC/DMC firmware before the real rootfs is mounted.
PACKAGE_INSTALL:append = " linux-firmware-i915"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""
PACKAGE_EXCLUDE = "kernel-image-*"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
IMAGE_FSTYPES:remove = "wic wic.bmap mender uefiimg uefiimg.bmap uefiimg.bz2"
IMAGE_NAME_SUFFIX ?= ""
IMAGE_ROOTFS_SIZE = "16384"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

INITRAMFS_IMAGE = ""
INITRAMFS_IMAGE_BUNDLE = ""

MENDER_FEATURES_DISABLE:append = " \
    mender-image \
    mender-install \
    mender-systemd \
    mender-image-uefi \
    mender-grub \
    mender-growfs-data \
"

inherit core-image
