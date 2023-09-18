---
title: "Data-driven CLI Pipelines"
date: 2023-09-15T13:30:16-07:00
slug: cli-data-pipelines
draft: true
tags:
- clojure
- cli
---

Pipelines in the shell enable rapid experimentation and endless flexibility by combining composable tools, each of which does one thing well. Clojure data pipelines provide this same capability, but in code.

I've been using Babashka to bring Clojure's power to the command line.

<!--more-->

# The OG data pipelines

I've always been a great fan of the terminal and it's expressive power. It's the [original programming environment][1], and it's power has only grown. I tend to avoid graphical applications or web sites where I can get the same information at my prompt. I constantly look for tools to enhance my experience at the command line.

UNIX pioneered the concept of pipes. In [this video][1], you can see Brian Kernighan describe finding spelling mistakes by piping discrete commands together.




[1]: https://www.youtube.com/watch?v=tc4ROCJYbm0
