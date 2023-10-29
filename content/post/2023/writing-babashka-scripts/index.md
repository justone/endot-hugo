---
title: "Developing Babashka Scripts"
date: 2023-10-27T12:55:14-07:00
slug: writing-babashka-scripts
tags:
- programming
- clojure
- babashka
---

Babashka is amazing. It's replaced 99.999% of my Bash usage. The only time I reach for Bash these days is when I need a few commands (sometimes lightly templated) to run in a row and I don't want to leverage my history too much. Everything else is Babashka.

I thought it might be useful to describe my approach to writing Babashka scripts and compare it to how I used to write Bash.

# Bashing scripts

In the time before the Babashkanaissance, I would usually reach for Bash[^1] to write small automation scripts. These scripts are the glue that fits between built-in commands and my development needs. I've accumulated quite a few of these over the years, and they've greatly enhanced my ability to get things done quickly.

The best thing about Bash is that it's quick to get started. Throw a few commands in a file and you have something useful. For me, the problem with Bash was that any time I wanted to do any of the following, I would have to open my web browser and go search for examples:

* Any conditional test (does this file exist? is this variable set?)
* Conditionals themselves (how many brackets? do I have to quote the variables?)
* Extract common code out into a function (how do I pass arguments? what about return values?)
* Collect up data and loop over it (basic syntax of a loop? how to terminate early?)
* Process command line arguments and provide a `--help` output (long and short options? defaults and required arguments?)

Then there's a whole class of things that are possible, but rather arcane in Bash, such as maps, arrays, and string substitution.

Most of these things would probably become second nature if I wrote Bash more often, but most of my time is usually spent working on a larger application in a different language. I think the issue is that the syntax and semantics of the language are enough different from any language I've spent a serious amount of time with (Java, Python, Go, Clojure, etc), that I can't tie any knowledge I learn to another set of information in my head.

So, I was stuck re-learning it each time I wrote a little script. It was a relatively small price to pay for the benefit of these small scripts.

# Bringing some Clojure to the problem

Some time in 2019, GraalVM was really starting to be a viable option for compiling Clojure programs. I remember using Borkdude's clj-kondo and jet, and then in August came the first versions of Babashka. I was instantly drawn to it and the idea of a fast-starting Clojure dialect.

I'd been a fan of Clojure for a while by then, and I really wanted to be able to use it for my command line scripts. My first attempts at this were with [planck][2] and [lumo][3], which are self-hosted ClojureScript runtimes. They satisfied the fast-startup requirement, but they bottomed out in the Javascript ecosystem and it's reliance on a single thread and callback-driven I/O. I have a few small experiments from back then, but it never caught on because it was difficult to call programs and read or write data, something that comes up rather often in command line scripts.

Babashka was different. Because it was based on the Java ecosystem, I could use `spit` and `slurp` as well as the `clojure.java.io` namespace to access the filesystem. I could execute other programs with `clojure.java.shell`. But it didn't stop there. Babashka also had command line options to automatically parse and emit data (`-i`, `-o`, `-I`, `-O`) which made it easier to write scripts that fit into pipelines. And there were Babashka-specific namespaces that made terminal and scripting tasks easier: `babashka.process` for running other programs, `babashka.fs` for filesystem operations, `babashka.deps` for dyanmically adding dependencies, and many more. These namespaces drew Babashka closer to it's hosted ecosystem (the terminal), and made it more powerful in the process.

As Babashka grew in power, I reached for it more and more when I needed a small script or tool to enhance my environment. These days, my scripts tend to take one of two paths toward completion: either as a single file or in my script incubator.

# Quick, self-contained scripts

Most of my scripts are of this variety. They are usually fewer than a couple dozen lines and are written in the fit of frustrated energy that occurs when there's a difference between what I need and the tools in front of me.

Here's a template of what I usually start off with:

```clojure
#!/usr/bin/env bb ;; [1]

(ns fooscript) ;; [2]

(when (= *file* (System/getProperty "babashka.file")) ;; [3]
  )
```

The important pieces here are:
1. Shebang line to elevate this program into Babashka
2. The namespace declaration, ready to add any of Babashka's built-in libraries
3. The "one little trick" that allows this file to be loaded into a REPL without executing it.

After opening this file up, I start a Babashka repl with `bb nrepl-server` in a tmux split and then I connect Conjure and evaluate the file. In a couple quick moves, I've reached cruising altitude and am ready to fly through my code.

From here, it's normal Clojure development; creating small composable functions, combining them together to accomplish my task, rapidly iterating as I learn more about what I want to do.

For the most part, I rely on Babashka's [built-in namespaces][4]. In the rare times I need something else, I don't need to convert the script into a "real" project. Instead, I rely on one of two techniques.

The first is to use `babashka.deps`, like this:

```clojure
(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.clj-commons/pretty {:mvn/version "2.2"}}})'
```

It's rather amazing that you can add any compatible library in-line like this. It's out of the realm of imagination in the old Bashiverse.

The second way I've reached out for other functionality is by including other Babashka scripts. This is one reason why I have a namespace declaration at the top (the other being that it's tidier).

For instance, I have a script called [`bbts`][5] that will take incoming EDN data and look for any map key of timestamp and convert its value into a human readable date/time. This is super useful as I'm not able to parse milliseconds directly.

```shell
$ cat sample.edn
{:mulog/timestamp 1697642960678}
{:mulog/data {:timestamp 1697642960678}}

$ cat sample.edn | bbts
{:mulog/timestamp "2023-10-18 8:29:20.67 AM"}
{:mulog/data {:timestamp "2023-10-18 8:29:20.67 AM"}}
```

Well, I recently was writing another script that had timestamp data and wanted to use `bbts`'s `humanize-timestamps` function. So I added this snippet:

```clojure
(load-file (-> *file* fs/real-path fs/parent (fs/path "bbts") fs/file))
(require '[bbts])

(comment
  (def records ...)
  (map bbts/humanize-timestamps records))
```

# Larger scripts

Sometimes, I want to write a larger script. One that has good `--help` documentation and tests to back up the implementation. My last post, about a [script that highlights text][6], is one such example.

For this, I use an incubator called [bb-scripts][7]. It's a Clojure project with some additional facilities for producing scripts that run in Babashka.

To make a new script, all I have to do is create [two files][8], one for the script's main namespace and one to run it under Babashka. Then, after I'm done (or done enough), I run an included script to create an uberscript so that the new script can stand on its own.

The advantages of using `bb-scripts` over the single file approach are:

* The Clojure nREPL implementation is slightly nicer in that it will pretty-print evaluation results.
* Scripts can share common library code for things like argument parsing and string handling.
* Tests are straightforward to specify and find, and can be run in both Babashka and Clojure.
* Fiddle code can be kept outside of the main namespaces.

# Combining power with ease of use

One thing I've been experimenting with lately is using some of the `bb-scripts` libraries in my single-file scripts.
The reason I want to do this is that I want my scripts to be well behaved citizens of the terminal. Every script should respond to `--help` and exit with a non-zero code if the arguments are invalid.
With single-file scripts, this results in needing to implement the same boilerplate handling in each script. It's much easier in the `bb-scripts` incubator as there are shared namespaces to handle this.

So, to bring the shared namespaces over, I created the [`lib.script.alpha`][9] namespace. It bundles together argument parsing and reporting into a single function.

For example, here's a very simple script to add two numbers. As you can see, almost half the script is devoted to command line argument handling.

```clojure
#!/usr/bin/env bb

(ns adder
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]))

(defn add
  [arg1 arg2]
  (format "The sum is %d." (+ (parse-long arg1) (parse-long arg2))))

(def usage "This script adds two numbers. Fancy, eh?")

(def cli-opts
  [["-h" "--help" "Show help."]
   ["-a" "--arg1 arg1" "First number to add." :missing "Must supply first number."]
   ["-b" "--arg2 arg1" "Second number to add." :missing "Must supply second number."]])

(when (= *file* (System/getProperty "babashka.file"))
  (let [{:keys [options errors summary]} (cli/parse-opts *command-line-args* cli-opts)
        {:keys [help arg1 arg2]} options]
    (cond
      help
      (do (println usage)
          (println summary))

      errors
      (do (println "Error:\n")
          (println (str/join "\n" errors))
          (println)
          (println summary)
          (System/exit 1))

      :else
      (println (add arg1 arg2)))))
```

Here's the same script using `lib.script.alpha`. There is some boilerplate to pull in the additional dependency, but now the script can leverage the `simple-main` function with only a few lines are boilerplate.  The `simple-main` function takes options related to argument parsing and usage and returns the result of calling `clojure.tools.cli/parse-opts` only if there are no errors and `--help` was not passed. In addition to making sure the usage help is formatted properly for printing, the token `SCRIPT_NAME` is replaced with the actual script name so that examples are easier to show.

```clojure
#!/usr/bin/env bb

(ns better-adder
  (:require [babashka.deps :as deps]))

(deps/add-deps '{:deps {io.github.justone/bb-scripts {:sha "13b61bea51da4c0e7634ba37131a3f42ed2a4a76"}}})
(require '[lib.script.alpha :as sa])

(def main-opts
  {:help "This script adds two numbers. Fancy, eh?

         Examples:

           SCRIPT_NAME -a 2 -b 4
           SCRIPT_NAME -a 3 -b 2"
   :cli-options [["-h" "--help" "Show help."]
                 ["-a" "--arg1 arg1" "First number to add." :missing "Must supply first number."]
                 ["-b" "--arg2 arg1" "Second number to add." :missing "Must supply second number."]]})

(defn add
  [{{:keys [arg1 arg2]} :options}]
  (format "The sum is %d." (+ (parse-long arg1) (parse-long arg2))))

(when (= *file* (System/getProperty "babashka.file"))
  (println (add (sa/simple-main main-opts))))
```

```shell
$ ./better_adder.clj -h
usage: better_adder.clj [opts]

This script adds two numbers. Fancy, eh?

Examples:

  better_adder.clj -a 2 -b 4
  better_adder.clj -a 3 -b 2

options:
  -h, --help       Show help.
  -a, --arg1 arg1  First number to add.
  -b, --arg2 arg1  Second number to add.

$ ./better_adder.clj -a 3 -b 3
The sum is 6.
```

At this point, `lib.script.alpha` is just an experiment, but I like the direction of making small helpers like this to ease single-file script creation.

# Wrapping up

So that's how I write Babashka scripts. It's great to use the same language to write applications and extend my development and operations environment.

Oh, one last thing. I did a talk a couple years ago about using Babashka on the command line and I covered some of this same information. Check it out below.

Enjoy.

{{< youtube RogyxI-GaGQ >}}

[1]: https://github.com/justone/dotfiles-personal/tree/personal/bin
[2]: https://planck-repl.org/
[3]: https://github.com/anmonteiro/lumo
[4]: https://book.babashka.org/#libraries
[5]: https://github.com/justone/dotfiles-personal/blob/personal/bin/bbts
[6]: /2023/08/07/highlight/
[7]: https://github.com/justone/bb-scripts
[8]: https://github.com/justone/bb-scripts/blob/master/README.md#set-up
[9]: https://github.com/justone/bb-scripts/blob/master/src/lib/script/alpha.clj

[^1]: Quite a bit earlier in my career, the language would have been Perl, but that knowledge left my brain several years ago.
