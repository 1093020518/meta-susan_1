SUMMARY = "Shell tab completion for Python CLI applications"
HOMEPAGE = "https://github.com/iterative/shtab"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENCE;md5=03492e75d06e3c4883c2f0567dca05f0"

DEPENDS += "python3-setuptools-scm-native"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "4e4bcb02eeb82ec45920a5d0add92eac9c9b63b2804c9196c1f1fdc2d039243c"

RDEPENDS:${PN} += "python3-core"
