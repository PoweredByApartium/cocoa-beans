# 🔬 Compound Observable

<sup>
Available Since 0.0.39
</sup>

A compound observable is a type of observable which encapsulates multiple observables into it. 
It allows accessing values of multiple observables as if they are just one. 

## Usage
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="compound"/>

## Use Cases
- Aggregating values from several observables into one.

- Building derived state from multiple sources.

- Managing logic-heavy computed values while preserving reactivity.
