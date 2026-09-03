SUMMARY = "Susan x86 recovery image"
DESCRIPTION = "Minimal recovery system for Susan x86 platform"
LICENSE = "MIT"

IMAGE_INSTALL = " \
    packagegroup-core-boot \
    kernel-image \
    kernel-modules \
    bash \
    e2fsprogs \
    e2fsprogs-e2fsck \
    e2fsprogs-mke2fs \
    e2fsprogs-tune2fs \
    util-linux \
    parted \
    dosfstools \
    nvme-cli \
    iproute2 \
    iputils \
    ethtool \
    dhcpcd \
    curl \
    ca-certificates \
    kmod \
    zstd \
    xz \
    gzip \
    tar \
    sudoagi-recovery-tools \
"

IMAGE_FEATURES += "ssh-server-dropbear"

IMAGE_FSTYPES = "ext4"
IMAGE_FSTYPES:remove = "wic wic.bmap mender uefiimg uefiimg.bmap uefiimg.bz2"
EXTRA_IMAGECMD:ext4:append = " -L recovery"
IMAGE_ROOTFS_SIZE:forcevariable = "1572864"
IMAGE_ROOTFS_MAXSIZE:forcevariable = "2097152"

MENDER_FEATURES_DISABLE:append = " \
    mender-image \
    mender-install \
    mender-systemd \
    mender-image-uefi \
    mender-grub \
    mender-growfs-data \
"

inherit core-image


set_recovery_root_readonly() {
    if [ -e ${IMAGE_ROOTFS}${sysconfdir}/fstab ]; then
        sed -i 's%^/dev/root[[:space:]]\+/[[:space:]]\+auto[[:space:]]\+defaults%/dev/root            /                    auto       ro%' ${IMAGE_ROOTFS}${sysconfdir}/fstab
    fi
}
ROOTFS_POSTPROCESS_COMMAND += "set_recovery_root_readonly;"
