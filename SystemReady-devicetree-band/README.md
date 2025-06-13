# SystemReady-devicetree band ACS

## Table of Contents

- [Introduction](#introduction)
- [Latest Release details](#latest-release-details)
- [Prebuilt Images](#prebuilt-images)
- [Steps to Manually Build Image](#steps-to-manually-build-image)
- [Image Directory Structure](#image-directory-structure)
- [Details and Functionalities of the Image](#details-and-functionalities-of-the-image)
  - [Grub Menu & Complaince Run](#grub-menu--complaince-run)
  - [Log Parser scripts](#log-parser-scripts)
    - [Standard Formatted Result](#standard-formatted-result)
    - [Waiver application process](#waiver-application-process)
- [Verification on Open-Source FVP](#verification-on-open-source-fvp)
  - [Software stack and Model](#software-stack-and-model)
  - [Model run command](#model-run-command)
- [Security Implication](#security-implication)
- [License](#license)
- [Feedback, contributions, and support](#feedback-contributions-and-support)

## Introduction
SystemReady-devicetree band is a band of system compliance in the Arm SystemReady program. This compliance is for devices in the IoT edge sector that are built around SoCs based on the Arm A-profile architecture. It ensures interoperability with embedded Linux and other embedded operating systems.

SystemReady-devicetree band compliant platforms implement a minimum set of hardware and firmware features that an operating system can depend on to deploy the operating system image.

The SystemReady-devicetree band compliance and testing requirements are specified in the [Arm SystemReady Requirements Specification (SRS)](https://developer.arm.com/documentation/den0109/latest)

## Latest Release details
 - Release version: v3.0.0-BET0
 - **The latest pre-built release of SystemReady-devicetree band ACS is available for download here: [v24.11_3.0.0-BET0](prebuilt_images/v24.11_3.0.0-BET0)**
 - The compliance suite is not a substitute for design verification.
 - To review the ACS logs, Arm licensees can contact Arm directly through their partner managers.
 - SystemReady-devicetree-band Image Test Suite details

| Test Suite                                                                                   | Test Suite Tag                                               | Specification Version |
|----------------------------------------------------------------------------------------------|--------------------------------------------------------------|-----------------------|
| [Base System Architecture (BSA)](https://github.com/ARM-software/bsa-acs)                    | v24.11_REL1.0.9                                              | BSA v1.0 (c)          |
| [Base Boot Requirements (BBR)](https://github.com/ARM-software/bbr-acs)                      | v24.11_EBBR_REL2.2.0-BETA0_SBBR_REL2.1.0-BETA0_BBSR_REL1.3.0 | EBBR v2.2             |
| [Base Boot Security Requirements (BBSR)](https://github.com/ARM-software/bbr-acs)            | v24.11_EBBR_REL2.2.0-BETA0_SBBR_REL2.1.0-BETA0_BBSR_REL1.3.0 | BBSR v1.3             |
| [UEFI Self Certification Tests (UEFI-SCT)](https://github.com/tianocore/edk2-test)           | 0e2ced3befa431bb1aebff005c4c4f1a9edfe6b4                     |                       |
| [Firmware Test Suite (FWTS)](http://kernel.ubuntu.com/git/hwe/fwts.git)                      | v24.09.00                                                    |                       |

## Prebuilt images
- Prebuilt images for each release are available in the prebuilt_images folder.To access the prebuilt_images, click : [prebuilt_images](prebuilt_images/)
- The prebuilt images are archived after compression to the .xz format. On Linux, use the xz utility to uncompress the image <br />
  `xz -d systemready-dt_acs_live_image.wic.xz`. <br />
   On Windows, use the 7zip or a similar utility.
- If you choose to use the prebuilt image, skip the build steps, and navigate to the "Verification" section below.
  
## Steps to Manually Build Image

### Prerequisites
Before starting the ACS build, ensure that the following requirements are met:
 - Ubuntu 20.04 or later LTS with at least 32GB of free disk space.
 - Use bash shell.
 - You must have **sudo** privilege to install tools required for build.
 - Install `git` using `sudo apt install git`
 - `git config --global user.name "Your Name"` and `git config --global user.email "Your Email"` must be configured.

### Code download
- To build a release version of the code, checkout the main branch with the appropriate release [tag](https://github.com/ARM-software/arm-systemready/tags).
- To build the latest version of the code with bug fixes and new features, use the main branch.

### Build Steps
1. Clone the arm-systemready repository <br />
 `git clone "https://github.com/ARM-software/arm-systemready.git"`

2. Navigate to the SystemReady-devicetree band/Yocto directory <br />
 `cd arm-systemready/SystemReady-devicetree-band/Yocto`

3. Run get_source.sh to download all the related sources and tools for the build. Provide sudo permission when prompted <br />
 `./build-scripts/get_source.sh` <br />

4. To start the build of the SystemReady-devicetree band ACS live image, execute the below step <br />
 `./build-scripts/build-systemready-dt-band-live-image.sh`

5. If the above steps are successful, the bootable image will be available at <br />
   `/path-to-arm-systemready/SystemReady-devicetree-band/Yocto/meta-woden/build/tmp/deploy/images/generic-arm64/systemready-dt_acs_live_image.wic.xz`

Note: The image is generated in a compressed (.xz) format. The image must be uncompressed before it is used.<br />

### Build output
This image comprises of 2 FAT file system partition recognized by UEFI: <br />
- '/' <br />
  Root partition for Linux which contains test-suites to run in Linux environment. <br/>
- 'BOOT_ACS' <br />
  Approximate size: 250 MB <br />
  contains bootable applications and test suites. <br />
  contains a 'acs_results' directory which stores logs of the automated execution of ACS.

## Image Directory Structure
```
├── EFI
│   └── BOOT
│       ├── Shell.efi
│       ├── bbsr_startup.nsh
│       ├── bootaa64.efi
│       ├── grub.cfg
│       └── startup.nsh
├── acs_tests
│   ├── app
│   │   ├── CapsuleApp.efi
│   │   └── UpdateVars.efi
│   ├── bbr
│   │   ├── SCT
│   │   ├── SctStartup.nsh
│   │   └── bbsr_SctStartup.nsh
│   ├── bbsr-keys
│   │   ├── NullPK.auth
│   │   ├── TestDB1.auth
│   │   ├── TestDB1.der
│   │   ├── TestDBX1.auth
│   │   ├── TestDBX1.der
│   │   ├── TestKEK1.auth
│   │   ├── TestKEK1.der
│   │   ├── TestPK1.auth
│   │   └── TestPK1.der
│   ├── bsa
│   │   ├── Bsa.efi
│   │   ├── bsa.nsh
│   │   └── bsa_dt.flag
│   ├── config
│   │   ├── acs_config_dt.txt
│   │   └── system_config.txt
│   └── debug
│       ├── debug_dump.nsh
│       └── pingtest.nsh
├── acs_results
├── acs_results_template
├── Image
├── core-image-initramfs-boot-genericarm64.cpio.gz
├── ubootefi.var
└── yocto_image.flag
```
- EFI/BOOT contains the uefi automation startup scripts and grub related files
  - Shell.efi - uefi shell executable
  - bbsr_startup.nsh - bbsr uefi test startup file
  - bootaa64.efi - grub executable
  - grub.cfg - grub config file
  - startup.nsh - uefi automation run startup file
- acs_tests contains executable files and configs related for test suites
  - app directory contains CapsuleApp.efi
  - bbr directory contains SCT related bianries and sequence files
  - bbsr-keys contains cryptographic keys for secure boot and testing secure firmware updates
  - bsa directory contains bsa uefi executable for bsa complaince
  - config directory contains system, acs related config files
  - debug directory contains script to gather debug information
  - debug/pingtest.nsh is uefi script for ping test
- acs_results will contain result logs of various test suite run of ACS test suite tools
- acs_results_template Place the manual/OS test logs in this folder as per the [systemready-ir-template structure](https://gitlab.arm.com/systemready/systemready-ir-template)
- Image - Linux kernel image file, also contains linux test suites and processing scripts
  - /usr/bin/init.sh - linux automation script
  - /usr/bin/secure_init.sh - linux automation script for bbsr
  - /usr/bin/fwts - fwts executable
  - /lib/modules/*/kernel/bsa_acs/bsa_acs.ko  - BSA Linux test kernel module
  - /usr/bin/bsa  - BSA Linux app
  - /usr/bin/device_driver_info.sh - device driver script
  - /usr/bin/dt-validate - device tree validate tool
  - /usr/kernel-selftest/run_kselftest.sh - device tree kernel selftest
  - /usr/bin/ethtool-test.py - ethtool test script
  - /usr/bin/read_write_check_blk_devices.py - block device verification script
  - /usr/bin/edk2-test-parser - SCT results parser
  - /usr/bin/log_parser - directory containing results post processing script
- core-image-initramfs-boot-genericarm64.cpio.gz - ram disk file

## Details and Functionalities of the Image

### Grub Menu & Complaince Run
```
 │ Linux Boot                                    │
 │*bbr/bsa                                       │
 │ SCT for BBSR (optional)                       │
 │ Linux Boot for BBSR (optional)                │

```
 - **Linux Boot** : This option will boot the ACS Linux kernel and run the default Linux tool (linux debug dump, fwts, linux bsa, linux sbsa (if selected))
   - noacs command line parameter: Edit the Linux Boot grub menu option and add **noacs** at the end of Linux Boot grub menu option, to boot into ACS Linux kernel without running the default Linux test suites.
   - initcall_blacklist=psci_checker command line parameter: Edit the Linux Boot grub menu option and add **initcall_blacklist=psci_checker** to skip default linux psci_checker tool.
 - **bbr/bsa** : This is **default** option and will run the automated complaince
   - UEFI complaince run - SCT, BSA UEFI, [Capsule Update](https://github.com/chetan-rathore/arm-systemready/blob/main/common/docs/Automatic_Capsule_Update_guide.md)
   - Boots to Linux and run Linux complaince run - FWTS, BSA Linux
 - **SCT for BBSR (optional)** : This option will run the SCT tests required for BBSR complaince. For the verification steps of BBSR ACS, refer to the [BBSR ACS Verification](../common/docs/BBSR_ACS_Verification.md).
 - **Linux Boot for BBSR (optional)** : This option will run the SCT tests required for BBSR complaince. For the verification steps of BBSR ACS, refer to the [BBSR ACS Verification](../common/docs/BBSR_ACS_Verification.md).

### ACS configs file
- **acs_config_dt.txt**: The file specifies the ARM specification version that the ACS tool suite complies with, and this information is included in the **System_Information** table of the **ACS_Summary.html** report.

- **system_config.txt**: The file is used to collect below system information which is required for **ACS_Summary.html** report, this needs to be manually filled by user.
   - FW source code: Unknown
   - Flashing instructions: Unknown
   - product website: Unknown
   - Tested operated Systems: Unknown
   - Testlab assistance: Unknown

### Log Parser scripts
- The scripts will parse the results generated by various test suite tools and consolidate them into JSON files. These JSON files will adhere to a standard format, maintaining a consistent structure for all test suites
- Also for easier intrepretation, results will also be captured in HTML format.

#### Standard Formatted Result
- The JSON and HTML formatted results are present in /acs_results/**acs_summary**  folder.

#### Waiver application process
- Please follow the [waiver application guide](https://github.com/ARM-software/arm-systemready/blob/main/common/docs/waiver_guide.md) on details of waiver application to acs results
- Template of waiver.json can be found [here](https://github.com/ARM-software/arm-systemready/blob/main/common/docs/example_waiver.json)

## Verification on Open-Source FVP

Note: The default UEFI EDK2 setting for "Console Preference" is "Graphical". In this default setting, the Linux output goes only to the graphical console (HDMI monitor). To force serial console output, you may change "Console Preference" to "Serial".

### Software stack and Model

The U-Boot firmware and QEMU can be built with [Buildroot](https://buildroot.org/).

To download and build the firmware code, do the following:

```
git clone https://git.buildroot.net/buildroot -b 2023.05.x
cd buildroot
make qemu_aarch64_ebbr_defconfig
make
```

When the build completes, it generates the firmware file <br />
`output/images/flash.bin`, comprising TF-A, OP-TEE and the U-Boot bootloader. <br /> 
A QEMU executable is also generated at `output/host/bin/qemu-system-aarch64`.

Specific information for this Buildroot configuration is available in the file <br />
`board/qemu/aarch64-ebbr/readme.txt`.

More information on Buildroot is available in [The Buildroot user manual](https://buildroot.org/downloads/manual/manual.html).

### Model run command

Launch the model using the following command: <br />

```
./output/host/bin/qemu-system-aarch64 \
    -bios output/images/flash.bin \
    -cpu cortex-a53 \
    -d unimp \
    -device virtio-blk-device,drive=hd1 \
    -device virtio-blk-device,drive=hd0 \
    -device virtio-net-device,netdev=eth0 \
    -device virtio-rng-device,rng=rng0 \
    -drive file=<path-to/systemready-dt_acs_live_image.wic>,if=none,format=raw,id=hd0 \
    -drive file=output/images/disk.img,if=none,id=hd1 \
    -m 1024 \
    -machine virt,secure=on \
    -monitor null \
    -netdev user,id=eth0 \
    -no-acpi \
    -nodefaults \
    -nographic \
    -object rng-random,filename=/dev/urandom,id=rng0 \
    -rtc base=utc,clock=host \
    -serial stdio \
    -smp 2
```

**Note: When verifying ACS on hardware, ensure that ACS image is not in two different boot medias (USB, NVMe drives etc) attached to the device.**

### Enabling Initcall debug prints in SystemReady-devicetree band Yocto Linux boot

Enabling initcall debug prints allows the kernel to print traces of initcall functions. This feature is not enabled by default, but manually booting Linux with initcall_debug can assist users in debugging kernel issues.

Edit the "Linux boot" boot option by pressing `e` in grub window and append the boot command with following command line options.

```
initcall_debug ignore_loglevel=1
```

Press Ctrl+x to boot the Yocto linux with initcall debug prints enabled.

## Security Implication
Arm SystemReady-devicetree band ACS test suite may run at higher privilege level. An attacker may utilize these tests as a means to elevate privilege which can potentially reveal the platform security assets. To prevent the leakage of Secure information, it is strongly recommended that the ACS test suite is run only on development platforms. If it is run on production systems, the system should be scrubbed after running the test suite.

## License
System Ready ACS is distributed under Apache v2.0 License.

## Feedback, contributions, and support

 - For feedback, use the GitHub Issue Tracker that is associated with this repository.
 - For support, send an email to "support-systemready-acs@arm.com" with details.
 - Arm licensees can contact Arm directly through their partner managers.
 - Arm welcomes code contributions through GitHub pull requests.

--------------

*Copyright (c) 2022-2024, Arm Limited and Contributors. All rights reserved.*

