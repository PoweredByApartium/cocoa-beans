#!/bin/bash

new_version="test"

input_file="Writerside/writerside.cfg"
sed -i 's/\(<instance.*version="\)[^"]*\("/\1v'"$new_version"'\2/' "$input_file"
git add .
git commit -m "."
git push origin release-ci