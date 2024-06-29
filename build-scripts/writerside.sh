#!/bin/bash

json_file="help-versions.json"
if [ "$major" = "true" ]; then
    # For major updates
    new_element=$(cat <<EOF
{
 "version": "$version",
 "url": "docs_$version",
 "isCurrent": true
}
EOF
)
    
    updated_json=$(jq --argjson new "$new_element" '
        map(.isCurrent = false) + [$new]
    ' "$json_file")
else
    updated_json=$(jq --arg version "$version" '
        .[-1].version = $version |
        .[-1].url = "docs_" + $version |
        .[-1].isCurrent = true |
        .[0:-1] |= map(.isCurrent = false)
    ' "$json_file")
fi

echo "$updated_json" > "$json_file"