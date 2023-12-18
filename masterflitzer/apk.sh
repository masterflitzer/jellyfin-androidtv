#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail
shopt -s inherit_errexit

SCRIPT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

set -o allexport
. "${SCRIPT_ROOT}/.env"
set +o allexport

export ANDROID_HOME="${ANDROID_HOME}"
export JAVA_HOME="${JAVA_HOME}"
export KEYSTORE="${KEYSTORE}"
export KEYSTORE_ALIAS="${KEYSTORE_ALIAS}"
export KEYSTORE_PASS="${KEYSTORE_PASS}"

ANDROID_SDK_BUILD_TOOLS_VERSION="$(find "${ANDROID_HOME}/build-tools" -mindepth 1 -maxdepth 1 -type d -exec basename {} \; | sort -n | tail -1)"
export PATH="${PATH}:${JAVA_HOME}/bin:${ANDROID_HOME}/build-tools/${ANDROID_SDK_BUILD_TOOLS_VERSION}"

export JELLYFIN_VERSION="$(curl -Ls "https://api.github.com/repos/jellyfin/jellyfin-androidtv/releases/latest" | jq -r '.tag_name')-fix"
./gradlew assembleRelease
APK_UNSIGNED="$(find app/build/outputs/apk/release -type f -iname '*-release-unsigned.apk' -print -quit)"
APK_ALIGNED="${APK_UNSIGNED//unsigned/aligned}"
APK_SIGNED="${APK_ALIGNED//aligned/signed}"
zipalign -p -f 4 "${APK_UNSIGNED}" "${APK_ALIGNED}"
zipalign -c 4 "${APK_ALIGNED}"
apksigner sign --in "${APK_ALIGNED}" --out "${APK_SIGNED}" --ks "${KEYSTORE}" --ks-key-alias "${KEYSTORE_ALIAS}" --ks-pass "env:KEYSTORE_PASS" --key-pass "env:KEYSTORE_PASS"
apksigner verify -v --v4-signature-file "${APK_SIGNED}.idsig" "${APK_SIGNED}"

printf "\n\n"
find app/build/outputs/apk/release -type f | sort
