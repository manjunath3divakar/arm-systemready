KBRANCH ?= "v6.6/standard/base"

require recipes-kernel/linux/linux-yocto.inc

# CVE exclusions
include recipes-kernel/linux/cve-exclusion.inc
include recipes-kernel/linux/cve-exclusion_6.6.inc

# board specific branches
KBRANCH:qemuarm  ?= "v6.6/standard/arm-versatile-926ejs"
KBRANCH:qemuarm64 ?= "v6.6/standard/qemuarm64"
KBRANCH:qemumips ?= "v6.6/standard/mti-malta32"
KBRANCH:qemuppc  ?= "v6.6/standard/qemuppc"
KBRANCH:qemuriscv64  ?= "v6.6/standard/base"
KBRANCH:qemuriscv32  ?= "v6.6/standard/base"
KBRANCH:qemux86  ?= "v6.6/standard/base"
KBRANCH:qemux86-64 ?= "v6.6/standard/base"
KBRANCH:qemuloongarch64  ?= "v6.6/standard/base"
KBRANCH:qemumips64 ?= "v6.6/standard/mti-malta64"

SRCREV_machine:qemuarm ?= "ceb94a85299b59d8840ed7ed392b1d3e4c727678"
SRCREV_machine:qemuarm64 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemuloongarch64 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemumips ?= "c79ffc89f8909f60de52005ef258db9752634eda"
SRCREV_machine:qemuppc ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemuriscv64 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemuriscv32 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemux86 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemux86-64 ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_machine:qemumips64 ?= "b0a73fa83073c8d7d7bc917bcbeac88d296ebe38"
SRCREV_machine ?= "2d01bc1d4eeade12518371139dd24a21438f523c"
SRCREV_meta ?= "f7f00b22efcfcae6489e9ec7db7002685fbc078b"

# set your preferred provider of linux-yocto to 'linux-yocto-upstream', and you'll
# get the <version>/base branch, which is pure upstream -stable, and the same
# meta SRCREV as the linux-yocto-standard builds. Select your version using the
# normal PREFERRED_VERSION settings.
BBCLASSEXTEND = "devupstream:target"
SRCREV_machine:class-devupstream ?= "5c7587f69194bc9fc714953ab4c7203e6e68885b"
PN:class-devupstream = "linux-yocto-upstream"
KBRANCH:class-devupstream = "v6.6/base"

SRC_URI = "git://git.yoctoproject.org/linux-yocto.git;name=machine;branch=${KBRANCH};protocol=https \
           git://git.yoctoproject.org/yocto-kernel-cache;type=kmeta;name=meta;branch=yocto-6.6;destsuffix=${KMETA};protocol=https \
           https://gitlab.arm.com/linux-arm/linux-acs/-/raw/master/kernel/src/0001-BSA-ACS-Linux-6.6.patch;patch=1;md5sum=89b6c420ece275846f79c8b6f6f9cb09 \
           file://0001-KSelfTest.patch;patch=1 \
           file://0001-dt-extract-compatibles.patch;patch=1 \
           file://0001-disable-psci-checker.patch;patch=1 \
           "

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"
LINUX_VERSION ?= "6.6.23"

PV = "${LINUX_VERSION}+git"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "1"

KERNEL_DEVICETREE:qemuarmv5 = "arm/versatile-pb.dtb"

COMPATIBLE_MACHINE = "^(qemuarm|qemuarmv5|qemuarm64|qemux86|qemuppc|qemuppc64|qemumips|qemumips64|qemux86-64|qemuriscv64|qemuriscv32|qemuloongarch64)$"

# Functionality flags
KERNEL_EXTRA_FEATURES ?= "features/netfilter/netfilter.scc"
KERNEL_FEATURES:append = " ${KERNEL_EXTRA_FEATURES}"
KERNEL_FEATURES:append:qemuall=" cfg/virtio.scc features/drm-bochs/drm-bochs.scc cfg/net/mdio.scc"
KERNEL_FEATURES:append:qemux86=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append:qemux86-64=" cfg/sound.scc cfg/paravirt_kvm.scc"
KERNEL_FEATURES:append = " ${@bb.utils.contains("TUNE_FEATURES", "mx32", " cfg/x32.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/scsi/scsi-debug.scc features/nf_tables/nft_test.scc", "", d)}"
KERNEL_FEATURES:append = " ${@bb.utils.contains("DISTRO_FEATURES", "ptest", " features/gpio/mockup.scc features/gpio/sim.scc", "", d)}"
KERNEL_FEATURES:append:powerpc =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64 =" arch/powerpc/powerpc-debug.scc"
KERNEL_FEATURES:append:powerpc64le =" arch/powerpc/powerpc-debug.scc"

INSANE_SKIP:kernel-vmlinux:qemuppc64 = "textrel"

#added extra
PACKAGECONFIG[dt] = ",,, bash"