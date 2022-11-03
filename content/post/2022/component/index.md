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

<!--more-->

## Tense Choices

First, a digression.

As my good friend Christoph says, every time you accept something (library, framework, technique) into your application, you invite in its additional complexity. Do you want to route web requests in a data-driven manner with Reitit? Then you need to understand its extensive configuration and the layered approach of middleware. Want to use XTDB for its awesome bitemporality? Then you need to understand datalog and the operational procedures of running it in production. The point is that nothing is free. The hope is that the benefit gained is worth the additional complexity cost.

Related to the technical capability/cost tradeoff is the impact that new dependencies have on development itself. It's important to consider the advantageous as well as detrimental behaviors that are unlocked. For instance, by choosing Clojure, you gain the ability to do interactive development. You also allow the possibility of your source code getting out of sync with the running application. Even Rich's decision to not include user-extensible reader macros in Clojure itself is a careful exclusion for the purpose of eliminating a whole category of behaviors that would have hurt the larger ecosystem.

So, don't choose things lightly.

## Dependency Injection

Second, another digression.

Dependency injection

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
