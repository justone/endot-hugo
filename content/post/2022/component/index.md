---
title: "Still Component After All These Years"
date: 2022-10-19T16:39:54-07:00
slug: still-component
tags:
- programming
- clojure
---

Lately, I've been thinking about application structure in Clojure.

Many years ago my first experience was with Component, the OG application lifecycle library. For a time, all of the applications I had experience with (self created or not) used Component. Over time, I heard of other libraries that solved the same problem. The first was Integrant, which externalizes the system definition as a data structure. The next was Mount, in the context of a Luminus-based application, which ties stateful pieces to Clojure namespace load order. The most recent one I learned about is Redelay, which is even more flexible by letting you control initialization order with simple derefs. There are even a few more that sprung up over the past year.

My conclusion? After several years of experience creating small and large applications in Clojure, I still prefer Component.

Why? Read on.

<!--more-->

## Tense Choices

First, a digression.

As my good friend Christoph says, every time you accept something (library, framework, technique) into your application, you invite in its additional complexity. Do you want to route web requests in a data-driven manner with Reitit? Then you need to understand its extensive configuration and the layered approach of middleware. Want to use XTDB for its awesome bitemporality? Then you need to understand datalog and the operational procedures of running it in production. The point is that nothing is free. The hope is that the benefit gained is worth the additional complexity cost.

Related to the technical capability/cost tradeoff is the impact that new dependencies have on development itself. It's important to consider the advantageous as well as detrimental behaviors that are unlocked. For instance, by choosing Clojure, you gain the ability to do interactive development. You also allow the possibility of your source code getting out of sync with the running application. Even Rich's decision to not include user-extensible reader macros in Clojure itself is a careful exclusion for the purpose of eliminating a whole category of behaviors that would have hurt the larger ecosystem.

So, don't choose things lightly.

## Dependency Injection

Ok, another digression.

You can't just make an application these days. You have to assemble it. Sure, if it's small enough, you can get away with having everything in one file, but once you have a web handler and a database, you're going to want to keep them separated and be careful how they interact.

Dependency injection is the big-pants developer way of saying this, but it's just about getting code what it needs at the right time. In fact, the simplest form of dependency injection is what we call function parameters. And, after all, functions are how stuff gets done in any application.

Take a look at this function call:

```clojure
(user/save db user-info)
```

It's pretty easy to see what it saves and where it saves it, without looking at the implementation. Ah, development bliss.

This function, on the other hand, should strike fear in the very core of your developer heart:

```clojure
(user/save user-info)
```

One of the needed dependencies is injected. Where's the other one? The function promises to save the user, but where will it be saved?!? Functions like this are hard to trust, because the very thing they promise to do seems impossible with what they are given. Of course, what ends up happening is that the function reaches for some "out of band" global for the necessary database handle. Another way of saying "out of band" is "congitive load". It means that I have to remember where it gets that information. I'm guessing that it's probably the same for all functions that access the database, but what if I want to use that function in a context outside of the main application, such as when I'm trying to add a user manually for that special customer who just can't seem to use the website right? I can't create a one-off connection to the right database and call the function, I have to look for that global and make sure that it's initialized correctly so that the user will be saved in the right place.

See what I'm getting at here?

## Lists are good

Ok, now another digression. I'm beginning to think these are important to the title topic.

Lists are good. There's a reason why we humans use lists for so many things. It makes those things easy remember. For instance, take the humble shopping list. This is the single place where you note all the things you need for the next week, whether or not they're going to be used for breakfast or cake, so that when you go to the grocery store you don't have to try and remember all the different food activities you've planned and then get to 9:42 on a Friday night and find you are all out of vanilla extract. One list saves the day.

Now, imagine if you didn't keep everything you needed at the grocery store in one list... Say you made lots of little lists; one in the pantry where the cereal and granola bars are, one over in the fridge where the milk goes, one in the cookbook where the cake recipe is, etc. Each one contains what is needed in that context. That might be nice for when the pantry is open, but it certainly won't help at the grocery store.

In software lists are great too. There's a list at the top of most source code files that shows all the other files that this one uses. Usually there's a file that has a list of all the routes that the application will handle. Why are these in lists? Because they are a directory, a jumping off point for exploring the rest of the code.

Imagine that you move those lists right next to where they're used? That library require, move it down next to the first function that uses it. Spread out all the request routes into the namespaces that handle them. That might be good while you're working on one function or one route, but how are you going to understand the entire application if you have to have 42 files open and a well-integrated go-to-definition keymap?

# Outline

* Intro
  * Why DI?
  * Not needed for simple scripts
  * Larger applications need to be "stitched together"
  * Things like database connection pooling need to be managed in one place
* I want to emphasize system understandability.
  * Layers of complexity
    * Simple function
	* Small, single-purpose script
	* Application with separate parts that work together
* The easiest to understand dependency injection system is function parameters.
* Functions should take all necessary parameters.
  * This function call should terrify you. It scares me.
  * Where is the data being saved?
  * Functions like this take out-of-band information. Another way of saying "out of band" is "congitive load"
  * Ugh, but what about passing the db handle through 12 layers?!
* Lists are a good thing
  * Humans like lists, they're a great way to organize information
  * They're all in one place. Imagine your grocery shopping list written in 5 different locations, in the pantry, cabinet, fridge?
  * They act as a directory, or jumping off point to more information
  * Namespace requires, application routes
  * One namespace that contains all the information about your app together
    * Some complain about that and want to define their system in an external file
  	* I've been burned by XML, I mean external data, before.
* Things that people don't like about Component
  * Ew Records, everything has to be a record or GTFO
    * Not true
  	* Simple atom - for sharing state
	* Time pool object for scheduling events
	* Can even do a function
