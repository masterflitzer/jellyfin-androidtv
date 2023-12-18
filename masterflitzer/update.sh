#!/usr/bin/env bash

set -o errexit
set -o errtrace
set -o nounset
set -o pipefail
shopt -s inherit_errexit

git switch main

set +o errexit
git remote show | grep -Fq origin
set -o errexit
if test "${?}" -eq 0
then
	git remote set-url origin git@github.com:masterflitzer/jellyfin-androidtv.git
else
	git remote add origin git@github.com:masterflitzer/jellyfin-androidtv.git
fi

set +o errexit
git remote show | grep -Fq upstream
set -o errexit
if test "${?}" -eq 0
then
	git remote set-url upstream git@github.com:jellyfin/jellyfin-androidtv.git
	git remote set-branches upstream master
else
	git remote add -t master upstream git@github.com:jellyfin/jellyfin-androidtv.git
fi

git fetch --all -p -P -t -q

git reset --hard upstream/master
git commit -am "fix"
git push -u --force-with-lease --force-if-includes origin main

TAG="$(curl -Ls "https://api.github.com/repos/jellyfin/jellyfin-androidtv/releases/latest" | jq -r '.tag_name')"
git rebase --onto "${TAG}" main fix

printf "To make a new release create a new tag and push it:\n\n"
printf "\tgit tag %s-fix\n" "${TAG}"
printf "\tgit push origin %s-fix\n" "${TAG}"
