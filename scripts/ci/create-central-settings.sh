#!/usr/bin/env bash
set -euo pipefail

settings_file="${1:?settings file argument is required}"
settings_dir="$(dirname "$settings_file")"

if [[ -z "${CENTRAL_USERNAME:-}" ]]; then
  echo "CENTRAL_USERNAME is required." >&2
  exit 2
fi

if [[ -z "${CENTRAL_PASSWORD:-}" ]]; then
  echo "CENTRAL_PASSWORD is required." >&2
  exit 2
fi

xml_escape() {
  local value="$1"
  value="${value//&/&amp;}"
  value="${value//</&lt;}"
  value="${value//>/&gt;}"
  value="${value//\"/&quot;}"
  value="${value//\'/&apos;}"
  printf "%s" "$value"
}

mkdir -p "$settings_dir"
umask 077

central_username="$(xml_escape "$CENTRAL_USERNAME")"
central_password="$(xml_escape "$CENTRAL_PASSWORD")"

cat >"$settings_file" <<XML
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>${central_username}</username>
      <password>${central_password}</password>
    </server>
  </servers>
XML

if [[ -n "${MAVEN_GPG_PASSPHRASE:-}" ]]; then
  gpg_passphrase="$(xml_escape "$MAVEN_GPG_PASSPHRASE")"
  cat >>"$settings_file" <<XML
  <profiles>
    <profile>
      <id>gpg-signing</id>
      <properties>
        <gpg.passphrase>${gpg_passphrase}</gpg.passphrase>
      </properties>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>gpg-signing</activeProfile>
  </activeProfiles>
XML
fi

cat >>"$settings_file" <<'XML'
</settings>
XML

echo "Created Maven Central settings at ${settings_file}."
