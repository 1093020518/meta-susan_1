SUMMARY = "SusanOS Rust 1.97.1 toolchain"
DESCRIPTION = "Official prebuilt Rust 1.97.1 toolchain for SusanOS x86_64"
HOMEPAGE = "https://www.rust-lang.org"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

RUST_TARGET = "x86_64-unknown-linux-gnu"
RUST_PREFIX = "/opt/rust-${PV}"

SRC_URI = "https://static.rust-lang.org/dist/2026-07-16/rust-${PV}-${RUST_TARGET}.tar.xz"
SRC_URI[sha256sum] = "88f28fa9af20594179f85d6df67078dfd6fa93e2f6da5e1e9b0ac4997988ca4f"

S = "${WORKDIR}/rust-${PV}-${RUST_TARGET}"

COMPATIBLE_HOST = "x86_64.*-linux"

# Rust ships a private LLVM runtime. It must not become the
# system-wide provider of this SONAME.
PRIVATE_LIBS = "libLLVM.so.22.1-rust-1.97.1-stable"

# Rust bundled LLVM/rust-lld dynamically link against system zlib.
DEPENDS += "zlib"
RDEPENDS:${PN} += "zlib"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Keep the upstream Rust toolchain isolated from Yocto's system LLVM/libs.
    ${S}/install.sh \
        --destdir="${D}" \
        --prefix="${RUST_PREFIX}" \
        --without=rust-docs \
        --disable-ldconfig

    # Remove upstream uninstall metadata. Package lifecycle is managed by Yocto.
    rm -f ${D}${RUST_PREFIX}/lib/rustlib/uninstall.sh
    rm -f ${D}${RUST_PREFIX}/lib/rustlib/install.log

    # Remove upstream installer metadata containing build-host paths.
    rm -f ${D}${RUST_PREFIX}/lib/rustlib/manifest-*

    # Expose normal command names through /usr/bin.
    install -d ${D}${bindir}

    for tool in \
        rustc \
        cargo \
        rustdoc \
        rust-gdb \
        rust-gdbgui \
        rust-lldb \
        rustfmt \
        cargo-fmt \
        clippy-driver \
        cargo-clippy
    do
        if [ -e "${D}${RUST_PREFIX}/bin/${tool}" ]; then
            ln -sf "${RUST_PREFIX}/bin/${tool}" \
                "${D}${bindir}/${tool}"
        fi
    done
}

PACKAGES = "${PN}"

FILES:${PN} = " \
    ${RUST_PREFIX} \
    ${bindir}/rustc \
    ${bindir}/cargo \
    ${bindir}/rustdoc \
    ${bindir}/rust-gdb \
    ${bindir}/rust-gdbgui \
    ${bindir}/rust-lldb \
    ${bindir}/rustfmt \
    ${bindir}/cargo-fmt \
    ${bindir}/clippy-driver \
    ${bindir}/cargo-clippy \
"

INSANE_SKIP:${PN} += "already-stripped ldflags staticdev"
