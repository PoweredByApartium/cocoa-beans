# 🔍 Observer

<sup>
Available Since 0.0.39
</sup>

Observer is an object depending on the changes in observables. 
When the value of an observable changes, the observer is notified via the `flagAsDirty` method.
Below is an example implementation of observer:

<code-block lang="java" src="common/CodeSnippets.java" include-symbol="MyObserver"/>

You can also use [Watchers](watcher.md).

See also:
- [](mapped-observable.md)
- [](watcher.md)