SUMMARY = "libLLVM 18 runtime shared library, rehosted from Ubuntu noble .deb"
DESCRIPTION = "libLLVM 18.1.3 (the LLVM core runtime) rehosted from the Ubuntu \
noble libllvm18 .deb, as the companion runtime for libclang1-18 (libclang-18 \
DT_NEEDED libLLVM.so.18.1). Replicates the .deb layout (real ELF + multiarch \
symlink) so ldd matches the build host; registered via ld.so.conf.d/llvm18-libs. \
Transitive deps already in the base image (libffi/libxml2/libzstd/libbsd/libstdc++/\
libgcc) are RRECOMMENDS-pinned; libedit.so.2 + libtinfo.so.6 (wrong SONAME in oe) \
are RDEPENDS-pinned via the companion libedit2 + libtinfo6 rehost recipes."

LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE-llvm;md5=ec6f7011b45cef66cfdff004963c6bb2"

PV = "18.1.3-1ubuntu1"

S = "${WORKDIR}"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "dpkg-native"

SRC_URI = "https://mirrors.tuna.tsinghua.edu.cn/ubuntu/pool/main/l/llvm-toolchain-18/libllvm18_18.1.3-1ubuntu1_amd64.deb;downloadfilename=libllvm18_18.1.3-1ubuntu1_amd64.deb \
           file://LICENSE-llvm"
SRC_URI[sha256sum] = "af6197096ce3b89aab86de59f01c1b5feb008b2ae1514a03906c0664edb8b0b1"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${libdir}/x86_64-linux-gnu
    install -d ${D}${libdir}/llvm-18/lib

    local stage=${WORKDIR}/libllvm-stage
    rm -rf "$stage"
    mkdir -p "$stage"
    dpkg-deb -x ${DL_DIR}/libllvm18_18.1.3-1ubuntu1_amd64.deb "$stage"

    if [ ! -e "$stage/usr/lib/llvm-18/lib/libLLVM.so.1" ]; then
        echo "ERROR: libllvm18 do_install: /usr/lib/llvm-18/lib/libLLVM.so.1 not found in $stage"
        ls -la "$stage/usr/lib/llvm-18/lib" 2>&1 | head
        exit 1
    fi

    cp -a "$stage/usr/lib/llvm-18/lib/libLLVM.so.1"     ${D}${libdir}/llvm-18/lib/
    cp -a "$stage/usr/lib/llvm-18/lib/libLLVM.so.18.1" ${D}${libdir}/llvm-18/lib/
    cp -a "$stage/usr/lib/x86_64-linux-gnu/libLLVM.so.18.1" ${D}${libdir}/x86_64-linux-gnu/

    chown -R root:root ${D}
}

FILES:${PN} += " \
    ${libdir}/x86_64-linux-gnu/libLLVM.so.18.1 \
    ${libdir}/llvm-18/lib/libLLVM.so.1 \
    ${libdir}/llvm-18/lib/libLLVM.so.18.1 \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped file-rdeps arch textrel"
SKIP_FILEDEPS:${PN} = "1"

RDEPENDS:${PN} += "libedit2 libtinfo6"

RRECOMMENDS:${PN} += "libffi libxml2 zstd libbsd libstdc++ libgcc"
