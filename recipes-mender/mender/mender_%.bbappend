do_install:append() {
    install -d -m 0700 ${D}/sudoagi/private/mender

    if [ -d ${D}/data/mender ]; then
        cp -a ${D}/data/mender/. ${D}/sudoagi/private/mender/
    fi

    rm -rf ${D}${localstatedir}/lib/mender
    ln -s /sudoagi/private/mender ${D}${localstatedir}/lib/mender
    rm -rf ${D}/data
}

FILES:${PN} += "/sudoagi/private/mender"
