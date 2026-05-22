#!/usr/bin/env bash
set -euo pipefail

release_version="${1:?release version argument is required}"

if [[ ! "$release_version" =~ ^([0-9]+)\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "Release version must match MAJOR.MINOR.PATCH, got: ${release_version}" >&2
  exit 2
fi

major="${BASH_REMATCH[1]}"
minor="${BASH_REMATCH[2]}"
patch="${BASH_REMATCH[3]}"
next_patch=$((10#$patch + 1))

printf "%s.%s.%s-SNAPSHOT\n" "$major" "$minor" "$next_patch"
