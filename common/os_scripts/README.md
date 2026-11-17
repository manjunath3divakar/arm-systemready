# SystemReady OS Scripts — Linux and WinPE

This is the **single usage guide** for the SystemReady installed-OS diagnostic scripts. It covers the Linux diagnostics ZIP and the existing WinPE device/driver script, without duplicating the Windows batch file in this directory.

For the overall **ACS results, firmware, manual-test and OS evidence directory structure** for **SystemReady SR and SystemReady-DT**, see the [Results Directory Structure guide](../../docs/README.md).

## 1. Available OS scripts

| File / location | Purpose | Band guidance |
| --- | --- | --- |
| [`common/os_scripts/systemready_os_diagnostics.zip`](systemready_os_diagnostics.zip) | Packaged Linux diagnostics: system dumps, block-device checks and network checks. | Packaged for **SystemReady SR installed-OS testing**. Do not assume its use is mandatory for DT. |
| [`common/windows_scripts/device_driver_winpe.bat`](../windows_scripts/device_driver_winpe.bat) | Captures connected device/driver details and network configuration in WinPE. | Use where WinPE evidence is requested. **The script stays in `common/windows_scripts/`; no second copy is maintained here.** |

The Linux ZIP contains:

```text
systemready_os_diagnostics/
├── linux_init.sh
├── linux_dump.sh
├── ethtool-test.py
├── read_write_check_blk_devices.py
├── system_config.txt
└── sr_band_os_test_guide.md
```

These files come from [`common/linux_scripts/`](../linux_scripts/), [`common/config/system_config.txt`](../config/system_config.txt) and [`docs/sr_band_os_test_guide.md`](../../docs/sr_band_os_test_guide.md). The ZIP is a **snapshot** of those sources and should be refreshed whenever they change.

## 2. Linux OS diagnostics (SystemReady SR)

For **SystemReady SR**, collect installed-OS evidence from **RHEL and SLES/SLE**; testing other Linux distributions is recommended. This requirement does **not** automatically apply to SystemReady-DT. Collect OS installation and boot serial-console logs separately from the diagnostic output.

### Download and extract

Download the repository-hosted ZIP from a Linux machine with network access:

```bash
wget "https://raw.githubusercontent.com/ARM-software/arm-systemready/main/common/os_scripts/systemready_os_diagnostics.zip"
unzip systemready_os_diagnostics.zip
cd systemready_os_diagnostics
```

Alternatively, download the [ZIP file](systemready_os_diagnostics.zip) from GitHub and transfer it to the installed OS being tested.

### Configure and run

Before running, review `system_config.txt`, especially:

```text
Total_number_of_network_controllers= 1
```

This specifies how many network interfaces are expected to pass. The source configuration documents that setting it to `0` or commenting it out requests compliance from all existing interfaces. Ensure the relevant network ports are connected and configured.

Run:

```bash
chmod +x linux_init.sh
sudo ./linux_init.sh --mode os
```

The script attempts to install diagnostic dependencies using `apt-get`, `dnf`, `yum` or `zypper` and then runs:

| Component | What it collects or checks |
| --- | --- |
| `linux_dump.sh` | OS and firmware information: kernel logs, PCI/USB devices, CPU/memory, ACPI/UEFI, RTC/clock, disks and system configuration. |
| `read_write_check_blk_devices.py` | Checks eligible block devices, including read and controlled write/verify/restore operations on available space. |
| `ethtool-test.py` | Ethernet interface, link, address and connectivity tests including `ethtool`, ping, `wget` and `curl`. |

**Precaution:** Run on a dedicated test system. The diagnostics may change UEFI boot variables (`efibootmgr -c`), system/hardware-clock settings and test data on eligible unmounted block-device space. Back up important data and review the scripts before use; automated restoration cannot guarantee recovery from every interruption or hardware failure. Package installation and external-network tests may require network access.

### Generated logs and submission location

The expanded log directory is created under the **invoking user's home directory** (not necessarily root's home when using `sudo`):

```text
$HOME/systemready-band-compliance-logs/
```

At the end of the run, the final archive is created **beside `linux_init.sh`**, in the extracted scripts directory:

```text
systemready_os_diagnostics/systemready-band-compliance-logs.tar.gz
```

The archive contains a top-level `systemready-band-compliance-logs/` directory. Its files include `dmesg.txt`, `lspci.txt`, `dmidecode.txt`, `cat-etc-os-release.txt`, `ethtool-test.log`, `read_write_check_blk_devices.log` and other platform/firmware dumps.

Copy the archive to the corresponding installed-OS results location and **extract it**. For example, from the results submission root:

```bash
mkdir -p os-logs/linux-rhel-10.1
cp /path/to/systemready-band-compliance-logs.tar.gz os-logs/linux-rhel-10.1/
tar -xzf os-logs/linux-rhel-10.1/systemready-band-compliance-logs.tar.gz \
    -C os-logs/linux-rhel-10.1/
```

The resulting files must be under `os-logs/linux-rhel-10.1/systemready-band-compliance-logs/`. Repeat for each tested OS, using the correct OS name and version. Collect `console.log` **separately** from reset through firmware, OS installation and boot and place it alongside the extracted folder. `linux_init.sh` does not create the serial-console log.

If a network interface does not receive an address, check the link, DHCP and routing configuration. Temporary manual commands such as `ip link set dev <interface> up` and `ip addr add <address>/<prefix> dev <interface>` may help when appropriate. On intranet-only systems, a manual ping to a reachable internal host can help diagnose connectivity; it does not replace the automated check, and its original errors should be retained.

For the complete diagnostics file list and script flow, see the [SR band OS run guide](../../docs/sr_band_os_test_guide.md).

## 3. WinPE device/driver and network diagnostics

The batch script is **already maintained** at [`common/windows_scripts/device_driver_winpe.bat`](../windows_scripts/device_driver_winpe.bat). Do not download it from an Actions artifact and do not add a duplicate to `common/os_scripts/`.

### Download the existing WinPE script

On a machine preparing the WinPE test media, download:

```bash
wget "https://raw.githubusercontent.com/ARM-software/arm-systemready/main/common/windows_scripts/device_driver_winpe.bat"
```

Copy `device_driver_winpe.bat` to a writable drive accessible from the Arm64 WinPE environment. `wget` is a preparation command on the host; it is not assumed to exist in WinPE.

### Run in WinPE

Boot WinPE and identify the volume containing the batch file:

```cmd
diskpart
list volume
exit
```

If the script is stored in `D:\`, for example:

```cmd
D:
cd \
device_driver_winpe.bat > device_driver_winpe.log
```

The script itself runs exactly these commands:

```cmd
pnputil /enum-devices /connected
ipconfig /all
```

The `>` redirection is required to create `device_driver_winpe.log` in the current directory. To inspect it:

```cmd
type device_driver_winpe.log | more
```

Copy the log into the appropriate submitted OS evidence, for example:

```text
os-logs/winpe-<version>/device_driver_winpe.log
```

Also save the WinPE boot/serial-console evidence independently. Where the WinPE/SAC validation procedure requires it, capture firmware-to-WinPE boot, working keyboard, and manual command results: `ver`, `pnputil /enum-devices`, `pnputil /enum-drivers`, `diskpart` (`list disk`, `list volume`), `dir /w`, `ipconfig /all`, `date` and `time`. The batch file **does not** perform these entire manual SAC/WinPE steps.

For serial SAC access, use `id`, `d`, `i`, `s` and then `cmd` when available; `ch` displays channels, and `Esc + Tab` switches between SAC and command channels. Use `shutdown` from SAC when the session is complete. Record the command-console output separately from the generated batch log.

## 4. Source and maintenance

- The Linux ZIP is assembled from the same six source files listed in the [SystemReady CI workflow](../../.github/workflows/systemready_ci.yml). The committed ZIP should be rebuilt and reviewed when any source changes; it does not update itself.
- The original WinPE script remains at [`common/windows_scripts/device_driver_winpe.bat`](../windows_scripts/device_driver_winpe.bat), avoiding duplicate `.bat` files or divergent versions.
- Requirements for particular OSes and evidence categories depend on the band. See the [shared results directory guide](../../docs/README.md), the [SR flow](../../docs/systemready_band_flow.md) and the [DT flow](../../docs/systemready_devicetree_band_flow.md).

---

*Copyright (c) 2026, Arm Limited and Contributors. All rights reserved.*
