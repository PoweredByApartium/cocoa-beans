#!/bin/bash

vars="Writerside/v.list"
writerside="Writerside/writerside.cfg"

sed -i 's/\(<instance.*version="\)[^"]*"/\1'"$new_version"'"/' "$writerside"
sed -i "s/\(<var name=\"version\" value=\"\)[^\"]*\(\"\/>\)/\1$new_version\2/" "$vars"

git add Writerside/writerside.cfg
git add Writerside/v.list
git commit -m "Update writerside.cfg and v.list" || git diff --staged --quiet
git push origin $branch || echo "No changes to push"