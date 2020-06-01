#!/bin/bash

if ! javac -version > /dev/null; then
  >&2 echo 'Must have java development kit installed'
  exit 1
fi
if gradle -v > /dev/null; then
  gradle=$(which gradle)
else
  echo "No gradle installed, downloading..."
  gradle_version='6.4.1'
  echo 'Installing gradle...'
  zip=$(mktemp --suffix .zip)
  gradle_dir=$(mktemp -d)
  cd $gradle_dir/
  curl -o $zip -L -s "https://services.gradle.org/distributions/gradle-${gradle_version}-bin.zip"
  jar xvf $zip
  gradle="${gradle_dir}/gradle-${gradle_version}/bin/gradle"
  chmod +x ${gradle}
  cd -
fi

${gradle}  --no-daemon build

if [ -z "$gradle_dir" ]; then
  rm -rf $gradle_dir
  rm -rf $zip
fi
