#!/usr/bin/env bash
set -euo pipefail

settings_file="${1:?settings file argument is required}"
settings_dir="$(dirname "$settings_file")"

mkdir -p "$settings_dir"

cat >"$settings_file" <<'XML'
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>unused</username>
      <password>unused</password>
    </server>
  </servers>
</settings>
XML

echo "Created Maven Central draft settings at ${settings_file}."
