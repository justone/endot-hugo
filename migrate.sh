#!/bin/bash

# Copy over content from old blog
mkdir -p static
cp -a ../endot.org/source/_posts/ content/post
cp -a ../endot.org/source/notes/ content/note
cp -a ../endot.org/source/uploads/ static/uploads
rm -f content/post/2014-11-08-talk-notes-mid-2014.markdown
git add content/post content/note static/uploads
git commit -m 'Initial copy from old blog'

# Remove note index, it gets in the way
git rm content/note/index.markdown
git commit -m 'Remove note index'

# Copy over projects page
cat ../endot.org/source/projects/index.markdown | grep -v "date:" | grep -v "author:" > content/projects.md
git add content/projects.md
git commit -m 'Port over about and projects'

# Fix markdown files
clojure -m cleanup
git add content/post content/note static/uploads
git commit -m 'After cleanup run'

# Make content listings plural
cat > content/post/_index.md <<EOF1
---
url: posts
---
EOF1
cat > content/note/_index.md <<EOF1
---
url: notes
---
EOF1
git add content/post/_index.md content/note/_index.md
git commit -m 'Add plural listings pages'
