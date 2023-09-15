---
title: "Developing Babashka scripts"
date: 2023-09-15T19:55:14-07:00
slug: bb-scripts
draft: true
tags:
- programming
- clojure
- babashka
---

Babashka is amazing. It's replaced 99.999% of my Bash usage. The only time I reach for Bash these days is when I need a few commands (sometimes lightly templated) to run in a row and I don't want to leverage my history too much. Everything else is Babashka.

I thought it might be useful to write up my approach to writing Babashka scripts and compare it to how I used to write Bash.

# The time "before"

I've always been a great fan of the terminal and it's expressive power. It's the [original programming environment][1], and it's power has only grown. I tend to avoid graphical applications or web sites where I can get the same information at my prompt. I constantly look for tools to enhance my experience at the command line.

In the great dark ages before the Babashkanaissance, I would usually reach for Bash[^1] to write small scripts.

# Quick, self-contained scripts

Most of my scripts are of this variety. They are usually fewer than a couple dozen lines and are written in the fit of frustrated energy that occurs when there's a difference between what I need and the tools in front of me.

#

I did a [talk a couple years ago][1] about using Babashka on the command line and I covered some of this same information.

[1]: https://www.youtube.com/watch?v=tc4ROCJYbm0

[^1]: Quite a bit earlier in my career, the language would have been Perl, but that knowledge left my brain several years ago.
