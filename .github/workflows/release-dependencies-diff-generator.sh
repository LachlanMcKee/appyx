#!/bin/bash -el
# Forked from https://github.com/usefulness/dependency-tree-diff-action/blob/master/entrypoint.sh
# This version allows running a different dependency task which is necessary when mixing java/android modules.

wget "https://github.com/JakeWharton/dependency-tree-diff/releases/download/$INPUT_VERSION/dependency-tree-diff.jar" -q -O dependency-tree-diff.jar

cd "$INPUT_BUILD_ROOT_DIR"

# Git is in a detached state, so to get back to this branch, we must give it a name.
git checkout -b pr-branch

# Switch to base ref and determine the dependencies
git fetch --force origin "$INPUT_BASEREF":"$INPUT_BASEREF" --no-tags
git switch --force "$INPUT_BASEREF"
./gradlew releaseDependenciesCreateFiles --directoryName=baseline-dependencies

# Determine the dependencies of the PR branch.
git switch --force pr-branch
./gradlew releaseDependenciesCreateFiles --directoryName=dependencies

./gradlew releaseDependenciesDiffFiles \
  --baselineDependenciesDirectoryName=baseline-dependencies \
  --dependenciesDirectoryName=dependencies
