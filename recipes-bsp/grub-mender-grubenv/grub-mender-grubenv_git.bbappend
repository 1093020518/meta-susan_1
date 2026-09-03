FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
SRC_URI:append = " file://11_sudoagi_overlay_bootargs_grub.cfg;subdir=git"

do_install:append() {
    install -m 0755 -d ${D}${sysconfdir}
    echo "ENV_DIR = ${GRUB_ENV_LOCATION}" > ${D}${sysconfdir}/mender_grubenv.config
}
SRC_URI:append = " file://03_susan_menu_defaults_grub.cfg;subdir=git"
SRC_URI:append = " file://90_mender_boot_grub.cfg;subdir=git"
SRC_URI:append = " file://91_susan_recovery_menu_grub.cfg;subdir=git"
SRC_URI:append = " file://95_mender_try_to_recover_grub.cfg;subdir=git"

do_compile:prepend() {
    rm -f ${S}/89_susan_recovery_prompt_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/03_susan_menu_defaults_grub.cfg ${S}/03_susan_menu_defaults_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/11_sudoagi_overlay_bootargs_grub.cfg ${S}/11_sudoagi_overlay_bootargs_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/90_mender_boot_grub.cfg ${S}/90_mender_boot_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/91_susan_recovery_menu_grub.cfg ${S}/91_susan_recovery_menu_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/95_mender_try_to_recover_grub.cfg ${S}/95_mender_try_to_recover_grub.cfg
    install -m 0644 /workspace/lyn/yocto_x86/meta-susan/recipes-bsp/grub-mender-grubenv/files/99_mender_end_of_grub.cfg ${S}/99_mender_end_of_grub.cfg
}
SRC_URI:append = " file://99_mender_end_of_grub.cfg;subdir=git"
