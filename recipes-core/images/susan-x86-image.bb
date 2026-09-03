SUMMARY = "Susan x86 image"
DESCRIPTION = "SusanOS x86 image"

require recipes-core/images/core-image-minimal.bb

inherit extrausers

IMAGE_FEATURES:remove = " \
    debug-tweaks \
    empty-root-password \
    allow-empty-password \
    allow-root-login \
    serial-autologin-root \
"

SUSAN_IMAGE_USERS ?= "tom jerry"
SUSAN_IMAGE_FIRST_UID ?= "1000"
SUSAN_IMAGE_ADMIN_GROUP ?= "susan-admins"
SUSAN_IMAGE_ADMIN_GID ?= "1100"
SUSAN_IMAGE_DEFAULT_GROUPS ?= "${SUSAN_IMAGE_ADMIN_GROUP} video audio dialout docker"

SUSAN_IMAGE_PASSWORD_HASH ?= "\$6\$susan-simple-pas\$RmNfZVhZsWXg0uBtnhjQDH4.VnmCOFRu9V/vWTY2ILDA60kSxqmDDt1IlOczlRGZ4K/YD76emElbnNhqCGG03/"

def susan_image_users(d):
    import re

    raw_users = d.getVar("SUSAN_IMAGE_USERS") or ""
    users = raw_users.replace("{", " ").replace("}", " ").replace(",", " ").split()
    if not users:
        bb.fatal("SUSAN_IMAGE_USERS must contain at least one user")
    if len(users) != len(set(users)):
        bb.fatal("SUSAN_IMAGE_USERS contains duplicate user names")

    for user in users:
        if not re.match(r"^[a-z_][a-z0-9_-]*$", user):
            bb.fatal("Invalid user name in SUSAN_IMAGE_USERS: %s" % user)

    return users

def susan_image_default_groups(d):
    import re

    admin_group = d.getVar("SUSAN_IMAGE_ADMIN_GROUP")
    raw_groups = d.getVar("SUSAN_IMAGE_DEFAULT_GROUPS") or ""
    groups = raw_groups.replace(",", " ").split()
    if not groups:
        bb.fatal("SUSAN_IMAGE_DEFAULT_GROUPS must contain %s" % admin_group)
    if len(groups) != len(set(groups)):
        bb.fatal("SUSAN_IMAGE_DEFAULT_GROUPS contains duplicate group names")
    if admin_group not in groups:
        bb.fatal(
            "SUSAN_IMAGE_DEFAULT_GROUPS must include %s to retain sudo access"
            % admin_group
        )

    for group in groups:
        if not re.match(r"^[a-z_][a-z0-9_-]*$", group):
            bb.fatal("Invalid group name in SUSAN_IMAGE_DEFAULT_GROUPS: %s" % group)

    return groups

def susan_image_extra_users(d):
    users = susan_image_users(d)
    first_uid = int(d.getVar("SUSAN_IMAGE_FIRST_UID"))
    admin_group = d.getVar("SUSAN_IMAGE_ADMIN_GROUP")
    admin_gid = int(d.getVar("SUSAN_IMAGE_ADMIN_GID"))
    default_groups = ",".join(susan_image_default_groups(d))
    password_hash = d.getVar("SUSAN_IMAGE_PASSWORD_HASH")

    if admin_gid in range(first_uid, first_uid + len(users)):
        bb.fatal("SUSAN_IMAGE_ADMIN_GID overlaps the generated user/group ID range")

    commands = ["groupadd --gid %d %s" % (admin_gid, admin_group)]
    for offset, user in enumerate(users):
        uid = first_uid + offset
        commands.append("groupadd --gid %d %s" % (uid, user))
        commands.append(
            "useradd --uid %d --gid %s --groups %s "
            "--home-dir /home/%s --no-create-home --shell /bin/bash "
            "--password '%s' %s"
            % (uid, user, default_groups, user, password_hash, user)
        )

    return "; ".join(commands) + ";"

SUSAN_IMAGE_USERS_NORMALIZED = "${@' '.join(susan_image_users(d))}"
EXTRA_USERS_PARAMS:append = " ${@susan_image_extra_users(d)}"

CORE_IMAGE_EXTRA_INSTALL += " packagegroup-susan-base \
    susan-users \
    sudoagi-runtime-config \
    miniforge3 \
    perl \
    kernel-modules \
    linux-firmware \
    nvidia-driver-kmod \
    nvidia-compute-libs \
    nvidia-display-libs \
    nvidia-container-toolkit \
    cuda-toolkit \
    \
    gcc gcc-symlinks g++ g++-symlinks cpp cpp-symlinks \
    libstdc++-dev glibc-dev linux-libc-headers-dev \
    binutils binutils-symlinks make pkgconfig \
    rust cargo rust-rustdoc rust-tools-clippy rust-tools-rustfmt \
"

LICENSE = "MIT"

do_image_wic[depends] += "susan-recovery-image:do_image_complete"

disable_root_login() {
    if [ -e ${IMAGE_ROOTFS}${sysconfdir}/shadow ]; then
        sed --follow-symlinks -i 's%^root:[^:]*:%root:*:%' \
            ${IMAGE_ROOTFS}${sysconfdir}/shadow
    fi

    install -d ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config.d
    cat > ${IMAGE_ROOTFS}${sysconfdir}/ssh/sshd_config.d/10-disable-root.conf <<'EOF'
PermitRootLogin no
PermitEmptyPasswords no
EOF
}
ROOTFS_POSTPROCESS_COMMAND += "disable_root_login;"


do_rootfs[depends] += "${INITRAMFS_IMAGE}:do_image_complete virtual/kernel:do_deploy"

install_bundled_kernel_for_mender() {
    bundled_kernel="${DEPLOY_DIR_IMAGE}/bzImage-initramfs-${MACHINE}.bin"
    if [ ! -e "$bundled_kernel" ]; then
        bbfatal "Bundled initramfs kernel not found: $bundled_kernel"
    fi

    install -d ${IMAGE_ROOTFS}/boot
    kernel_target="$(readlink ${IMAGE_ROOTFS}/boot/bzImage || true)"
    if [ -n "$kernel_target" ]; then
        install -m 0644 "$bundled_kernel" "${IMAGE_ROOTFS}/boot/$kernel_target"
    else
        install -m 0644 "$bundled_kernel" "${IMAGE_ROOTFS}/boot/bzImage"
    fi
}
ROOTFS_POSTPROCESS_COMMAND += "install_bundled_kernel_for_mender;"

install_susan_user_tmpfiles() {
    install -d ${IMAGE_ROOTFS}${nonarch_libdir}/tmpfiles.d

    {
        echo "# Created from SUSAN_IMAGE_USERS by susan-x86-image.bb"
        for user in ${SUSAN_IMAGE_USERS_NORMALIZED}; do
            echo "C /home/$user 0755 $user $user - /etc/skel"
        done
    } > ${IMAGE_ROOTFS}${nonarch_libdir}/tmpfiles.d/susan-users.conf
}
ROOTFS_POSTPROCESS_COMMAND += "install_susan_user_tmpfiles;"

bind_home_to_data() {
    install -d ${IMAGE_ROOTFS}/data/home
    echo "/data/home /home none bind 0 0" >> ${IMAGE_ROOTFS}${sysconfdir}/fstab
}
ROOTFS_POSTPROCESS_COMMAND += "bind_home_to_data;"

prepare_log_partition() {
    install -d ${IMAGE_ROOTFS}/var/log
    install -d -m 02755 ${IMAGE_ROOTFS}/var/log/journal
    JGID=$(sed -n 's/^systemd-journal:x:\([0-9][0-9]*\):.*/\1/p' ${IMAGE_ROOTFS}${sysconfdir}/group)
    if [ -n "$JGID" ]; then
        chown root:$JGID ${IMAGE_ROOTFS}/var/log/journal
    fi
    echo "/dev/nvme0n1p6 /var/log ext4 defaults 0 2" >> ${IMAGE_ROOTFS}${sysconfdir}/fstab
}
ROOTFS_POSTPROCESS_COMMAND += "prepare_log_partition;"

create_sudoagi_paths() {
    install -d ${IMAGE_ROOTFS}/sudoagi
    ln -snf /data ${IMAGE_ROOTFS}/sudoagi/data
    install -d ${IMAGE_ROOTFS}/sudoagi/private
    echo "/dev/nvme0n1p8 /sudoagi/private ext4 defaults 0 2" >> ${IMAGE_ROOTFS}${sysconfdir}/fstab
}
ROOTFS_POSTPROCESS_COMMAND += "create_sudoagi_paths;"

IMAGE_INSTALL:append = " docker-moby docker-compose libclang1-18 libllvm18 libedit2 libtinfo6"
