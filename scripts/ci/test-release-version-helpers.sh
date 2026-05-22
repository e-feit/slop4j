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

assert_next_snapshot "2.5.1" "2.5.2-SNAPSHOT"
assert_next_snapshot "0.0.9" "0.0.10-SNAPSHOT"
assert_next_snapshot "10.20.30" "10.20.31-SNAPSHOT"

assert_rejects "v2.5.1"
assert_rejects "2.5"
assert_rejects "2.5.1-SNAPSHOT"
assert_rejects "2.5.1.0"

echo "Version helper tests passed."
