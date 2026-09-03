SUMMARY = "CIP (Civil Infrastructure Platform) Linux kernel"
DESCRIPTION = "Mainline-based CIP kernel (linux-6.12.y-cip, ~10-yr SLTS) from \
git.kernel.org. Built via kernel-yocto with the in-tree x86_64_defconfig baseline \
(alldefconfig) PLUS a proven-booting full-config fragment (proven.cfg, captured from \
our linux-yocto 6.12 v10 build / kmeta common-pc-64 BSP) -- instead of the thin \
x86_64_defconfig alone that hung pre-earlycon on this real UEFI box. CIP source + \
SRCREV (10-yr support) kept; the proven config is applied as a merge fragment so other \
recipes/layers can still inject extra CONFIG via SRC_URI .cfg fragments. No yocto kmeta."

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit kernel kernel-yocto

KCIP_BRANCH ?= "linux-6.12.y-cip"
KBRANCH = "${KCIP_BRANCH}"
KSRC ?= "git://git.kernel.org/pub/scm/linux/kernel/git/cip/linux-cip.git;protocol=https"
SRCREV_machine ?= "021c2f95227f3aca5dd6bf4c6c761088eb0ea888"

SRC_URI = "${KSRC};branch=${KCIP_BRANCH};name=machine"
SRC_URI += "file://board-bsp.cfg"

LINUX_VERSION ?= "6.12.y-cip"
LINUX_VERSION_EXTENSION ?= ""
PV = "${LINUX_VERSION}+git${SRCPV}"
KERNEL_VERSION_SANITY_SKIP = "1"

KBUILD_DEFCONFIG ?= "x86_64_defconfig"
KCONFIG_MODE = "alldefconfig"
KMACHINE ?= "genericx86-64"
KMETA ?= ""
KERNEL_FEATURES ?= ""

COMPATIBLE_MACHINE = "genericx86-64"

DEPENDS += "xz-native bc-native elfutils-native openssl-native util-linux-native gmp-native libmpc-native"
