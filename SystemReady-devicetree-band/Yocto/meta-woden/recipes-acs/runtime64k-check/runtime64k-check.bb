SUMMARY = "UEFI 64 KiB Page Attribute Check"
DESCRIPTION = "Build the AArch64 UEFI 64 KiB page attribute compliance application"

require recipes-acs/edk2-firmware/edk2-firmware-rev.bb

PROVIDES:remove = "virtual/uefi-firmware"
PROVIDES:remove = "virtual/bootloader"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE:genericarm64 = "genericarm64"
COMPATIBLE_HOST = "aarch64.*-linux"
EDK2_ARCH = "AARCH64"
EDK2_PLATFORM = "MdeModule"
EDK2_PLATFORM_DSC = "${WORKDIR}/bbr-acs/common/uefi_app/Runtime64KiBPageAttributeCheck/Runtime64KiBPageAttributeCheck.dsc"

SRC_URI += "git://github.com/ARM-software/bbr-acs;destsuffix=bbr-acs;protocol=https;branch=main;name=bbr-acs"
SRCREV_bbr-acs = "${AUTOREV}"

BBR_ACS_DIR = "${WORKDIR}/bbr-acs"
PACKAGES_PATH .= ":${BBR_ACS_DIR}"
EDK2_EXTRA_BUILD = ""

do_install() {
    install -d ${D}/firmware
    install \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/${EDK2_ARCH}/Runtime64KiBPageAttributeCheck.efi \
        ${D}/firmware/
}
