#!/usr/bin/env bash
set -euo pipefail

mapfile -t bundles < <(find . -path "*/target/central-publishing/*.zip" -type f | sort)

if [[ "${#bundles[@]}" -eq 0 ]]; then
  echo "No Central publishing bundle was generated." >&2
  exit 1
fi

if [[ "${#bundles[@]}" -gt 1 ]]; then
  echo "Expected one Central publishing bundle, found ${#bundles[@]}:" >&2
  printf "  %s\n" "${bundles[@]}" >&2
  exit 1
fi

bundle="${bundles[0]}"

echo "Central publishing draft bundle:"
echo "$bundle"
echo
echo "Bundle contents:"
jar tf "$bundle" | sort
