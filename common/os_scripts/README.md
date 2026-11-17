# SystemReady OS Scripts — Linux and WinPE

This is the **usage guide** for the SystemReady installed-OS diagnostic scripts. It covers the Linux diagnostics ZIP and the existing WinPE device/driver SystemReady scripts.

For the overall **ACS results, firmware, manual-test and OS directory structure** for **SystemReady SR and SystemReady-DT**, see the [Results Directory Structure guide](../../docs/README.md).

## SystemReady Band OS Run Guide

The OS run collects debug logs from the installed operating system and runs additional Linux diagnostics. The generated logs are packaged so they can be copied into the ACS results template and validated by the parser.

### Scripts Used

The OS run uses the following scripts:

| File | Purpose |
|------|---------|
| [`linux_init.sh`](../common/linux_scripts/linux_init.sh) | Initializes the OS run and triggers ACS or OS log collection flow |
| [`linux_dump.sh`](../common/linux_scripts/linux_dump.sh) | Collects Linux system dump logs |
| [`ethtool-test.py`](../common/linux_scripts/ethtool-test.py) | Perfoms Ethernet interface checks |
| [`read_write_check_blk_devices.py`](../common/linux_scripts/read_write_check_blk_devices.py) | Performs read/write checks on block devices |
| [`system_config.txt`](../common/config/system_config.txt) | System configuration input used by the scripts |

> **Note**: Please download the latest OS script files from the **[SystemReady-ACS Daily artifacts](https://github.com/ARM-software/arm-systemready/actions/workflows/systemready_daily.yml)**.

### Script Flow

```text
linux_init.sh
    ├── detects OS mode
    ├── installs required tools
    ├── calls linux_dump.sh
    ├── runs read_write_check_blk_devices.py to validate block devices using read checks and optional write checks.
    ├── runs ethtool-test.py to validate Ethernet interfaces using link, ethtool, IP, ping, wget, and curl checks.
    ├── creates systemready-band-compliance-logs.tar.gz
    └── prints where to copy the generated OS logs for ACS parser use

linux_dump.sh
    ├── collects Linux debug dump logs
    ├── captures firmware, ACPI, UEFI, RTC, PCI, CPU, memory, USB, and block-device information
    ├── performs system time and hardware clock set checks
    └── restores OS time synchronization using chronyd or systemd-timesyncd when available
```

### How to Run

#### Download and extract

Download the repository-hosted ZIP from a Linux machine with network access:

```bash
wget "https://raw.githubusercontent.com/ARM-software/arm-systemready/main/common/os_scripts/systemready_os_diagnostics.zip"
unzip systemready_os_diagnostics.zip
cd systemready_os_diagnostics
```
Alternatively, download the [ZIP file](systemready_os_diagnostics.zip) from GitHub and transfer it to the installed OS being tested.

#### Commands to Run
From the directory containing the scripts, run the script with root privileges.
```sh
chmod +x linux_init.sh
```
```sh
sudo ./linux_init.sh --mode os
```

#### Generated Log Directory

The OS logs are generated under the user’s home directory:
```text
$HOME/systemready-band-compliance-logs/
```

At the end of the run, the script creates:
```text
systemready-band-compliance-logs.tar.gz
```

### Logs Collected

The OS run collects the following Linux debug logs:
```text
dmesg.txt
lspci.txt
lspci-vvv.txt
cat-proc-interrupts.txt
cat-proc-cpuinfo.txt
cat-proc-meminfo.txt
cat-proc-iomem.txt
lscpu.txt
lsblk.txt
lsusb.txt
lshw.txt
dmidecode.txt
dmidecode.bin
uname-a.txt
cat-etc-os-release.txt
date.txt
timedatectl.txt
cat-proc-driver-rtc.txt
hwclock.txt
efibootmgr.txt
efibootmgr-t-20.txt
efibootmgr-t-5.txt
efibootmgr-c.txt
ifconfig.txt
ip-addr-show.txt
ping-c-5-www-arm-com.txt
cat-proc-cmdline.txt
df-h.txt
mount.txt
lsmod.txt
acpi.log
*.dat
*.dsl
acpixtract.txt
iasl.txt
date-set-202212150530.txt
date-after-set.txt
hw-clock-set-20230101091015.txt
hwclock-after-set.txt
firmware.txt
firmware/
time-sync-restore.txt
```

Additional diagnostic logs:

```text
read_write_check_blk_devices.log
ethtool-test.log
```


### Copying OS Logs for ACS Parser

Please rename the subfolders to indicate the OS name and version. For example, rename 'linux-distro1-version' to 'linux-rhel-9.4'.

It is recommended to choose one OS per group below.
- Groups based on heritage: RHEL/Fedora/CentOS/AlmaLinux, SLES/openSUSE, Ubuntu/Debian, or NetBSD/OpenBSD/FreeBSD.
- For example, you can choose RHEL, SLES, or Ubuntu for your testing.

After the OS run completes, copy the generated OS logs into the ACS results template.

Expected destination:

```text
acs_results_template/os-logs/<linux-os-name>/systemready-band-compliance-logs/
```

Directory structure:

```text
acs_results_template/
└── os-logs/
    └── linux-[distroname]-[distroversion] (ex: linux-rhel-10.1)/
        └── console.log
        └── systemready-band-compliance-logs/
            ├── dmesg.txt
            ├── lspci.txt
            ├── cat-etc-os-release.txt
            ├── ethtool-test.log
            ├── read_write_check_blk_devices.log
            └── other OS logs
```

> The resulting files must be under `os-logs/linux-rhel-10.1/systemready-band-compliance-logs/`. Repeat for each tested OS, using the correct OS name and version. Collect `console.log` **separately** from reset through firmware, OS installation, and boot, and place it alongside the extracted folder. `linux_init.sh` does not create the serial-console log.

-----------------

## Windows Scripts

### device_driver_winpe.bat
A batch script to capture **device driver** and **network configuration** information in **Windows Preinstallation Environment (WinPE)**, useful for system diagnostics.

### Setup
Boot the system into the **Windows Preinstallation Environment (WinPE)**.

Download the `device_driver_winpe.bat` script from the `common/windows_scripts/` directory of the `arm-systemready` repository and save it to a writable disk or partition accessible from WinPE.

To identify the available disks and volumes in WinPE:
```cmd
diskpart
list volume
exit
```
Switch to the drive where the script was downloaded. For example:
```cmd
D:
cd <path-to-script>
```
---
### Commands used in the script
```cmd
pnputil /enum-devices /connected
```
> Lists all currently connected devices with their associated driver details. Useful for confirming hardware recognition and driver loading inside WinPE.

```cmd
ipconfig /all
```
> Outputs detailed networking configuration — including IP address, gateway, DNS, MAC address, DHCP info, and more. Helpful for verifying network connectivity in pre-boot environments.

---
### Usage
To run and save the output to a file:
```cmd
device_driver_winpe.bat > device_driver_winpe.log
```
> The log will be saved as `device_driver_winpe.log` in the current directory.

**Note:** Use `type device_driver_winpe.log | more` to view logs page by page in the WinPE environment.

---
### Saving Logs to ACS Results
Identify the drive containing the ACS results and copy the generated log to the corresponding WinPE OS logs directory in the ACS results:

```cmd
copy device_driver_winpe.log D:\acs_results_template\os-logs\win-[version]\
```
The resulting log should be available at:
```text
acs_results_template/os-logs/win-[version]/device_driver_winpe.log
```
Alternatively, the generated `device_driver_winpe.log` can be downloaded or copied from the WinPE environment to the local system and then manually copied to `acs_results_template/os-logs/win-[version]/.`

---------------------------------
*Copyright (c) 2025-2026, Arm Limited and Contributors. All rights reserved.*
