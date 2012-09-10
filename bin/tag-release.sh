#!/bin/bash

#
# tag-release.sh
#

# A script for tagging a release of the ImgLib/ImgLib2 projects.

set -e

msg () {
	printf '\n%s\n' "$*" >&2
}

DIR="$(dirname "$0")/.."

cd "$DIR"

old="$1"
new="$2"
imagej1="$3"
scifio="$4"

if [ -z "$old" -o -z "$new" -o -z "$imagej1" -o -z "$scifio" ];
then
	cat >&2 << EOF
Usage:
   tag-release.sh old.version new.version imagej1.version scifio.version

E.g.:
   tag-release.sh 2.0.0-SNAPSHOT 2.0.0-beta3 1.46r 4.4.0
EOF
	exit 1
fi

tag="v$new"

echo "====== Configuration ======"
echo "Old version = $old"
echo "New version = $new"
echo "ImageJ1 version = $imagej1"
echo "SCIFIO version = $scifio"
echo "Tag = $tag"

cd "$DIR"

if [ -n "$(git tag -l | grep "$tag")" ];
then
	msg "Tag '$tag' already exists. Delete it, or use a different version."
	exit 1
fi

msg '====== Updating master branch to the latest ======'
git fetch --all --tags --prune
git checkout master
git merge 'HEAD@{u}'

msg '====== Updating version numbers ======'

# update project versions
mvn -P broken versions:set -DoldVersion="$old" -DnewVersion="$new" \
	-DgenerateBackupPoms=false

# replace any remaining SNAPSHOT versions (especially in broken subtree)
set +e # grep returns non-zero when nothing matches, which kills the script
if [ -n "$(git grep -l "$old" imglib1 imglib2)" ];
then
	# NB: We cannot use the xargs "-r" flag because it is a GNU extension only.
	git grep -zl "$old" imglib1 imglib2 | xargs -0 sed -i '' -e "s/$old/$new/"
fi
set -e

# add needed properties block to toplevel POM
sed -E -i'' -e 's_(</build>)_\1\
\
	<properties>\
		<imglib1.version>${project.version}</imglib1.version>\
		<imglib2.version>${project.version}</imglib2.version>\
		<imagej1.version>'"$imagej1"'</imagej1.version>\
		<scifio.version>'"$scifio"'</scifio.version>\
	</properties>_' pom.xml

msg '====== Making release commit ======'

# create a temporary branch using "detached HEAD"
git checkout HEAD^0 > /dev/null 2>&1

msg='Release version '"$new"'

This release uses the following dependency versions:
  * SCIFIO at '"$scifio"'
  * ImageJ1 at '"$imagej1"

# do the commit
git commit . -m "$msg"

msg '====== Tagging the release ======'

# create the tag
git tag -a "$tag" -m "$msg"

msg '====== Work complete ======'
