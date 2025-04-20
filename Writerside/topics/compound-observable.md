# 🔬 Compound Observable

<sup>
Available Since 0.0.39
</sup>

Compound observables allow you to create a new Observable that derives its value from multiple dependent observables.
Whenever one of the source observables updates, the compound observable will automatically recompute its value when it has been called.

## Usage
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="compound"/>

## Use Cases
- Aggregating values from several observables into one.

- Building derived state from multiple sources.

- Managing logic-heavy computed values while preserving reactivity.
