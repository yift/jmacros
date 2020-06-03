#!/bin/bash

build_dir=$(dirname "$0")/../build
rm -rf $build_dir/toupload
mkdir -p $build_dir/toupload
cp -r $build_dir/libs $build_dir/toupload
cp -r $build_dir/reports $build_dir/toupload
cp -r $build_dir/docs $build_dir/toupload
cp $build_dir/../src/main/html/index.html $build_dir/toupload

export GEM_HOME="$HOME/.gems"
gem install net-sftp

ruby $(dirname "$0")/upload_dir.rb $build_dir/toupload/
