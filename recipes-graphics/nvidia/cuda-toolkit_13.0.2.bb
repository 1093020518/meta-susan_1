require cuda-toolkit-common.inc

SUMMARY = "NVIDIA CUDA 13.0.2 toolkit (nvcc + math/runtime libs, headless compute)"
DESCRIPTION = "CUDA 13.0.2 toolkit for the Blackwell (RTX 50) GPU: nvcc, libcudart, \
libcurand, libcublas, libcufft, libcusparse, libcusolver, Thrust headers, and \
Nsight Compute/Systems. Toolkit-only (no driver, no OpenGL) for a headless CUDA box."

LICENSE = "NVIDIA-Proprietary"
NO_GENERIC_LICENSE[NVIDIA-Proprietary] = "EULA.txt"
LIC_FILES_CHKSUM = "file://EULA.txt;md5=9bb406cb55047e0a074c8509c51c1ca4"

PV = "13.0.2"

CUDA_INSTALLER_VERSION = "13.0.2_580.95.05"
CUDA_INSTALLER_NAME = "cuda_${CUDA_INSTALLER_VERSION}_linux.run"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "https://developer.download.nvidia.com/compute/cuda/13.0.2/local_installers/${CUDA_INSTALLER_NAME};downloadfilename=${CUDA_INSTALLER_NAME}"
SRC_URI[sha256sum] = "81a5d0d0870ba2022efb0a531dcc60adbdc2bbff7b3ef19d6fd6d8105406c775"

S = "${WORKDIR}/cuda_install"

do_compile[noexec] = "1"

do_unpack() {
    chmod +x ${DL_DIR}/${CUDA_INSTALLER_NAME}
    rm -rf ${S}
    install -d ${S}
    install -d ${WORKDIR}/cuda_tmp
    ${DL_DIR}/${CUDA_INSTALLER_NAME} \
        --silent --toolkit --no-opengl-libs --no-man-page --override \
        --installpath=${S} \
        --tmpdir=${WORKDIR}/cuda_tmp \
        || { echo "cuda installer failed; see ${WORKDIR}/cuda_tmp"; exit 1; }
    rm -rf ${WORKDIR}/cuda_tmp
}

do_install() {
    set -e
    if [ ! -f ${S}/version.json ] || [ ! -e ${S}/bin/nvcc ]; then
        echo "ERROR: cuda-toolkit do_install: expected ${S}/bin/nvcc + version.json (flat installpath layout) not found"
        echo "S contents:"; ls -la ${S}
        exit 1
    fi

    install -d ${D}${prefix}/local
    cp -a ${S} ${D}${prefix}/local/cuda-13.0

    ln -sf cuda-13.0 ${D}${prefix}/local/cuda

    chown -R root:root ${D}${prefix}/local/cuda-13.0

    cuda_toolkit_setup_env
}

FILES:${PN} += "${prefix}/local/cuda ${prefix}/local/cuda-13.0"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped dev-so file-rdeps arch staticdev"
SKIP_FILEDEPS:${PN} = "1"
PRIVATE_LIBS:${PN} = "*"

SKIP_PACKAGE_RDEPENDS:${PN} = "1"

RDEPENDS:${PN} += "nvidia-compute-libs"
