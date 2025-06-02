# 📹 Animation Observables

<sup>
Available Since 0.0.39
</sup>

**Table of content:**
- [](#introduction)
- [](#overview)
- [](#usage-showcase)
- [](#advantages)
- [](#what-next)

## Introduction
This page describes a set of animation-based observable implementations based on the `Observable` and `Observer` interfaces. These are useful for reactive and animated UI elements, such as fading or blinking text, typewriter-style effects, and interpolated values.

All classes implement the standard `Observable<T>` interface and update automatically using an `Observable<Instant>` time source.


## Overview

Each animation implementation provides a different visual behavior:

| Class Name                                                                                                                                                                    | Description                                                                |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------|
| <a href="https://cocoa-beans.apartium.net/%version%/state-animation/net/apartium/cocoabeans/state/animation/FadingColorBlinkObservable.html">`FadingColorBlinkObservable`</a> | Fades text in and out with a blinking effect at the end.                   |
| <a href="https://cocoa-beans.apartium.net/%version%/state-animation/net/apartium/cocoabeans/state/animation/FadingColorInOutObservable.html">`FadingColorInOutObservable`</a> | Fades text in and out symmetrically, with hold times at the start and end. |
| <a href="https://cocoa-beans.apartium.net/%version%/state-animation/net/apartium/cocoabeans/state/animation/TypingObservable.html">`TypingObservable`</a>                     | Animates text like a typewriter.                                           |
| <a href="https://cocoa-beans.apartium.net/%version%/state-animation/net/apartium/cocoabeans/state/animation/FixedDoubleLerpObservable.html">`FixedDoubleLerpObservable`</a>   | Smoothly interpolates numeric values over a fixed duration.                |

---

## Usage & Showcase

```java
public void onJoin(CocoaBoard board) {
    board.title(new FadingColorInOutObservable(
            Observable.immutable("ProjectZero"),
            boardManager.getNow(),
            Duration.ofSeconds(3),
            Duration.ofMillis(750),
            Duration.ofMillis(2 * 50L),
            Style.style(NamedTextColor.GREEN, TextDecoration.BOLD),
            Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD),
            Style.style(NamedTextColor.DARK_GREEN, TextDecoration.BOLD)
    ));
    
    board.add(Component.text("Simple scoreboard animations"));

    board.add(new FixedDoubleLerpObservable(
            boardManager.getMoney().map(i -> i + 0.0),
            boardManager.getNow(),
            Duration.ofMillis(750)
    ).map(money -> Component.text("Money ")
            .color(NamedTextColor.GRAY)
            .append(Component.text(
                    String.format("%.1f", money),
                    NamedTextColor.RED
            ))
    ));
    board.add(new TypingObservable(
            Observable.immutable("Hello, World!"),
            boardManager.getNow(),
            Duration.ofMillis(50 * 50L),
            Duration.ofMillis(50 * 50L),
            Duration.ofMillis(2 * 50L),
            "§k"
    ).map(Component::text));

    board.add(new FadingColorInOutObservable(
            Observable.immutable("example.com"),
            boardManager.getNow(),
            Duration.ofSeconds(3),
            Duration.ofMillis(750),
            Duration.ofMillis(2 * 50L),
            Style.style(NamedTextColor.YELLOW, TextDecoration.BOLD),
            Style.style(NamedTextColor.GOLD, TextDecoration.BOLD),
            Style.style(NamedTextColor.WHITE, TextDecoration.BOLD)
    ));
}
```

<img src="scoreboard_animations.gif" alt="scoreboard_animations.gif"/>

## Advantages

Animation observables provide a declarative and reactive approach to building time-based UI transitions. Below are some of the key advantages:

### 🔁 Continuous Reactivity
All animations automatically update based on their dependencies (`Observable<String>`, `Observable<Instant>`, etc.), without manual ticking or recomputation. This ensures a clean separation between logic and time-based rendering.

### 🧩 Composable Animations
Each animation is implemented as a self-contained observable that can be easily composed with others, enabling layered effects like fading + blinking or typing + styling.

### ⚙️ Configurable Behavior
Animations like `FadingColorBlinkObservable`, `FadingColorInOutObservable`, and `TypingObservable` expose parameters such as `characterDelay`, `blinkTimes`, and `writingSpeedPerCharacter`, allowing fine-grained control over timing and styling.

### 🎨 Style Interpolation
Visual transitions are achieved using styled `Component`s and helper functions like `AnimationHelpers.fading`, enabling smooth color blending and appearance effects.

### 🔄 State API Integration
Because all animation classes implement `Observable<T>` and `Observer`, they seamlessly plug into the state-based update mechanism, propagating changes downstream efficiently.

### 🧪 Predictable & Testable
Deterministic behavior based on time and text input makes these animations easy to simulate, debug, and verify in tests.

### ⏱️ Time-Based Synchronization
By using `Observable<Instant>` for time tracking, all animations can remain in sync with a global clock or custom scheduler, supporting coordinated animations across components.

## What next
* [](scoreboard-api.md)
* [Javadocs](https://cocoa-beans.apartium.net/%version%/state-animation/net/apartium/cocoabeans/state/animation/package-summary.html)