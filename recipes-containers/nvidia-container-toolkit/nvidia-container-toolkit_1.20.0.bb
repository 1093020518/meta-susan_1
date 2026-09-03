
SUMMARY = "NVIDIA Container Toolkit — GPU injection for docker containers"
DESCRIPTION = "Lets `docker run --gpus all` pass the host NVIDIA GPU into a \
container by injecting the /dev/nvidia* device nodes + the compute shared \
libraries (libcuda/libnvidia-ml). v1.20.0 prebuilt amd64 binaries rehosted \
from the upstream GitHub release tarball. GPU-architecture-agnostic; host \
driver decides the GPU generation (Blackwell here)."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

PV = "1.20.0"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "dpkg-native"

SRC_URI = "https://github.com/NVIDIA/nvidia-container-toolkit/releases/download/v${PV}/nvidia-container-toolkit_${PV}_deb_amd64.tar.gz;downloadfilename=nvidia-container-toolkit_${PV}_deb_amd64.tar.gz"
SRC_URI[sha256sum] = "6b6e45547f0db11ab46c6d28693d0d5a087464a783fafe474bb8848cbc56b4e6"

S = "${WORKDIR}/release-v1.20.0-stable"
DEBDIR = "${S}/packages/ubuntu18.04/amd64"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${bindir}
    install -d ${D}${libdir}
    install -d ${D}${sysconfdir}/nvidia-container-toolkit
    install -d ${D}${systemd_system_unitdir}

    local stage=${WORKDIR}/nctk-stage
    rm -rf "$stage"
    for p in libnvidia-container1 libnvidia-container-tools \
             nvidia-container-toolkit-base nvidia-container-toolkit; do
        mkdir -p "$stage/$p"
        dpkg-deb -x ${DEBDIR}/${p}_${PV}-1_amd64.deb "$stage/$p"
    done

    install -m 0755 "$stage/libnvidia-container1/usr/lib/x86_64-linux-gnu/libnvidia-container.so.1.20.0" \
                    ${D}${libdir}/
    ln -sf libnvidia-container.so.1.20.0 ${D}${libdir}/libnvidia-container.so.1
    install -m 0755 "$stage/libnvidia-container1/usr/lib/x86_64-linux-gnu/libnvidia-container-go.so.1.20.0" \
                    ${D}${libdir}/
    ln -sf libnvidia-container-go.so.1.20.0 ${D}${libdir}/libnvidia-container-go.so.1

    install -m 0755 "$stage/libnvidia-container-tools/usr/bin/nvidia-container-cli" ${D}${bindir}/
    install -m 0755 "$stage/nvidia-container-toolkit-base/usr/bin/nvidia-container-runtime" ${D}${bindir}/
    install -m 0755 "$stage/nvidia-container-toolkit-base/usr/bin/nvidia-ctk" ${D}${bindir}/
    install -m 0755 "$stage/nvidia-container-toolkit-base/usr/bin/nvidia-cdi-hook" ${D}${bindir}/
    install -m 0755 "$stage/nvidia-container-toolkit/usr/bin/nvidia-container-runtime-hook" ${D}${bindir}/

    cat > ${D}${sysconfdir}/nvidia-container-toolkit/config.toml <<'EOF'
disable-require = false
supported-driver-capabilities = "compat32,compute,display,graphics,ngx,utility,video"

[nvidia-container-cli]
environment = []
ldconfig = "@/usr/sbin/ldconfig"
load-kmods = true

[nvidia-container-runtime]
log-level = "info"
mode = "legacy"
runtimes = ["runc"]

[nvidia-container-runtime.modes.legacy]
cuda-compat-mode = "ldconfig"

[nvidia-container-runtime-hook]
path = "nvidia-container-runtime-hook"
skip-mode-detection = false

[nvidia-ctk]
path = "nvidia-ctk"
EOF
    chmod 0644 ${D}${sysconfdir}/nvidia-container-toolkit/config.toml

    install -m 0644 "$stage/nvidia-container-toolkit-base/lib/systemd/system/nvidia-cdi-refresh.service" \
                    ${D}${systemd_system_unitdir}/
    install -m 0644 "$stage/nvidia-container-toolkit-base/lib/systemd/system/nvidia-cdi-refresh.path" \
                    ${D}${systemd_system_unitdir}/
    install -m 0644 "$stage/nvidia-container-toolkit-base/etc/nvidia-container-toolkit/nvidia-cdi-refresh.env" \
                    ${D}${sysconfdir}/nvidia-container-toolkit/

    chown -R root:root ${D}
}

FILES:${PN} += " \
    ${bindir}/nvidia-container-cli \
    ${bindir}/nvidia-container-runtime \
    ${bindir}/nvidia-container-runtime-hook \
    ${bindir}/nvidia-ctk \
    ${bindir}/nvidia-cdi-hook \
    ${libdir}/libnvidia-container.so.1* \
    ${libdir}/libnvidia-container-go.so.1* \
    ${sysconfdir}/nvidia-container-toolkit \
    ${systemd_system_unitdir}/nvidia-cdi-refresh.* \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags textrel already-stripped file-rdeps dev-so arch"
SKIP_FILEDEPS:${PN} = "1"
PRIVATE_LIBS:${PN} = "*"

RDEPENDS:${PN} += "libcap libseccomp ldconfig"

RRECOMMENDS:${PN} += "nvidia-driver-kmod nvidia-compute-libs"
