#!/bin/bash

vars="Writerside/v.list"
writerside="Writerside/writerside.cfg"

sed -i 's/\(<instance.*version="\)[^"]*"/\1'"$new_version"'"/' "$writerside"
sed -i "s/\(<var name=\"version\" value=\"\)[^\"]*\(\"\/>\)/\1$new_version\2/" "$vars"

git add .
git commit -m "Update writerside.cfg and home.md" || git diff --staged --quiet
git push origin $branch || echo "No changes to push"