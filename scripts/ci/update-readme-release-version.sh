#!/usr/bin/env bash
set -euo pipefail

release_version="${1:?release version argument is required}"
readme_root="${SLOP4J_README_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)}"

if [[ ! "$release_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Release version must match MAJOR.MINOR.PATCH, got: ${release_version}" >&2
  exit 2
fi

replace_installation_block() {
  local file="$1"
  local language="$2"
  local start="<!-- slop4j-installation:start -->"
  local end="<!-- slop4j-installation:end -->"
  local tmp_file
  tmp_file="$(mktemp)"

  if ! grep -Fq "$start" "$file"; then
    echo "Missing installation start marker in ${file}" >&2
    exit 2
  fi
  if ! grep -Fq "$end" "$file"; then
    echo "Missing installation end marker in ${file}" >&2
    exit 2
  fi

  awk -v start="$start" -v end="$end" -v version="$release_version" -v language="$language" '
    function print_block() {
      if (language == "de") {
        print "Aktuell veröffentlichte Version: `" version "`."
        print ""
        print "Für die Core-Bibliothek:"
      } else {
        print "Latest published version: `" version "`."
        print ""
        print "For the core library:"
      }
      print ""
      print "```xml"
      print "<dependency>"
      print "    <groupId>dev.feit</groupId>"
      print "    <artifactId>slop4j-core</artifactId>"
      print "    <version>" version "</version>"
      print "</dependency>"
      print "```"
      print ""
      if (language == "de") {
        print "Für AssertJ-Assertions in Tests:"
      } else {
        print "For AssertJ assertions in tests:"
      }
      print ""
      print "```xml"
      print "<dependency>"
      print "    <groupId>dev.feit</groupId>"
      print "    <artifactId>slop4j-assertj</artifactId>"
      print "    <version>" version "</version>"
      print "    <scope>test</scope>"
      print "</dependency>"
      print "```"
      print ""
      if (language == "de") {
        print "Für Maven-Build-Audits:"
      } else {
        print "For Maven build audits:"
      }
      print ""
      print "```xml"
      print "<plugin>"
      print "    <groupId>dev.feit</groupId>"
      print "    <artifactId>slop4j-maven-plugin</artifactId>"
      print "    <version>" version "</version>"
      print "</plugin>"
      print "```"
    }
    $0 == start {
      print
      print_block()
      in_block = 1
      next
    }
    $0 == end {
      in_block = 0
      print
      next
    }
    !in_block {
      print
    }
  ' "$file" >"$tmp_file"

  mv "$tmp_file" "$file"
}

replace_inline_versions() {
  local file="$1"
  local tmp_file
  tmp_file="$(mktemp)"

  awk -v version="$release_version" '
    /<!-- slop4j-release-version -->/ {
      sub(/<version>[^<]+<\/version><!-- slop4j-release-version -->/, "<version>" version "</version><!-- slop4j-release-version -->")
    }
    { print }
  ' "$file" >"$tmp_file"

  mv "$tmp_file" "$file"
}

readme_en="${readme_root}/README.md"
readme_de="${readme_root}/README_DE.md"

replace_installation_block "$readme_en" "en"
replace_installation_block "$readme_de" "de"
replace_inline_versions "$readme_en"
replace_inline_versions "$readme_de"

echo "Updated README release snippets to ${release_version}."
