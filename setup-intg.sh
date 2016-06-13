#!/usr/bin/env bash
# Setup for integration tests
# create symlink for download heavy files/directories
# tested only on Mac OSX

mkdir -p ~/.gradle-testkit/caches/modules-2
ln -s ~/.gradle/appengine-sdk/ ~/.gradle-testkit/
ln -s ~/.gradle/caches/modules-2/files-2.1/ ~/.gradle-testkit/caches/modules-2/
