#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

assert_next_snapshot() {
  local input="$1"
  local expected="$2"
  local actual
  actual="$("${script_dir}/next-snapshot-version.sh" "$input")"
  if [[ "$actual" != "$expected" ]]; then
    echo "Expected ${input} -> ${expected}, got ${actual}" >&2
    exit 1
  fi
}

assert_rejects() {
  local input="$1"
  if "${script_dir}/next-snapshot-version.sh" "$input" >/tmp/slop4j-version-test.out 2>/tmp/slop4j-version-test.err; then
    echo "Expected ${input} to be rejected." >&2
    exit 1
  fi
}

assert_contains() {
  local file="$1"
  local expected="$2"
  if ! grep -Fq "$expected" "$file"; then
    echo "Expected ${file} to contain: ${expected}" >&2
    exit 1
  fi
}

assert_not_contains() {
  local file="$1"
  local unexpected="$2"
  if grep -Fq "$unexpected" "$file"; then
    echo "Expected ${file} not to contain: ${unexpected}" >&2
    exit 1
  fi
}

assert_settings_requires_credentials() {
  local settings_file
  settings_file="$(mktemp)"

  if env -u CENTRAL_USERNAME -u CENTRAL_PASSWORD "${script_dir}/create-central-settings.sh" "$settings_file" \
    >/tmp/slop4j-settings-test.out 2>/tmp/slop4j-settings-test.err; then
    echo "Expected Central settings creation to require credentials." >&2
    exit 1
  fi
}

assert_settings_uses_central_credentials() {
  local settings_file
  settings_file="$(mktemp)"

  CENTRAL_USERNAME="token-user" CENTRAL_PASSWORD="token-password" \
    "${script_dir}/create-central-settings.sh" "$settings_file" >/tmp/slop4j-settings-test.out

  assert_contains "$settings_file" "<id>central</id>"
  assert_contains "$settings_file" "<username>token-user</username>"
  assert_contains "$settings_file" "<password>token-password</password>"
  assert_not_contains "$settings_file" "unused"
}

assert_settings_includes_optional_gpg_passphrase() {
  local settings_file
  settings_file="$(mktemp)"

  CENTRAL_USERNAME="token-user" CENTRAL_PASSWORD="token-password" MAVEN_GPG_PASSPHRASE="secret phrase" \
    "${script_dir}/create-central-settings.sh" "$settings_file" >/tmp/slop4j-settings-test.out

  assert_contains "$settings_file" "<gpg.passphrase>secret phrase</gpg.passphrase>"
}

assert_next_snapshot "2.5.1" "2.5.2-SNAPSHOT"
assert_next_snapshot "0.0.9" "0.0.10-SNAPSHOT"
assert_next_snapshot "10.20.30" "10.20.31-SNAPSHOT"

assert_rejects "v2.5.1"
assert_rejects "2.5"
assert_rejects "2.5.1-SNAPSHOT"
assert_rejects "2.5.1.0"

assert_settings_requires_credentials
assert_settings_uses_central_credentials
assert_settings_includes_optional_gpg_passphrase

echo "Version helper tests passed."
