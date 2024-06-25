git fetch origin

git checkout gh-pages
rm -rf docs/
git add .
git commit -m "."
git push origin gh-pages

git checkout release-ci