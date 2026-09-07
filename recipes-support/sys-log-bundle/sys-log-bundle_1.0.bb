SUMMARY = "System log collection bundle"
DESCRIPTION = "One-shot journald, rsyslog and kernel log collection tool seeded to the persistent data partition"
LICENSE = "CLOSED"

SRC_URI = "file://sys-log-bundle-20260825.tgz;unpack=0"

S = "${WORKDIR}"

RDEPENDS:${PN} = " \
    bash \
    coreutils \
    gzip \
    procps \
    rsyslog \
    sudo \
    systemd \
    tar \
    util-linux \
"

do_install() {
    install -d ${D}${datadir}/sys-tools/data-seed
    install -m 0644 ${WORKDIR}/sys-log-bundle-20260825.tgz \
        ${D}${datadir}/sys-tools/data-seed/sys-log-bundle-20260825.tgz
}

FILES:${PN} = "${datadir}/sys-tools/data-seed/sys-log-bundle-20260825.tgz"
