
cuda_toolkit_setup_env() {
    set -e
    install -d -m 0755 ${D}${bindir}

    install -d -m 0755 ${D}${sysconfdir}/profile.d
    cat > ${D}${sysconfdir}/profile.d/cuda.sh <<'EOF'
if [ -d /usr/local/cuda ]; then
    case ":$PATH:" in *:/usr/local/cuda/bin:*) ;; *) PATH="/usr/local/cuda/bin:$PATH";; esac
    export PATH
    if [ -d /usr/local/cuda/lib64 ]; then
        case ":$LD_LIBRARY_PATH:" in *:/usr/local/cuda/lib64:*) ;; *) LD_LIBRARY_PATH="/usr/local/cuda/lib64${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}";; esac
        export LD_LIBRARY_PATH
    fi
fi
EOF
    chmod 0644 ${D}${sysconfdir}/profile.d/cuda.sh

    cat > ${D}${bindir}/nvcc <<'EOF'
export PATH="/usr/local/cuda/bin:$PATH"
exec /usr/local/cuda/bin/nvcc "$@"
EOF
    chmod 0755 ${D}${bindir}/nvcc

    for b in cuobjdump nvdisasm nvprune fatbinary ncu nsys; do
        [ -e ${D}${prefix}/local/cuda/bin/$b ] && ln -sf ../local/cuda/bin/$b ${D}${bindir}/$b
    done

    install -d -m 0755 ${D}${sysconfdir}/ld.so.conf.d
    echo /usr/local/cuda/lib64 > ${D}${sysconfdir}/ld.so.conf.d/cuda.conf
}
