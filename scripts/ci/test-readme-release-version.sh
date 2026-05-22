#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
tmp_dir="$(mktemp -d)"

cleanup() {
  rm -rf "$tmp_dir"
}
trap cleanup EXIT

cat >"$tmp_dir/README.md" <<'README'
# Test

## Installation

<!-- slop4j-installation:start -->
stale
<!-- slop4j-installation:end -->

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.0.1</version><!-- slop4j-release-version -->
</plugin>
```
README

cat >"$tmp_dir/README_DE.md" <<'README'
# Test

## Installation

<!-- slop4j-installation:start -->
veraltet
<!-- slop4j-installation:end -->

```xml
<plugin>
    <groupId>dev.feit</groupId>
    <artifactId>slop4j-maven-plugin</artifactId>
    <version>0.0.1</version><!-- slop4j-release-version -->
</plugin>
```
README

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

SLOP4J_README_ROOT="$tmp_dir" "${script_dir}/update-readme-release-version.sh" "1.2.3"

assert_contains "$tmp_dir/README.md" "Latest published version: \`1.2.3\`."
assert_contains "$tmp_dir/README.md" "<artifactId>slop4j-core</artifactId>"
assert_contains "$tmp_dir/README.md" "<artifactId>slop4j-assertj</artifactId>"
assert_contains "$tmp_dir/README.md" "<artifactId>slop4j-maven-plugin</artifactId>"
assert_contains "$tmp_dir/README.md" "<version>1.2.3</version><!-- slop4j-release-version -->"
assert_not_contains "$tmp_dir/README.md" "stale"

assert_contains "$tmp_dir/README_DE.md" "Aktuell veröffentlichte Version: \`1.2.3\`."
assert_contains "$tmp_dir/README_DE.md" "<artifactId>slop4j-core</artifactId>"
assert_contains "$tmp_dir/README_DE.md" "<artifactId>slop4j-assertj</artifactId>"
assert_contains "$tmp_dir/README_DE.md" "<artifactId>slop4j-maven-plugin</artifactId>"
assert_contains "$tmp_dir/README_DE.md" "<version>1.2.3</version><!-- slop4j-release-version -->"
assert_not_contains "$tmp_dir/README_DE.md" "veraltet"

if SLOP4J_README_ROOT="$tmp_dir" "${script_dir}/update-readme-release-version.sh" "1.2-SNAPSHOT" \
  >/tmp/slop4j-readme-version-test.out 2>/tmp/slop4j-readme-version-test.err; then
  echo "Expected invalid release version to be rejected." >&2
  exit 1
fi

echo "README release version tests passed."
