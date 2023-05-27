---
title: Developing Clojure in Vim (2023 edition)
date: 2023-05-27T16:16:10-07:00
slug: vim-clojure-dev-2023
tags:
- programming
- clojure
- vim
---

The last time I wrote about [using Vim to write Clojure][1], I had just started using it professionally. Now I'm at my second Clojure job and I am still enjoying the its power, combined with the speed of Vim. However, in contrast to the [last update][26], which was incremental to [my first post][2], quite a few things about my setup have changed in the past few years.

<!--more-->

## Old Faithful

First off, let's start with the plugins that havent changed.

* [vim-clojure-static][3] - Base syntax highlighting and indentation
* [rainbow][4] - Rainbow colors parentheses
* [vim-sexp][5] - Structural editing (slurp, barf, splice, raise, swap, etc.)
* [vim-sexp-mappings-for-regular-people][6] - Add a few more useful bindings to vim-sexp
* [cljfold][7] - Code folding cues for Clojure

My config for all of these hasn't changed. There have been a handful of small updates, but for the most part these plugins are rock solid and reliable.

One thing I will say is that I really enjoy structural editing with the two SExp plugins above. Despite Clojure being a lisp, I can count on one hand the number of times I've somehow gotten my parentheses unbalanced. I highly recommend taking the time to learn structural editing. I remember looking at some of the more odd manipulation names (like splice and raise) and thinking I would never use them, but that hasn't borne out. They come up quite regularly, and I also use the reordering mappings quite a bit. I have never tried parinfer, mostly because I've seen people get frustrated at it too often.

I also remain a voracious code folder. I think this throws some people off, but I find it valuable to hide what I am not looking at.

## REPL Connection

For quite a while, I was a happy user of [Fireplace][8], but a few years ago I started looking at some of the other Vim plugins that were available[^1]. I settled on  [Conjure][9] after briefly trying them all out. Each evaluation is its own buffer in Fireplace, and I added some mappings to call them up for reference. Conjure takes another tactic by putting the output from all evaluations into a shared buffer. Early on, this took some getting used to, but I have grown to prefer it this way. Having a common buffer makes it quite easy to reference and compare data together, and has had some surprising uses, such as editing down a portion as a report for others.

I remember when Conjure first started off as a Clojure-based Neovim plugin, with the associated wait time for start up and the occasional orphaned JDK process. I tend to run my Vim session for a while, so this wasn't a major annoyance, but I am tremendously grateful that it was rewritten into a Fennel/Lua plugin. It loads quite fast and is useful immediately. I also really appreciate the attention that its author Oliver Caldwell has given it over the years and that he continues to innovate with it.

Here are the bindings that I use the most:
* `,er` - Evaluate the root form from where my cursor is. This obviates the need to more my cursor to the top of the form to evaulate with `,ee`, and the fewer keystrokes between `ESC` and evaluation, the better. This even works properly in `(comment ...)` forms, thanks to the [conjure-efroot][27] plugin.
* `,ew` - Evaluate the word under my cursor. This is particularly useful for global defines or vars defined in fiddle code to check their value.
* `,em[mark]` - This is one of Conjure's coolest features. I can put a [mark][10], such as `a`, somewhere in my file and evaulate it from wherever my cursor is by hitting `,ema`. This is particularly useful for when I'm editing multiple functions and I have a [Rich Comment][11] with one or more calls to those functions. I can then load the function definition and run it by hitting `,er` followed by `,ema`, all without moving my cursor at all. Capital letter marks can be evaluated from other files, and I use this to allow `,emC` to call [Portal][12]'s `clear` function to reset its viewport data from anywhere I am working.
* `,lr` - This resets the Conjure log buffer, ready for a fresh set of evaluations.

## Fast Linting

[Clj-kondo][12] is a linter that has definitely taken over the Clojure community in the past few years. It offers quite a few good suggestions and even has some disabled-by-default rules that can be helpful. I originally used [Syntastic][13] to integrate Clj-kondo (like Joker before it), but disliked the pause it took on each file write. I switched over to [ALE][14] a couple years ago and haven't looked back. It runs Clj-kondo in the background and adds its suggestions when finished, allowing me to move on.

For the most part, I leave the ALE configuration stock. I did disable checking in the Conjure buffer, and I added a couple of [vim-unimpaired][15] compatible mappings to go between warnings in the file.

```vim
" Async Lint Engine config
let g:ale_linters = {'clojure': ['clj-kondo']}
let g:ale_pattern_options = {'conjure-log.*.cljc': {'ale_enabled': 0}}
" Easy navigation between errors
map [w <Plug>(ale_previous_wrap)
map ]w <Plug>(ale_next_wrap)
```

## LSP with clojure-lsp

The Language Server Protocol movement has been quite a revelation for developer tooling. Decoupling the editor from language-specific tooling allows the latter to move at its own pace and benefit all editors. The [clojure-lsp][16] project has grown quite a bit and [provides all the features][28] and more that I would expect in a sophisticated IDE. Integrating it has impacted my Clojure development workflow as much or more than my switch to Conjure.

Here are the bindings that I use the most:
* `gd` (and `gD` when I want a split) - Jump to definition. This greatly eases opening up related files and I find I can get a faster understanding of a call tree because I can jump as quickly as Clojure would.
* `,ci` and `,co` - Incoming and outgoing calls. This pops up a list of the callers or callees of the current function. Again, this greatly enhances my exploration and understanding.
* `,cr` - Rename identifier. I often will pick a bad-but-good-enough name when quickly writing/designing code and this allows me to go back and refine the names. It even works across files.
* `,c` - Bring up code actions for the code my cursor is on. This is how I access most of the occasionally useful actions, like "clean namespace" and "sort map keys."

One of the coolest features that I only need from time to time is moving pairs up and down. vim-sexp-mappings-for-regular-people has mappings for moving the current element or form backward and forward, and I always wanted to be able to do the same for pairs, such as let bindings and map key/value pairs. Clojure-lsp calls this drag forward/backward, and I mapped that to `<p` and `>p`. Now I can move pairs around and I laugh at the magic every time I do.

## Future

A lot has changed since I last wrote about Clojure and Neovim, and I haven't even caught up with most of the cool new things out there. Here are a few that I'm hoping to try out soon.

### Fennel/Lua Neovim config

It's now possible to completely configure Neovim with Lua, and since Fennel compiles to Lua, there are a few brave souls who have converted almost their entire config into lisp! Here are a few of them:

* Conjure's creator Oliver Caldwell's [config][17], also helpfully extracted for use by others as [magic-kit][18]
* Rafael Delboni's [config][19]
* Practicalli's [config][20]

This is very appealing to me as these configs are well organized and easy to follow and extend.

### Native Neovim LSP

Neovim itself has its own LSP server now. I currently integrate with clojure-lsp via the [coc.nvim][21] plugin, and its served me well. There was some recent instability that caused me to pin to an older version, but now it is back to stable again. I am curious how the native LSP integration behaves and would like to try it out.

### Other Neovim plugins

There are quite a few new plugins out there that are re-imaginings of older plugins. I'm wary of changing too much at once, but over time I will start trying some of them out.

## Further reading

All of my configuration is in my [dotfiles][22] repo[^2].

* [.vimrc][23] - one big messy file, search for `coc.nvim` to find my `clojure-lsp` bindings
* [coc.nvim configuration][25]

That's all I have for now.

Enjoy.

[1]: /2018/07/14/vim-clojure-dev-2018/
[2]: /2014/02/12/setting-up-vim-for-clojure/
[3]: https://github.com/guns/vim-clojure-static
[4]: https://github.com/luochen1990/rainbow
[5]: https://github.com/guns/vim-sexp
[6]: https://github.com/tpope/vim-sexp-mappings-for-regular-people
[7]: https://github.com/gberenfield/cljfold.vim
[8]: https://github.com/tpope/vim-fireplace
[9]: https://conjure.fun/
[10]: https://vim.fandom.com/wiki/Using_marks
[11]: https://practical.li/clojure/clojure-cli/projects/rich-comments.html
[12]: https://github.com/djblue/portal
[13]: https://github.com/vim-syntastic/syntastic
[14]: https://github.com/dense-analysis/ale
[15]: https://github.com/tpope/vim-unimpaired
[16]: https://clojure-lsp.io/
[17]: https://github.com/Olical/dotfiles/tree/main/stowed/.config/nvim
[18]: https://github.com/Olical/magic-kit
[19]: https://github.com/rafaeldelboni/dotfiles/tree/master/config/nvim
[20]: https://github.com/practicalli/neovim-config-redux
[21]: https://github.com/neoclide/coc.nvim
[22]: https://github.com/justone/dotfiles-personal/
[23]: https://github.com/justone/dotfiles-personal/blob/personal/.vimrc
[25]: https://github.com/justone/dotfiles-personal/blob/personal/.vim/coc-settings.json
[26]: /2018/07/14/vim-clojure-dev-2018/
[27]: https://github.com/walterl/conjure-efroot
[28]: https://clojure-lsp.io/features/

[^1]: How cool is it that we have no fewer than 3 (!!) different ways for Vim to reach out to Clojure. So awesome!
[^2]: I originally started versioning my dotfiles to enable keeping them consistent across different servers. For this post, the history in there was helpful in remembering when I switched between plugins.
