FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://03_susan_menu_defaults_grub.cfg;subdir=git \
    file://11_sudoagi_overlay_bootargs_grub.cfg;subdir=git \
    file://90_mender_boot_grub.cfg;subdir=git \
    file://91_susan_recovery_menu_grub.cfg;subdir=git \
    file://95_mender_try_to_recover_grub.cfg;subdir=git \
    file://99_mender_end_of_grub.cfg;subdir=git \
"

do_install:append() {
    install -m 0755 -d ${D}${sysconfdir}
    echo "ENV_DIR = ${GRUB_ENV_LOCATION}" > ${D}${sysconfdir}/mender_grubenv.config
}

do_compile:prepend() {
    rm -f ${S}/89_susan_recovery_prompt_grub.cfg
}
