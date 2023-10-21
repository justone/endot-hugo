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

Then there's the problem of code reuse. In Bash, you can source any other file you want, but that's only possible after looking up how to find a file relative to the current script, how to make functions, and that doesn't address relying on libraries that are not on your local system.

# The time "after"

Some time in 2019, GraalVM was really starting to be a viable option for compiling Clojure programs. I remember using Borkdude's clj-kondo and jet, and then in August came the first versions of Babashka. I was instantly drawn to it and the idea of a fast-starting Clojure dialect.

I'd been a fan of Clojure for a while by then, and I really wanted to be able to use it for my command line scripts. My first attempts at this were with [planck][2] and [lumo][3], which are self-hosted ClojureScript runtimes. They satisfied the fast-startup requirement, but they bottomed out in the Javascript ecosystem and it's reliance on a single thread and call-backs. I have a few small experiments from back then, but it never caught on because it was difficult to call programs and read or write data, something that comes up rather often in command line scripts.

Babashka was different. Because it was based on the Java ecosystem, I could use spit and slurp as well as the clojure.java.io namespace to access the filesystem. I

Then, new Babashka-specific namespaces were added to make these things even easier. `babashka.process` for running other programs, `babashka.fs` for filesystem operations, `babashka.deps` for dyanmically adding dependencies, and many more. These namespaces drew Babashka closer to it's hosted ecosystem (the terminal), and made it more powerful in the process.

## Quick, self-contained scripts

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

For the most part, I rely on Babashka's built-in namespaces. In the rare times I need something else, I don't need to convert the script into a "real" project. Instead, I rely on one of two techniques.

The first is to use `babashka.deps`, like this:

```
(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.clj-commons/pretty {:mvn/version "2.2"}}})'
```

It's rather amazing that you can add any compatible library in-line like this. It's out of the realm of imagination in the old Bashiverse.

The second way I've reached out for other functionality is by including other Babashka scripts. This is one reason why I have a namespace declaration at the top (the other being that it's tidier).

For instance, I have a script called `bbts` that will take incoming EDN data and look for any map key of timestamp and convert its value into a human readable date/time. This is super useful as I'm not able to parse milliseconds directly.

```
$ cat sample.edn
{:mulog/timestamp 1697642960678}
{:mulog/data {:timestamp 1697642960678}}

$ cat sample.edn | bbts
{:mulog/timestamp "2023-10-18 8:29:20.67 AM"}
{:mulog/data {:timestamp "2023-10-18 8:29:20.67 AM"}}
```

I recently was writing another script that had timestamp data and wanted to use `bbts`'s `humanize-timestamps` function. So I added this snippet:

```
(load-file (-> *file* fs/real-path fs/parent (fs/path "bbts") fs/file))
(require '[bbts])

(comment
  (def records ...)
  (map bbts/humanize-timestamps records))
```

## Larger scripts

Talk about bb-scripts and why I use it.

# Accelerating the small by packaging the large

Talk about the `lib.script.alpha` namespace and how it can make small scripts even easier to write.

I did a [talk a couple years ago][4] about using Babashka on the command line and I covered some of this same information.

[1]: https://github.com/justone/dotfiles-personal/tree/personal/bin
[2]: https://planck-repl.org/
[3]: https://github.com/anmonteiro/lumo
[4]: https://www.youtube.com/watch?v=tc4ROCJYbm0

[^1]: Quite a bit earlier in my career, the language would have been Perl, but that knowledge left my brain several years ago.
