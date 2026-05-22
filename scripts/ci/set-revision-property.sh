#!/usr/bin/env bash
set -euo pipefail

next_snapshot="${1:?next snapshot version argument is required}"

if [[ ! "$next_snapshot" =~ ^[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT$ ]]; then
  echo "Next snapshot version must match MAJOR.MINOR.PATCH-SNAPSHOT, got: ${next_snapshot}" >&2
  exit 2
fi

mvn --batch-mode org.codehaus.mojo:versions-maven-plugin:2.19.1:set-property \
  -Dproperty=revision \
  -DnewVersion="$next_snapshot" \
  -DgenerateBackupPoms=false
