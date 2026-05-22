#!/usr/bin/env bash
set -euo pipefail

release_version="${1:?release version argument is required}"

if [[ ! "$release_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "Release tag must match MAJOR.MINOR.PATCH, got: ${release_version}" >&2
  exit 2
fi

if [[ "${CI_COMMIT_TAG:-}" != "$release_version" ]]; then
  echo "CI_COMMIT_TAG does not match release version argument." >&2
  exit 2
fi

if [[ -z "${CI_DEFAULT_BRANCH:-}" ]]; then
  echo "CI_DEFAULT_BRANCH is required." >&2
  exit 2
fi

git fetch origin "$CI_DEFAULT_BRANCH"

tag_sha="$(git rev-parse HEAD)"
branch_sha="$(git rev-parse "origin/${CI_DEFAULT_BRANCH}")"

if [[ "$tag_sha" != "$branch_sha" ]]; then
  echo "Tag commit ${tag_sha} is not the current ${CI_DEFAULT_BRANCH} head ${branch_sha}." >&2
  echo "Create release tags from the current default branch head before publishing." >&2
  exit 2
fi

echo "Release tag ${release_version} is valid and points at ${CI_DEFAULT_BRANCH} head."
