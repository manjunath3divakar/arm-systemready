LICENSE = "CLOSED"
inherit systemd
DEPENDS = "ebbr-sct"
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "acs_run-before-login-prompt.service"

SRC_URI:append = " file://acs_run-before-login-prompt.service \
                   file://init.sh \
                   file://secure_init.sh \
                   file://verify_tpm_measurements.py \
                   file://extract_capsule_fw_version.py \
                   file://ethtool-test.py \
                   file://read_write_check_blk_devices.py \
                   file://device_driver_info.sh \
                   file://log_parser \
                 "

FILES:${PN} += "${systemd_unitdir}/system"
RDEPENDS:${PN} += "bash"

do_install:append() {
  echo "S is ${S}"
  install -d ${D}${systemd_unitdir}/system
  install -d ${D}${bindir}
  install -m 0770 ${WORKDIR}/init.sh                             ${D}${bindir}
  install -m 0770 ${WORKDIR}/../../ebbr-sct/1.0/bbr-acs/ebbr/config/ir_bbr_fwts_tests.ini ${D}${bindir}
  install -m 0770 ${WORKDIR}/secure_init.sh                      ${D}${bindir}
  install -m 0770 ${WORKDIR}/../../ebbr-sct/1.0/bbr-acs/bbsr/config/bbsr_fwts_tests.ini   ${D}${bindir}
  install -m 0644 ${WORKDIR}/acs_run-before-login-prompt.service ${D}${systemd_unitdir}/system
  install -m 0770 ${WORKDIR}/verify_tpm_measurements.py          ${D}${bindir}
  install -m 0770 ${WORKDIR}/extract_capsule_fw_version.py       ${D}${bindir}
  install -m 0770 ${WORKDIR}/ethtool-test.py                     ${D}${bindir}
  install -m 0770 ${WORKDIR}/read_write_check_blk_devices.py     ${D}${bindir}
  install -m 0770 ${WORKDIR}/device_driver_info.sh               ${D}${bindir}
  cp -r ${WORKDIR}/log_parser                                    ${D}${bindir}/
}
