# SystemReady Band ACS Automation Flow

## Overview

This document explains the complete automation flow of the **Arm SystemReady Band ACS** image.

The SystemReady Band ACS image is a bootable validation environment used to run firmware, UEFI, Linux, architecture, and compliance test suites on Arm SystemReady platforms.

The automation flow covers:

- Image validations
- SystemReady Band ACS Automation Flow
- GRUB Boot Menu Options

---

## What the SR Image Validates

| Validation Area | Tools / Test Suites |
|---|---|
| UEFI firmware compliance | SCT, SCRT, BBR |
| Base system architecture | BSA |
| Server architecture | SBSA |
| Firmware behavior | FWTS |
| Secure Boot compliance | BBSR |
| Manageability checks | SBMR |
| Linux-side validation | Linux scripts and test tools |
| Result reporting | ACS log parser and waiver flow |

---

## SystemReady Band ACS Automation Flow

This section explains the end-to-end automation flow for the SystemReady Band ACS image.

The flow is divided into two parts:

1. **Build Automation Flow** — how the ACS image is prepared and generated.
2. **Run Automation Flow** — what happens when the ACS image boots on the platform.

---
### SR Build Automation Flow

Commands executed from **arm-systemready/SystemReady-band/**:

```text
./build-scripts/get_source.sh
./build-scripts/build-systemready-band-live-image.sh
```

```mermaid
%%{init: {
  "theme": "base",
  "themeVariables": {
    "fontFamily": "Arial",
    "fontSize": "14px",
    "primaryBorderColor": "#0f172a",
    "lineColor": "#2563eb",
    "tertiaryColor": "#ffffff"
  }
}}%%

flowchart TD

    linkStyle default stroke:#2563eb,stroke-width:4px;

    Start((Start)) --> A["Start SR build flow"]
    A --> B["Run get_source.sh"]
    B --> C["Fetch ACS and dependent sources"]
    C --> D["Prepare common configs and scripts"]
    D --> E["Apply required patches"]

    E --> F["Run build-all.sh"]

    F --> G1["Build UEFI components"]
    F --> G2["Build Linux kernel"]
    F --> G3["Build Buildroot ramdisk"]
    F --> G4["Build ACS test binaries"]
    F --> G5["Build parser/helper tools"]

    G1 --> H["Package SR ACS image"]
    G2 --> H
    G3 --> H
    G4 --> H
    G5 --> H

    H --> I["Add EFI boot files"]
    I --> J["Add ACS test content"]
    J --> K["Add Linux Image and ramdisk"]
    K --> L["Add config and result directories"]
    L --> M["Generate compressed SR ACS image"]
    M --> End((End))

    classDef startEnd fill:#ffffff,stroke:#0f172a,stroke-width:3px,color:#0f172a;
    classDef source fill:#dbeafe,stroke:#1d4ed8,stroke-width:3px,color:#0f172a;
    classDef build fill:#ffedd5,stroke:#ea580c,stroke-width:3px,color:#0f172a;
    classDef package fill:#dcfce7,stroke:#16a34a,stroke-width:3px,color:#0f172a;
    classDef output fill:#ede9fe,stroke:#7c3aed,stroke-width:3px,color:#0f172a;

    class Start,End startEnd;
    class A,B,C,D,E source;
    class F,G1,G2,G3,G4,G5 build;
    class H,I,J,K,L package;
    class M output;
```

---
### SR Runtime Automation Flow

> **Reboot handling:** Some UEFI test suites reset the platform after execution.  
> After each reset, the platform returns to GRUB and the automation resumes from `startup.nsh`.  
> Already-completed suites are skipped or not re-run because their result logs are present.

```mermaid
%%{init: {
  "theme": "base",
  "themeVariables": {
    "fontFamily": "Arial",
    "fontSize": "15px",
    "primaryBorderColor": "#0f172a",
    "lineColor": "#2563eb"
  }
}}%%

flowchart TD

    Start((Start)) --> Boot["Power on / Reset platform"]
    Boot --> Grub["GRUB menu"]
    Grub --> Choice{"Select boot path"}

    Choice -->|"SystemReady ACS Automation"| ACS
    Choice -->|"Linux Boot"| Linux
    Choice -->|"BBSR Compliance Automation"| BBSR
    Choice -->|"Execution Environment"| Manual

    ACS["SystemReady ACS UEFI phase<br/><br/>startup.nsh<br/>Parser.efi<br/>SCT / BBR<br/>SCRT<br/>Capsule dump<br/>UEFI debug dump<br/>BSA / SBSA UEFI"]
    ACS --> RebootCheck{"UEFI suite<br/>triggered reset?"}
    RebootCheck -->|"yes"| Reboot["Reset / reboot<br/>resume from GRUB"]
    Reboot -.-> Grub
    RebootCheck -->|"no"| Linux

    Linux["Linux runtime phase<br/><br/>ACS Linux kernel<br/>Buildroot ramdisk<br/>init.sh<br/>Linux debug dump<br/>FWTS<br/>SBMR / BSA / SBSA Linux"]
    Linux --> Results

    BBSR["BBSR compliance phase<br/><br/>bbsr_startup.nsh<br/>Secure Boot check<br/>Key provisioning if required<br/>BBSR UEFI / SCT flow<br/>Secure Linux boot<br/>secure_init.sh"]
    BBSR --> BbsrReset{"Keys provisioned?"}
    BbsrReset -->|"yes, reboot required"| Reboot
    BbsrReset -->|"no / already enabled"| Results

    Manual["Manual execution environment<br/><br/>Run selected tests manually"]
    Manual --> End

    Results["Result processing<br/><br/>EDK2 test parser<br/>SystemReady post scripts<br/>ACS log parser<br/>Waivers<br/>acs_results / acs_summary"]
    Results --> End((End))

    classDef startEnd fill:#ffffff,stroke:#0f172a,stroke-width:3px,color:#0f172a;
    classDef boot fill:#dbeafe,stroke:#1d4ed8,stroke-width:3px,color:#0f172a;
    classDef acs fill:#ffedd5,stroke:#ea580c,stroke-width:3px,color:#0f172a;
    classDef linux fill:#dcfce7,stroke:#16a34a,stroke-width:3px,color:#0f172a;
    classDef bbsr fill:#fef3c7,stroke:#d97706,stroke-width:3px,color:#0f172a;
    classDef result fill:#ede9fe,stroke:#7c3aed,stroke-width:3px,color:#0f172a;
    classDef reboot fill:#fee2e2,stroke:#dc2626,stroke-width:3px,color:#0f172a;
    classDef manual fill:#f3f4f6,stroke:#64748b,stroke-width:3px,color:#0f172a;
    classDef decision fill:#ffffff,stroke:#2563eb,stroke-width:3px,color:#0f172a;

    class Start,End startEnd;
    class Boot,Grub,Choice boot;
    class ACS acs;
    class Linux linux;
    class BBSR bbsr;
    class Results result;
    class Reboot reboot;
    class Manual manual;
    class RebootCheck,BbsrReset decision;
```
---

## GRUB Boot Menu Options

| Boot Option | Purpose |
|---|---|
| `Linux Boot` | Boots ACS Linux environment |
| `SystemReady band ACS (Automation)` | Runs the complete automated SR compliance flow |
| `BBSR Compliance (Automation)` | Runs Secure Boot / BBSR compliance flow |
| `UEFI Execution Environment` | Provides manual UEFI shell execution environment |
| `Linux Execution Environment` | Provides manual Linux-side execution environment |
| `Linux Boot with SetVirtualAddressMap enabled` | Debug or special Linux boot option |

---

## Configuration Files

| File | Description |
|---|---|
| `acs_config.txt` | Contains ACS and specification version information |
| `acs_run_config.ini` | Enables or disables test suites and passes test arguments |
| `system_config.txt` | Contains platform details used in the final ACS report |

---

## Result Collection

ACS logs and summaries are stored under:

```text
acs_results/
```

Final parsed reports are generated under:

```text
acs_results/acs_summary/
```
