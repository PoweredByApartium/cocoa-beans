#!/bin/bash

CURRENT_VERSION="v100.0.1"
INPUT_VERSION="v101.0.0"

CURRENT_VERSION=${CURRENT_VERSION#v}
INPUT_VERSION=${INPUT_VERSION#v}

IFS='.' read -ra CURRENT <<< "$CURRENT_VERSION"
IFS='.' read -ra INPUT <<< "$INPUT_VERSION"

echo "Current version array: ${CURRENT[@]}"
echo "Input version array: ${INPUT[@]}"

echo "([ "${INPUT[0]}" == "${CURRENT[0]}" ] && [ "${INPUT[1]}" == "${CURRENT[1]}" ] && [ "${INPUT[2]}" != "${CURRENT[2]}" ])"
echo "::set-output name=is_minor::$([ "${INPUT[0]}" == "${CURRENT[0]}" ] && [ "${INPUT[1]}" == "${CURRENT[1]}" ] && [ "${INPUT[2]}" != "${CURRENT[2]}" ])"
