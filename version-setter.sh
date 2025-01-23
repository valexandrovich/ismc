#!/bin/bash

# Function to display usage
usage() {
  echo "Usage: $0 <new_version>   //  UPDATE ALL MODULES VERSION"
  echo "       $0 -l              // SHOW CURRENT VERSIONS"
  exit 1
}

# Function to update version in a given pom.xml file
update_version_pom() {
  local pom_file=$1
  if [ -f "$pom_file" ]; then
    # Create a temporary file
    tmp_file=$(mktemp)
    sed -e "/<groupId>ua.com.valexa<\/groupId>/,/<\/version>/ s|<version>.*<\/version>|<version>${NEW_VERSION}<\/version>|" "$pom_file" > "$tmp_file"
    mv "$tmp_file" "$pom_file"
    echo "Version updated to ${NEW_VERSION} in ${pom_file}"
  else
    echo "${pom_file} not found."
  fi
}

# Function to update version in a given package.json file
update_version_package_json() {
  local package_json_file=$1
  if [ -f "$package_json_file" ]; then
    # Create a temporary file
    tmp_file=$(mktemp)
    sed -e "s|\"version\": \".*\"|\"version\": \"${NEW_VERSION}\"|" "$package_json_file" > "$tmp_file"
    mv "$tmp_file" "$package_json_file"
    echo "Version updated to ${NEW_VERSION} in ${package_json_file}"
  else
    echo "${package_json_file} not found."
  fi
}

# Function to list current version in a given pom.xml file
list_version_pom() {
  local pom_file=$1
  if [ -f "$pom_file" ]; then
    local current_version=$(sed -n -e "/<groupId>ua.com.valexa<\/groupId>/,/<\/version>/ s|.*<version>\(.*\)</version>|\1|p" "$pom_file" | head -n 1)
    echo "Current version in ${pom_file}: ${current_version}"
  else
    echo "${pom_file} not found."
  fi
}

# Function to list current version in a given package.json file
list_version_package_json() {
  local package_json_file=$1
  if [ -f "$package_json_file" ]; then
    local current_version=$(sed -n 's|.*"version": "\(.*\)".*|\1|p' "$package_json_file")
    echo "${package_json_file}: ${current_version}"
  else
    echo "${package_json_file} not found."
  fi
}

if [ "$1" == "-l" ]; then
  # List current versions
  list_version_pom "pom.xml"
  list_version_pom "api-gateway/pom.xml"
  list_version_pom "api-server/pom.xml"
  list_version_pom "afs-common/pom.xml"
  list_version_pom "cpms/pom.xml"
  list_version_pom "downloader/pom.xml"
  list_version_pom "importer/pom.xml"
  list_version_pom "enricher/pom.xml"
  list_version_pom "migrations/pom.xml"
  list_version_pom "scheduler/pom.xml"
  list_version_pom "sandbox/pom.xml"
  list_version_pom "uploader/pom.xml"
  list_version_package_json "web-frontend/package.json"
  list_version_package_json "web-app-vue/package.json"
  exit 0
fi

# Check if a version argument is provided
if [ -z "$1" ]; then
  usage
fi

NEW_VERSION=$1

# Update version in root pom
update_version_pom "pom.xml"
# Update version in child poms
update_version_pom "api-gateway/pom.xml"
update_version_pom "api-server/pom.xml"
update_version_pom "afs-common/pom.xml"
update_version_pom "cpms/pom.xml"
update_version_pom "downloader/pom.xml"
update_version_pom "importer/pom.xml"
update_version_pom "enricher/pom.xml"
update_version_pom "migrations/pom.xml"
update_version_pom "scheduler/pom.xml"
update_version_pom "sandbox/pom.xml"
update_version_pom "uploader/pom.xml"

# Update version in web-frontend/package.json
update_version_package_json "web-frontend/package.json"
update_version_package_json "web-app-vue/package.json"