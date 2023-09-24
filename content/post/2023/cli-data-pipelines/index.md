---
title: "Data-driven CLI Pipelines"
date: 2023-09-15T13:30:16-07:00
slug: cli-data-pipelines
draft: true
tags:
- clojure
- cli
---

Pipelines in the shell enable rapid experimentation and endless flexibility by combining composable tools, each of which does one thing well. Clojure data pipelines provide this same capability, but in code. I've been using Babashka to bring Clojure's power to the command line.

<!--more-->

# The OG data pipelines

UNIX pioneered the concept of pipes. In [this video][1], you can see Brian Kernighan describe finding spelling mistakes by piping discrete commands together. The operating system takes care of all the I/O and transferring data between processes. It's amazing how such a simple concept can enable so many different combinations of useful functionality.

I've always been a great fan of the terminal and it's expressive power. It is as relevant today as it ever was. Long ago, I grew to appreciate the dense information and power of the command line, and so I gravitate toward using it any time I can. I avoid graphical applications or web sites where I can get the same information at my prompt. I constantly look for tools to enhance my experience at the command line.

# The limitation

Pipelines are great, but they are hampered by their core interchange format: data is transferred between programs line by line. The problem is that, depending on the task at hand, information value is not evenly distributed in each line. When you are looking for unique visitors, the IP address is the most important part of a server log, but when you are curious as to how many 500s your application is returning, it's the status code.

There are core utilities that help with this problem. `awk` or `cut` are often used to grab positional information, but that is sometimes unreliable, and can be thrown off by header information.

The standard UNIX philosophy of being liberal with what you accept and strict with what you emit has enforced that every program must read and emit lines of text. There is no common higher-level format.

# The solution

The solution I've been exploring has been to use one of several readily available data formats to encode information. So, instead of each line being a bespoke data format that must be sliced apart to be useful, it's in a data format that is trivially parsed into rich data. I often use JSON for this, but there's also [EDN][0] and [Transit][0][^1] which provide a more extensible and rich encoding scheme.

So, now that data is richer, how do we

One of the oldest tools for handling this is [JQ][0]

# Someday/maybe

It's the same that affects line-oriented application logs. They are packed with information, but that sweet insight is trapped in human-readable lines and must be parsed in order for deeper diving to occur. That has given rise to



[0]: https://foobar.com/
[1]: https://www.youtube.com/watch?v=tc4ROCJYbm0

[^1]: You might be able do something similar with YAML, but honestly who would want to?
