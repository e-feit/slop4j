#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${MAVEN_GPG_PRIVATE_KEY:-}" ]]; then
  echo "MAVEN_GPG_PRIVATE_KEY is required." >&2
  exit 2
fi

if ! command -v gpg >/dev/null 2>&1; then
  echo "gpg is required for Maven Central artifact signing." >&2
  exit 2
fi

export GNUPGHOME="${GNUPGHOME:-${CI_PROJECT_DIR:-$PWD}/.gnupg}"
mkdir -p "$GNUPGHOME"
chmod 700 "$GNUPGHOME"

printf "%s" "$MAVEN_GPG_PRIVATE_KEY" | gpg --batch --import

if [[ -n "${MAVEN_GPG_KEY_ID:-}" ]]; then
  gpg --batch --list-secret-keys "$MAVEN_GPG_KEY_ID" >/dev/null
else
  gpg --batch --list-secret-keys >/dev/null
fi

echo "Imported Maven Central signing key into ${GNUPGHOME}."
