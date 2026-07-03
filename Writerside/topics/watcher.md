# 🥸 Watcher

<sup>
Available Since 0.0.39
</sup>

After we have made our reactive state its time to put the final piece in the puzzle - watchers.
Watchers allow us trigger external operations when the state changes.

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="watch"/>

## Re-attaching Watchers {id="re-attaching-watchers"}

<sup>
Available Since 0.0.52
</sup>

An `AttachedWatcher` can be detached from its current operator and re-attached to a different one.
This is useful when you need to move a watcher between managers, for example when transferring ownership of a game loop or switching execution contexts.

Once detached, the watcher will no longer be triggered by the original operator's heartbeat.
After re-attaching to a new operator, the watcher will respond to the new operator's heartbeat instead.

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="reAttachWatcher"/>
