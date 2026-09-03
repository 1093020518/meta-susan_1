require nvidia-driver-580.inc

inherit module

do_compile[depends] += "virtual/kernel:do_shared_workdir"

export ARCH = "${TARGET_ARCH}"
EXTRA_OEMAKE:append = " \
    SYSSRC=${STAGING_KERNEL_DIR} \
    KERNEL_OUTPUT=${STAGING_KERNEL_BUILDDIR} \
    ARCH=${TARGET_ARCH} \
    NV_KERNEL_MODULES='nvidia nvidia-uvm' \
"

do_shared_workdir:append() {
    cp -r ${STAGING_KERNEL_BUILDDIR}/scripts $kerneldir/
    cp ${STAGING_KERNEL_BUILDDIR}/include/config/auto.conf $kerneldir/include/config/auto.conf
}

do_install:append() {
    install -d ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra
    install -m 0644 ${S}/nvidia.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/
    install -m 0644 ${S}/nvidia-uvm.ko ${D}${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/

    install -d ${D}${sysconfdir}/modules-load.d
    cat > ${D}${sysconfdir}/modules-load.d/nvidia.conf <<EOF
nvidia
nvidia-uvm
EOF
}

FILES:${PN} += " \
    ${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/*.ko \
    ${sysconfdir}/modules-load.d/nvidia.conf \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped"
