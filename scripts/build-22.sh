#!/bin/bash

set -e

if ! docker --version > /dev/null; then
  >&2 echo 'Must have docker installed'
  exit 1
fi

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

docker build "$SCRIPT_DIR"/build-docker-image -t jmacro-builder-22

cd "$SCRIPT_DIR"/..
git clean -xfd
docker run --rm -u gradle -v "$PWD":/src -w /src jmacro-builder-22 /bin/bash -c "source ~/.sdkman/bin/sdkman-init.sh; gradle build"

echo "Done"
