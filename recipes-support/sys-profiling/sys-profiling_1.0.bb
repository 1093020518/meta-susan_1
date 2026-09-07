SUMMARY = "System profiling tools"
DESCRIPTION = "Glances CSV recording and offline plotting tools seeded to the persistent data partition"
LICENSE = "CLOSED"

SRC_URI = "file://sys-profiling-service.tar.gz;unpack=0"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    bash \
    coreutils \
    python3-modules \
    python3-glances \
    python3-nvidia-ml-py \
    python3-pandas \
    python3-matplotlib \
"

do_install() {
    install -d ${D}${datadir}/sys-tools/data-seed
    install -m 0644 ${WORKDIR}/sys-profiling-service.tar.gz \
        ${D}${datadir}/sys-tools/data-seed/sys-profiling-service.tar.gz
}

FILES:${PN} = "${datadir}/sys-tools/data-seed/sys-profiling-service.tar.gz"
