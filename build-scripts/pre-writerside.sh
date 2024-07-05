#!/bin/bash

new_version="snapshot"
input_file="Writerside/v.list"
sed -i "s/\(<var name=\"version\" value=\"\)[^\"]*\(\"\/>\)/\1$new_version\2/" "input_file"

git add .
git commit -m "Update writerside.cfg and home.md" || git diff --staged --quiet
git push origin main || echo "No changes to push"