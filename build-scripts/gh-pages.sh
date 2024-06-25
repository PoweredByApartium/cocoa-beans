git fetch origin

git checkout gh-pages
git rm -rf docs/
git commit -m "."
git push origin gh-pages

git checkout release-ci