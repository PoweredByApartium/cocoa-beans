#!/bin/bash
TARGET_VERSION="$1"
if [ -z "${TARGET_VERSION}" ]; then
    echo "Error: TARGET_VERSION is not set"
    exit 1
fi

file="CHANGELOG.md"
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

# After loop, check if capturing is still true to handle the case of single version
if $capturing; then
    echo -e "$content" > CHANGELOG_temp.md
    echo "Content written to CHANGELOG_temp.md"
else
    # If capturing is false, check if the TARGET_VERSION was found
    found_version=false
    while IFS= read -r line; do
        if [[ $line == \#\#* ]]; then
            version=$(echo "$line" | sed 's/^## //')
            if [[ $version == $TARGET_VERSION ]]; then
                found_version=true
                capturing=true
                content="$line"
                break
            fi
        fi
    done < "$file"
    
    if $capturing; then
        echo -e "$content" > CHANGELOG_temp.md
        echo "Content written to CHANGELOG_temp.md"
    else
        if $found_version; then
            echo "No content found after $TARGET_VERSION in $file"
        else
            echo "Target version not found in $file"
        fi
        exit 1
    fi
fi
