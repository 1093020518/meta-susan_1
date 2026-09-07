SUMMARY = "Cross-platform system monitoring tool"
HOMEPAGE = "https://github.com/nicolargo/glances"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=852ecadc0ac7e6f4d7144d5544a3815b"

PYPI_PACKAGE = "glances"

inherit pypi python_setuptools_build_meta

SRC_URI[sha256sum] = "56695ff97043149e007edbb188dea82dd29f479e2ca5b8324a119b95aafb8fa4"

RDEPENDS:${PN} += " \
    python3-core \
    python3-defusedxml \
    python3-packaging \
    python3-psutil \
    python3-shtab \
"
