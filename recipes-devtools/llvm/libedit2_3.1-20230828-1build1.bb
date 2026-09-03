SUMMARY = "libedit.so.2 (BSD line editor), rehosted from Ubuntu noble .deb"
DESCRIPTION = "libedit 3.1-20230828 (the BSD line-edit library) rehosted from the Ubuntu \
noble libedit2 .deb, as a transitive runtime dep of the rehosted libLLVM-18 \
(libLLVM-18 DT_NEEDED libedit.so.2). oe's libedit0 ships libedit.so.0, NOT .so.2, \
so the Ubuntu-built libLLVM-18 cannot resolve without this rehost. Staged to \
/usr/lib/x86_64-linux-gnu (already in ld.so.conf.d/llvm18-libs); libedit.so.2 \
DT_NEEDED libtinfo.so.6 (companion libtinfo6 recipe) + libbsd.so.0 (oe libbsd0, \
already in the image)."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE-libedit;md5=f7af8917847d961cd40ec83a79dea107"

PV = "3.1-20230828-1build1"

S = "${WORKDIR}"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "dpkg-native"

SRC_URI = "https://mirrors.tuna.tsinghua.edu.cn/ubuntu/pool/main/libe/libedit/libedit2_3.1-20230828-1build1_amd64.deb;downloadfilename=libedit2_3.1-20230828-1build1_amd64.deb \
           file://LICENSE-libedit"
SRC_URI[sha256sum] = "3afbc8ddf835049dd4d88275b9d13680e4c4cdd59e191cbd63424152cc08e00e"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${libdir}/x86_64-linux-gnu

    local stage=${WORKDIR}/libedit-stage
    rm -rf "$stage"
    mkdir -p "$stage"
    dpkg-deb -x ${DL_DIR}/libedit2_3.1-20230828-1build1_amd64.deb "$stage"

    if [ ! -e "$stage/usr/lib/x86_64-linux-gnu/libedit.so.2" ]; then
        echo "ERROR: libedit2 do_install: /usr/lib/x86_64-linux-gnu/libedit.so.2 not found in $stage"
        ls -la "$stage/usr/lib/x86_64-linux-gnu" 2>&1 | head
        exit 1
    fi

    cp -a "$stage/usr/lib/x86_64-linux-gnu/libedit.so.2.0.72" ${D}${libdir}/x86_64-linux-gnu/
    cp -a "$stage/usr/lib/x86_64-linux-gnu/libedit.so.2"      ${D}${libdir}/x86_64-linux-gnu/

    chown -R root:root ${D}
}

FILES:${PN} += " \
    ${libdir}/x86_64-linux-gnu/libedit.so.2 \
    ${libdir}/x86_64-linux-gnu/libedit.so.2.0.72 \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped file-rdeps arch textrel"
SKIP_FILEDEPS:${PN} = "1"

RDEPENDS:${PN} += "libtinfo6 libbsd"
