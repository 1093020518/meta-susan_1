SUMMARY = "Python bindings for the NVIDIA Management Library"
HOMEPAGE = "https://pypi.org/project/nvidia-ml-py/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=266;endline=278;md5=6ff8af14c318beec4f912120562b0008"

PYPI_PACKAGE = "nvidia_ml_py"

inherit pypi setuptools3

SRC_URI[sha256sum] = "6490e93fea99eb4e966327ae18c6eec6256194c921f23459c8767aee28c54581"

RDEPENDS:${PN} += "python3-core python3-ctypes"
