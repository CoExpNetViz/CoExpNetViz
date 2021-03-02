#!/bin/sh
set -e
pushd target
aunpack CoExpNetViz*
less CoExpNetViz*/META-INF/MANIFEST.MF
rm -rf CoExpNetViz*T
popd
