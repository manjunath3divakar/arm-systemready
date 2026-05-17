# SystemReady Band OS Run Guide

## Overview

This document explains how to run the Linux OS-side diagnostics for SystemReady Band.

The OS run collects debug logs from the installed operating system and runs additional Linux diagnostics. The generated logs are packaged so they can be copied into the ACS results template and validated by the parser.

## Scripts Used

The OS run uses the following scripts:

```text
linux_init.sh
linux_dump.sh
ethtool-test.py
read_write_check_blk_devices.py
system_config.txt
```

### Script Flow

```text
linux_init.sh
├── detects OS mode
├── installs required tools if possible
├── calls linux_dump.sh
├── runs read_write_check_blk_devices.py
├── runs ethtool-test.py
├── creates systemready-band-compliance-logs.tar.gz
└── prints where to copy the generated OS logs for ACS parser use

linux_dump.sh
├── collects Linux debug dump logs
├── captures firmware, ACPI, UEFI, RTC, PCI, CPU, memory, USB, and block-device information
├── performs system time and hardware clock set checks
└── restores OS time synchronization using chronyd or systemd-timesyncd when available
```

## How to Run

From the directory containing the scripts:
Run the script with root privileges.
```sh
chmod +x linux_init.sh
sudo ./linux_init.sh --mode os
```
If the script does not detect an ACS environment, it automatically runs in OS mode.

## Generated Log Directory

The OS logs are generated under the original user’s home directory:
```text
$HOME/systemready-band-compliance-logs/
```

At the end of the run, the script creates:
```text
systemready-band-compliance-logs.tar.gz
```

## Logs Collected

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


## Copying OS Logs for ACS Parser

After the OS run completes, copy the generated OS logs into the ACS results template.

Expected destination:

```text
acs_results_template/os-logs/<linux-os-name>/systemready-band-compliance-logs/
```

Final structure:

```text
acs_results_template/
└── os-logs/
    └── linux-redhat/
        └── systemready-band-compliance-logs/
            ├── dmesg.txt
            ├── lspci.txt
            ├── cat-etc-os-release.txt
            ├── ethtool-test.log
            ├── read_write_check_blk_devices.log
            └── other OS logs
```
