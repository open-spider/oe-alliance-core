SUMMARY = "Linux kernel for ${MACHINE}"
LICENSE = "GPLv2"
SECTION = "kernel"
KV = "3.9.7"

inherit machine_kernel_pr

SRCDATE = "20140211"
MACHINE_KERNEL_PR_append = ".3"

SRC_URI[md5sum] = "8b79fd38328b6ad4f94b4e93b5690d22"
SRC_URI[sha256sum] = "7c5076588aa3ee66dbb0a54fd197043ab59ca6819d2e6ace569a62067e0c6fff"

LIC_FILES_CHKSUM = "file://${WORKDIR}/linux-${KV}-r1/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "http://openembedded.homelinux.com/release/images/oedrivers/linux-3.9.7-r1-${SRCDATE}.tar.gz \
    file://defconfig \
    "

S = "${WORKDIR}/linux-${KV}-r1"

inherit kernel

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_OUTPUT = "vmlinux"
KERNEL_IMAGETYPE = "vmlinux"
KERNEL_IMAGEDEST = "/tmp"

FILES_kernel-image = "${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz"

do_configure_prepend() {
    oe_machinstall -m 0644 ${WORKDIR}/defconfig ${S}/.config
    oe_runmake oldconfig
}

kernel_do_install_append() {
    ${STRIP} ${D}${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
    gzip -9c ${D}${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} > ${D}${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
    rm ${D}${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
}

MTD_DEVICE = "mtd6"

pkg_postinst_kernel-image () {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz ] ; then
            flash_erase /dev/${MTD_DEVICE} 0 0
            nandwrite -p /dev/${MTD_DEVICE} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
            rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        fi
    fi
    true
}