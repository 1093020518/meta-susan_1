require nvidia-driver-580.inc

S = "${NVIDIA_SRC}"

NV_HOST_DIR = "${libdir}/nvidia"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${NV_HOST_DIR}
    local V=${PV}
    local DDIR=${D}${NV_HOST_DIR}

    for lib in libGLX_nvidia libEGL_nvidia libGLESv2_nvidia libGLESv1_CM_nvidia \
               libnvidia-glcore libnvidia-eglcore libnvidia-glvkspirv libnvidia-glsi \
               libnvidia-tls libnvidia-rtcore libnvidia-allocator libnvidia-ngx libnvoptix; do
        install -m 0644 ${NVIDIA_SRC}/${lib}.so.${V} ${DDIR}/
    done

    for lib in libnvcuvid libnvidia-encode libnvidia-fbc libnvidia-opticalflow libnvidia-present; do
        install -m 0644 ${NVIDIA_SRC}/${lib}.so.${V} ${DDIR}/
    done

    ln -sf libGLX_nvidia.so.${V}        ${DDIR}/libGLX_nvidia.so.0
    ln -sf libEGL_nvidia.so.${V}        ${DDIR}/libEGL_nvidia.so.0
    ln -sf libGLESv2_nvidia.so.${V}     ${DDIR}/libGLESv2_nvidia.so.2
    ln -sf libGLESv1_CM_nvidia.so.${V}  ${DDIR}/libGLESv1_CM_nvidia.so.1
    ln -sf libnvidia-allocator.so.${V}  ${DDIR}/libnvidia-allocator.so.1
    ln -sf libnvidia-ngx.so.${V}        ${DDIR}/libnvidia-ngx.so.1
    ln -sf libnvoptix.so.${V}           ${DDIR}/libnvoptix.so.1
    ln -sf libnvcuvid.so.${V}           ${DDIR}/libnvcuvid.so.1
    ln -sf libnvidia-encode.so.${V}     ${DDIR}/libnvidia-encode.so.1
    ln -sf libnvidia-fbc.so.${V}        ${DDIR}/libnvidia-fbc.so.1
    ln -sf libnvidia-opticalflow.so.${V} ${DDIR}/libnvidia-opticalflow.so.1

    install -m 0644 ${NVIDIA_SRC}/nvidia_icd.json ${DDIR}/
    install -m 0644 ${NVIDIA_SRC}/10_nvidia.json  ${DDIR}/

    install -d ${D}${sysconfdir}/ld.so.conf.d
    printf '%s\n' "${NV_HOST_DIR}" > ${D}${sysconfdir}/ld.so.conf.d/nvidia-graphics.conf
    install -d ${D}${datadir}/vulkan/icd.d ${D}${datadir}/glvnd/egl_vendor.d
    install -m 0644 ${NVIDIA_SRC}/nvidia_icd.json ${D}${datadir}/vulkan/icd.d/
    install -m 0644 ${NVIDIA_SRC}/10_nvidia.json  ${D}${datadir}/glvnd/egl_vendor.d/
}

FILES:${PN} = "${NV_HOST_DIR}/*.so* ${NV_HOST_DIR}/*.json \
               ${sysconfdir}/ld.so.conf.d/nvidia-graphics.conf \
               ${datadir}/vulkan/icd.d/nvidia_icd.json \
               ${datadir}/glvnd/egl_vendor.d/10_nvidia.json"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped dev-so file-rdeps"
SKIP_FILEDEPS:${PN} = "1"
PRIVATE_LIBS:${PN} = "*"

RRECOMMENDS:${PN} += "nvidia-compute-libs"
