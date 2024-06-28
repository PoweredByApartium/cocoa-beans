#!/bin/bash

if [ -z "${TARGET_VERSION}" ]; then
    echo "Error: TARGET_VERSION is not set"
    exit 1
fi

file="../CHANGELOG.md"
if [ ! -f "$file" ]; then
    echo "File not found: $file"
    exit 1
fi

capturing=false
content=""
while IFS= read -r line; do
    if [[ $line == \#\#* ]]; then
        version=$(echo "$line" | sed 's/^## //')
        if [[ $version == $TARGET_VERSION ]]; then
            capturing=true
            content="$line"
        elif $capturing; then
            break
        fi
    elif $capturing; then
        content+=$'\n'"$line"
    fi
done < "$file"

if $capturing; then
    echo -e "$content" > CHANGELOG_temp.md
    echo "Content written to CHANGELOG_temp.md"
else
    echo "Target version not found"
    exit 1