#!/bin/bash

input_file="Writerside/writerside.cfg"
sed -i 's/\(<instance.*version="\)[^"]*"/\1'"$new_version"'"/' "$input_file"

sed -i 's|https://cocoa-beans.apartium.net/[^/]*/spigot/|https://cocoa-beans.apartium.net/'"$new_version"'/spigot/|' "Writerside/topics/🫘 Home.md"
sed -i 's|https://cocoa-beans.apartium.net/[^/]*/common/|https://cocoa-beans.apartium.net/'"$new_version"'/common/|' "Writerside/topics/🫘 Home.md"
sed -i 's|https://cocoa-beans.apartium.net/[^/]*/commands/|https://cocoa-beans.apartium.net/'"$new_version"'/commands/|' "Writerside/topics/🫘 Home

git add .
git commit -m "Update writerside.cfg and home.md"
git push origin release-ci