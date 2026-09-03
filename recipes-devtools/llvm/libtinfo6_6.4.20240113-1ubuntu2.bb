SUMMARY = "libtinfo.so.6 (ncurses terminfo runtime), rehosted from Ubuntu noble .deb"
DESCRIPTION = "libtinfo 6.4+20240113 (ncurses terminfo runtime) rehosted from the Ubuntu \
noble libtinfo6 .deb, as a transitive runtime dep of the rehosted libLLVM-18 \
(libLLVM-18 DT_NEEDED libtinfo.so.6). oe's ncurses ships libtinfo.so.5 (ABI 5), \
NOT .so.6, so the Ubuntu-built libLLVM-18 cannot resolve without this rehost. \
Staged to /usr/lib/x86_64-linux-gnu (already in ld.so.conf.d/llvm18-libs); \
libtinfo.so.6 DT_NEEDED is libc only, no further closure."

LICENSE = "X11 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE-libtinfo;md5=36b4f748fe84bf613a10acb29ac390f3"

PV = "6.4.20240113-1ubuntu2"

S = "${WORKDIR}"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "dpkg-native"

SRC_URI = "https://mirrors.tuna.tsinghua.edu.cn/ubuntu/pool/main/n/ncurses/libtinfo6_6.4%2B20240113-1ubuntu2_amd64.deb;downloadfilename=libtinfo6_6.4+20240113-1ubuntu2_amd64.deb \
           file://LICENSE-libtinfo"
SRC_URI[sha256sum] = "53e1e1753729d04cf65b05e6e58abe06e2bb76cc07eff0e1b2a638a638ca209b"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${libdir}/x86_64-linux-gnu

    local stage=${WORKDIR}/libtinfo-stage
    rm -rf "$stage"
    mkdir -p "$stage"
    dpkg-deb -x ${DL_DIR}/libtinfo6_6.4+20240113-1ubuntu2_amd64.deb "$stage"

    if [ ! -e "$stage/usr/lib/x86_64-linux-gnu/libtinfo.so.6" ]; then
        echo "ERROR: libtinfo6 do_install: /usr/lib/x86_64-linux-gnu/libtinfo.so.6 not found in $stage"
        ls -la "$stage/usr/lib/x86_64-linux-gnu" 2>&1 | head
        exit 1
    fi

    cp -a "$stage/usr/lib/x86_64-linux-gnu/libtinfo.so.6.4" ${D}${libdir}/x86_64-linux-gnu/
    cp -a "$stage/usr/lib/x86_64-linux-gnu/libtinfo.so.6"   ${D}${libdir}/x86_64-linux-gnu/

    chown -R root:root ${D}
}

FILES:${PN} += " \
    ${libdir}/x86_64-linux-gnu/libtinfo.so.6 \
    ${libdir}/x86_64-linux-gnu/libtinfo.so.6.4 \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped file-rdeps arch textrel"
SKIP_FILEDEPS:${PN} = "1"

