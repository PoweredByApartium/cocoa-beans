#!/bin/bash

set -e

SETTINGS_FILE="settings.gradle.kts"

if [ ! -f "$SETTINGS_FILE" ]; then
  echo "Error: $SETTINGS_FILE not found."
  exit 1
fi

modules=$(grep -Eo 'include\("([^"]+)"\)' "$SETTINGS_FILE" \
  | sed -E 's/include\("([^"]+)"\)/\1/')

report_paths=$(echo "$modules" \
  | sed 's/:/\//g' \
  | awk '{print "**/" $1 "/build/test-results/test/TEST-*.xml"}')

check_names=$(echo "$modules" \
  | sed 's/:/-/g')

echo "report_paths<<EOF" >> "$GITHUB_OUTPUT"
echo "$report_paths" >> "$GITHUB_OUTPUT"
echo "EOF" >> "$GITHUB_OUTPUT"

echo "check_names<<EOF" >> "$GITHUB_OUTPUT"
echo "$check_names" >> "$GITHUB_OUTPUT"
echo "EOF" >> "$GITHUB_OUTPUT"
