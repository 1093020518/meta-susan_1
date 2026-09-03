SUMMARY = "libclang (Clang C API) shared library, rehosted from Ubuntu noble .deb"
DESCRIPTION = "libclang 18.1.3 (the Clang C interface library) rehosted from the \
Ubuntu noble libclang1-18 .deb, for bindgen/clang-sys at Rust compile time. \
glibc 2.39 matches (noble == Scarthgap); libLLVM-18 runtime need satisfied by the \
companion libllvm18 recipe. Staged to /usr/lib/x86_64-linux-gnu + this recipe ships \
/etc/ld.so.conf.d/llvm18-libs.conf so clang-sys finds it without LIBCLANG_PATH."

LICENSE = "Apache-2.0-with-LLVM-exception"
LIC_FILES_CHKSUM = "file://LICENSE-clang;md5=ec6f7011b45cef66cfdff004963c6bb2"

PV = "18.1.3-1ubuntu1"

S = "${WORKDIR}"

COMPATIBLE_MACHINE = "genericx86-64"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS += "dpkg-native"

SRC_URI = "https://mirrors.tuna.tsinghua.edu.cn/ubuntu/pool/main/l/llvm-toolchain-18/libclang1-18_18.1.3-1ubuntu1_amd64.deb;downloadfilename=libclang1-18_18.1.3-1ubuntu1_amd64.deb \
           file://LICENSE-clang"
SRC_URI[sha256sum] = "1ed67106d743ab4f12db2726c5fc11c436c5e1fba613e9f1006bf3fa05cc69bd"

do_compile[noexec] = "1"

do_install() {
    set -e
    install -d ${D}${libdir}/x86_64-linux-gnu

    local stage=${WORKDIR}/libclang-stage
    rm -rf "$stage"
    mkdir -p "$stage"
    dpkg-deb -x ${DL_DIR}/libclang1-18_18.1.3-1ubuntu1_amd64.deb "$stage"

    if [ ! -e "$stage/usr/lib/x86_64-linux-gnu/libclang-18.so.18" ]; then
        echo "ERROR: libclang1-18 do_install: /usr/lib/x86_64-linux-gnu/libclang-18.so.18 not found in $stage"
        ls -la "$stage/usr/lib/x86_64-linux-gnu" 2>&1 | head
        exit 1
    fi

    cp -a "$stage/usr/lib/x86_64-linux-gnu/libclang-18.so.18" ${D}${libdir}/x86_64-linux-gnu/
    cp -a "$stage/usr/lib/x86_64-linux-gnu/libclang-18.so.1"  ${D}${libdir}/x86_64-linux-gnu/

    chown -R root:root ${D}

    install -d ${D}${sysconfdir}/ld.so.conf.d
    printf '%s\n' '/usr/lib/x86_64-linux-gnu' '/usr/lib/llvm-18/lib' > ${D}${sysconfdir}/ld.so.conf.d/llvm18-libs.conf
}

FILES:${PN} += " \
    ${libdir}/x86_64-linux-gnu/libclang-18.so.18 \
    ${libdir}/x86_64-linux-gnu/libclang-18.so.1 \
    ${sysconfdir}/ld.so.conf.d/llvm18-libs.conf \
"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INSANE_SKIP:${PN} += "ldflags already-stripped file-rdeps arch textrel"
SKIP_FILEDEPS:${PN} = "1"

RDEPENDS:${PN} += "libllvm18"
