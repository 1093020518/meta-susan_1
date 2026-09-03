require nvidia-driver-580.inc

S = "${NVIDIA_SRC}"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${libdir}
    install -d ${D}${bindir}
    install -d ${D}${mandir}/man1
    install -d ${D}${nonarch_base_libdir}/firmware/nvidia/${PV}
    install -d ${D}${sysconfdir}/OpenCL/vendors

    local V=${PV}

    for lib in libcuda libnvidia-ml libnvidia-cfg libnvidia-ptxjitcompiler \
               libnvidia-nvvm libnvidia-gpucomp; do
        install -m 0755 ${NVIDIA_SRC}/${lib}.so.${V} ${D}${libdir}/
    done

    ln -sf libcuda.so.${V} ${D}${libdir}/libcuda.so.1
    ln -sf libcuda.so.1 ${D}${libdir}/libcuda.so
    ln -sf libnvidia-ml.so.${V} ${D}${libdir}/libnvidia-ml.so.1
    ln -sf libnvidia-ml.so.1 ${D}${libdir}/libnvidia-ml.so
    ln -sf libnvidia-cfg.so.${V} ${D}${libdir}/libnvidia-cfg.so.1
    ln -sf libnvidia-cfg.so.1 ${D}${libdir}/libnvidia-cfg.so
    ln -sf libnvidia-ptxjitcompiler.so.${V} ${D}${libdir}/libnvidia-ptxjitcompiler.so.1
    ln -sf libnvidia-ptxjitcompiler.so.1 ${D}${libdir}/libnvidia-ptxjitcompiler.so
    ln -sf libnvidia-nvvm.so.${V} ${D}${libdir}/libnvidia-nvvm.so.4
    ln -sf libnvidia-nvvm.so.4 ${D}${libdir}/libnvidia-nvvm.so
    ln -sf libnvidia-gpucomp.so.${V} ${D}${libdir}/libnvidia-gpucomp.so.1
    ln -sf libnvidia-gpucomp.so.1 ${D}${libdir}/libnvidia-gpucomp.so

    for b in nvidia-smi nvidia-debugdump nvidia-modprobe nvidia-persistenced \
             nvidia-cuda-mps-server nvidia-cuda-mps-control; do
        install -m 0755 ${NVIDIA_SRC}/${b} ${D}${bindir}/
    done

    for m in nvidia-smi nvidia-modprobe nvidia-persistenced nvidia-cuda-mps-control; do
        install -m 0644 ${NVIDIA_SRC}/${m}.1.gz ${D}${mandir}/man1/ 2>/dev/null || true
    done

    install -m 0644 ${NVIDIA_SRC}/firmware/gsp_ga10x.bin ${D}${nonarch_base_libdir}/firmware/nvidia/${PV}/
    install -m 0644 ${NVIDIA_SRC}/firmware/gsp_tu10x.bin ${D}${nonarch_base_libdir}/firmware/nvidia/${PV}/

    install -m 0644 ${NVIDIA_SRC}/nvidia.icd ${D}${sysconfdir}/OpenCL/vendors/ 2>/dev/null || true

    install -d ${D}${systemd_system_unitdir}
    install -d ${D}${sbindir}
    cat > ${D}${sbindir}/nvidia-modprobe-boot <<'EOF'
nvidia-persistenced --persistence-mode >/dev/null 2>&1

for i in $(seq 1 300); do
    [ -c /dev/nvidia0 ] && break
    nvidia-modprobe -c 0 -u >/dev/null 2>&1
    [ -c /dev/nvidia0 ] && break
    sleep 2
done

nvidia-modprobe -c 0 -u >/dev/null 2>&1

for i in $(seq 1 15); do
    n=0
    for capf in $(find /proc/driver/nvidia/capabilities -type f 2>/dev/null); do
        grep -q '^DeviceFileMinor:' "$capf" 2>/dev/null || continue
        nvidia-modprobe -f "$capf" >/dev/null 2>&1 && n=$((n+1))
    done
    [ "$n" -gt 0 ] && break
    sleep 2
done
exit 0
EOF
    chmod 0755 ${D}${sbindir}/nvidia-modprobe-boot

    cat > ${D}${systemd_system_unitdir}/nvidia-modprobe.service <<'EOF'
[Unit]
Description=NVIDIA boot bring-up: trigger adapter init (persistenced) + create device nodes
DefaultDependencies=no
After=systemd-modules-load.service

[Service]
Type=oneshot
ExecStart=sudo /usr/sbin/nvidia-modprobe-boot
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF
    chmod 0644 ${D}${systemd_system_unitdir}/nvidia-modprobe.service
    install -d ${D}${sysconfdir}/systemd/system/multi-user.target.wants
    ln -sf ${systemd_system_unitdir}/nvidia-modprobe.service \
           ${D}${sysconfdir}/systemd/system/multi-user.target.wants/nvidia-modprobe.service

    nvidia_setup_lib64_loader
}

FILES:${PN} += " \
    ${libdir}/lib*.so* \
    ${bindir}/nvidia* \
    /lib64/ld-linux-x86-64.so.2 \
    ${nonarch_base_libdir}/firmware/nvidia \
    ${sysconfdir}/OpenCL \
    ${mandir}/man1/nvidia*.1.gz \
    ${sbindir}/nvidia-modprobe-boot \
    ${systemd_system_unitdir}/nvidia-modprobe.service \
    ${sysconfdir}/systemd/system/multi-user.target.wants/nvidia-modprobe.service \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped dev-so file-rdeps arch"
SKIP_FILEDEPS:${PN} = "1"
PRIVATE_LIBS:${PN} = "*"

RRECOMMENDS:${PN} += "nvidia-driver-kmod"
