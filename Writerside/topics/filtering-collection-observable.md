# 🔎 Filtering Collection Observable

<sup>
Available Since 0.0.46
</sup>

## Introduction
Sometimes we want to derive a **live view** of a collection by filtering its elements based on an `Observable<Boolean>` **owned by each element**.

This is different from a simple `Predicate<E>` filter, because the membership of each element can change over time (for example: a player becomes alive/dead, online/offline) and the filtered collection should update automatically.

## Usage
<tabs>
    <tab title="Filter test">
        <code-block lang="java" src="state/CodeSnippets.java" include-symbol="filteredCollection"/>
    </tab>
    <tab title="GamePlayer.java">
        <code-block lang="java" src="state/CodeSnippets.java" include-symbol="GamePlayer"/>
    </tab>
    <tab title="PlayerState.java">
        <code-block lang="java" src="state/CodeSnippets.java" include-symbol="PlayerState"/>
    </tab>
</tabs>

### Expected behavior
- When an element is **added** to the source collection:
  - Subscribe to its predicate observable
  - Include/exclude it based on the current predicate value
- When an element is **removed** from the source collection:
  - Unsubscribe from its predicate observable
  - Remove it from the filtered collection
- When the **predicate** observable changes:
  - `true`: element appears in filtered view
  - `false`: element disappears from filtered view 

## Advantages
- **✅ Live subset view**: Always up-to-date without manual bookkeeping
- **🧠 Reactive membership**: Elements can enter/leave the view when their internal state changes
- **🧹 No observer leaks**: Observers are removed when elements leave the source collection
- **⚡ Efficient updates**: Updates only the impacted elements rather than recomputing everything
- **⛓ Chaining**: Can be chained with other operators