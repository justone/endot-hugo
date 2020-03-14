---
layout: post
title: Octopress migration details
date: '2011-11-13 20:16:00'
status: publish
comments: true
tags:
- blog
slug: octopress-migration-details
---

As is customary for those who've converted from WordPress to Octopress, here's a quick post about my experience converting this blog.

Getting the blog up and running was a cinch, especially with a [good example](https://github.com/jbarratt/serialized.net-octopress) to examine when I had questions.

## Converting old entries

To convert my WordPress entries, I turned to [exitwp](https://github.com/thomasf/exitwp).  It worked pretty well, but I ran into two issues.

The first was that the YAML blob at the top of the converted posts wasn't formatted correctly.

``` yaml
