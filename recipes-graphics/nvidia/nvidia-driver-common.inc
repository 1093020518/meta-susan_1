
SUMMARY = "NVIDIA Linux GPU driver (compute-only, open kernel modules)"
DESCRIPTION = "NVIDIA driver for Blackwell (RTX 50 series), compute subset only \
(no X/Wayland/GL display stack). Builds the OPEN kernel modules (nvidia.ko + \
nvidia-uvm.ko) from the .run's kernel-open/ tree — Blackwell GPUs require the \
open modules (the closed ones refuse RmInitAdapter)."

LICENSE = "NVIDIA-Proprietary"
NO_GENERIC_LICENSE[NVIDIA-Proprietary] = "LICENSE"
LIC_FILES_CHKSUM = "file://LICENSE;md5=92aa2e2af6aa0bcba1c3fe49da021937"

NVIDIA_ARCHIVE_NAME = "NVIDIA-Linux-${TARGET_ARCH}-${PV}-no-compat32"
NVIDIA_SRC = "${WORKDIR}/${NVIDIA_ARCHIVE_NAME}"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "https://download.nvidia.com/XFree86/Linux-${TARGET_ARCH}/${PV}/NVIDIA-Linux-${TARGET_ARCH}-${PV}-no-compat32.run;downloadfilename=${NVIDIA_ARCHIVE_NAME}.run"

S = "${NVIDIA_SRC}/kernel-open"

do_unpack() {
    chmod +x ${DL_DIR}/${NVIDIA_ARCHIVE_NAME}.run
    rm -rf ${NVIDIA_SRC}
    ${DL_DIR}/${NVIDIA_ARCHIVE_NAME}.run -x --target ${NVIDIA_SRC}
    if [ "${S}" != "${NVIDIA_SRC}" ]; then
        cp ${NVIDIA_SRC}/LICENSE ${S}/LICENSE
    fi
}

do_make_scripts[noexec] = "1"

nvidia_setup_lib64_loader() {
    install -d ${D}/lib64
    ln -sf /usr/lib/ld-linux-x86-64.so.2 ${D}/lib64/ld-linux-x86-64.so.2
}
