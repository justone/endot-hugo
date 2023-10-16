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

In the great dark ages before the Babashkanaissance, I would usually reach for Bash[^1] to write small scripts. These scripts are the glue that fits between built-in commands and my development needs. I've accumulated [quite a few of these over the years][1], and they've greatly enhanced my ability to get things done quickly.

The problem with scripting in Bash is that while it's great for running several commands in a row, any time I wanted to do any of the following, I would have to open my web browser and go search for examples:

* Any conditional test, like for file existence or if a variable is defined
* Conditionals themselves (how many brackets? do I have to quote the variables?)
* Extract common code out into a function
* Loop over a dynamic list
* Process command line arguments and provide a `--help` output

There's also a whole class of things that are possible, but very arcane in Bash, such as maps and arrays.

Then there's the problem of code reuse. In Bash,

# Quick, self-contained scripts

Most of my scripts are of this variety. They are usually fewer than a couple dozen lines and are written in the fit of frustrated energy that occurs when there's a difference between what I need and the tools in front of me.

Here's a template of what I usually start off with:

```clojure
#!/usr/bin/env bb [1]

(ns fooscript) [2]

(when (= *file* (System/getProperty "babashka.file")) [3]
  )
```

The important pieces here are:
1. Shebang line to elevate this program into Babashka
2. The namespace declaration, ready to add any of Babashka's built-in libraries
3. The "one little trick" that allows this file to be loaded into a REPL without executing it.

After opening this file up, I start a Babashka repl with `bb nrepl-server` in a tmux split and then I connect Conjure and evaluate the file. In a couple quick moves, I've reached cruising altitude and am ready to fly through my code.

From here, it's normal Clojure development; creating small composable functions, combining them together to accomplish my task, rapidly iterating as I learn more about what I want to do.

# Larger scripts

Talk about bb-scripts and why I use it.

# Accelerating the small by packaging the large

Talk about the `lib.script.alpha` namespace and how it can make small scripts even easier to write.

I did a [talk a couple years ago][2] about using Babashka on the command line and I covered some of this same information.

[1]: https://github.com/justone/dotfiles-personal/tree/personal/bin
[2]: https://www.youtube.com/watch?v=tc4ROCJYbm0

[^1]: Quite a bit earlier in my career, the language would have been Perl, but that knowledge left my brain several years ago.
