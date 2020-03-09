#!/bin/bash

# Copy over content from old blog
mkdir -p static
cp -a ../endot.org/source/_posts/ content/posts
cp -a ../endot.org/source/notes/ content/notes
cp -a ../endot.org/source/uploads/ static/uploads
git add content/posts content/notes static/uploads
git commit -m 'Initial copy from old blog'

# Remove note index, it gets in the way
git rm content/notes/index.markdown
git commit -m 'Remove notes index'

# Copy over about and projects pages
cat ../endot.org/source/about/index.markdown | grep -v "date:" | grep -v "author:" > content/about.md
git add content/about.md
cat ../endot.org/source/projects/index.markdown | grep -v "date:" | grep -v "author:" > content/projects.md
git add content/projects.md
git commit -m 'Port over about and projects'

# Fix markdown files
clojure -m cleanup
git add content/posts content/notes static/uploads
git commit -m 'After cleanup run'
