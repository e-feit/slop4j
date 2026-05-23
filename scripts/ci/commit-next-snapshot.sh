#!/usr/bin/env bash
set -euo pipefail

next_snapshot="${1:?next snapshot version argument is required}"

if [[ ! "$next_snapshot" =~ ^[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT$ ]]; then
  echo "Next snapshot version must match MAJOR.MINOR.PATCH-SNAPSHOT, got: ${next_snapshot}" >&2
  exit 2
fi

if [[ -z "${CI_DEFAULT_BRANCH:-}" ]]; then
  echo "CI_DEFAULT_BRANCH is required." >&2
  exit 2
fi

if [[ -z "${CI_SERVER_HOST:-}" || -z "${CI_PROJECT_PATH:-}" ]]; then
  echo "CI_SERVER_HOST and CI_PROJECT_PATH are required." >&2
  exit 2
fi

release_bot_name="${GITLAB_RELEASE_BOT_NAME:-slop4j release automation}"
release_bot_email="${GITLAB_RELEASE_BOT_EMAIL:-release-bot@users.noreply.gitlab.com}"

git add pom.xml README.md README_DE.md

if git diff --cached --quiet; then
  echo "No revision bump to commit."
  exit 0
fi

GIT_AUTHOR_NAME="${release_bot_name}" \
  GIT_AUTHOR_EMAIL="${release_bot_email}" \
  GIT_COMMITTER_NAME="${release_bot_name}" \
  GIT_COMMITTER_EMAIL="${release_bot_email}" \
  git commit -m "chore: update release metadata for ${next_snapshot} [skip ci]"

if [[ "${RELEASE_BUMP_DRY_RUN:-false}" == "true" ]]; then
  echo "RELEASE_BUMP_DRY_RUN=true, skipping push."
  exit 0
fi

if [[ -n "${GITLAB_PUSH_TOKEN:-}" ]]; then
  git remote set-url origin "https://oauth2:${GITLAB_PUSH_TOKEN}@${CI_SERVER_HOST}/${CI_PROJECT_PATH}.git"
elif [[ "${USE_CI_JOB_TOKEN_PUSH:-false}" == "true" ]]; then
  if [[ -z "${CI_JOB_TOKEN:-}" ]]; then
    echo "CI_JOB_TOKEN is required when USE_CI_JOB_TOKEN_PUSH=true." >&2
    exit 2
  fi
  git remote set-url origin "https://gitlab-ci-token:${CI_JOB_TOKEN}@${CI_SERVER_HOST}/${CI_PROJECT_PATH}.git"
else
  echo "No push authentication configured." >&2
  echo "Set GITLAB_PUSH_TOKEN, or enable GitLab job token repository pushes and set USE_CI_JOB_TOKEN_PUSH=true." >&2
  exit 2
fi

git push origin "HEAD:${CI_DEFAULT_BRANCH}"
