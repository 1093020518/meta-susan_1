SUMMARY = "SusanOS base utilities (Ubuntu-24.04-style toolset)"
DESCRIPTION = "The command-line toolset a Noble server install would have, \
mapped onto Yocto/oe-core + meta-oe packages for the scarthgap branch."

inherit packagegroup

PACKAGES = "${PN}"

RDEPENDS:${PN} = " \
    base-files \
    base-passwd \
    bash \
    ldd \
    bash-completion \
    bash-completion-extra \
    coreutils \
    cpio \
    diffutils \
    file \
    findutils \
    gawk \
    grep \
    gzip \
    less \
    logrotate \
    ncurses \
    net-tools \
    procps \
    psmisc \
    sed \
    tar \
    time \
    unzip \
    util-linux \
    util-linux-fdisk \
    pciutils \
    pciutils-ids \
    xz \
    \
    vim \
    nano \
    \
    curl \
    wget \
    ca-certificates \
    openssh \
    openssh-sftp-server \
    \
    iproute2 \
    iputils-ping \
    iptables \
    dnsmasq \
    \
    sudo \
    \
    htop \
    tree \
    man-pages \
    \
    tzdata \
    \
    apt \
    dpkg \
    \
    git \
    python3 \
    python3-pip \
    rsync \
    tmux \
    screen \
    zip \
    zstd \
    lsof \
    strace \
    bc \
    dmidecode \
    man-db \
    \
    cmake \
    meson \
    ninja \
    autoconf \
    automake \
    libtool \
    cronie \
    chrony \
    parted \
    gptfdisk \
    nvme-cli \
    hdparm \
    smartmontools \
    efibootmgr \
    socat \
    sshfs-fuse \
    \
    zsh \
    gnupg \
    lsb-release \
"

RRECOMMENDS:${PN} = " \
    ${@bb.utils.contains('DISTRO_FEATURES','bluetooth','bluez5','',d)} \
    ${@bb.utils.contains('DISTRO_FEATURES','wifi','wpa-supplicant','',d)} \
    kernel-modules \
    linux-firmware \
"
