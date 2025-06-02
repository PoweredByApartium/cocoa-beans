# 🔬 Compound Observable

<sup>
Available Since 0.0.39
</sup>

A compound observable is a type of observable which encapsulates multiple observables into it. 
It allows accessing values of multiple observables as if they are just one. 

If:
- All the observables are of the same element type
- The observables are not changing on their own
Consider using ()[collection-observable.md] instead.

## Usage - The fast approach

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="compound"/>

## Usage - Prettier compounds

The default implementations of compound observables support compounding 1 up to 10 observables.
You can support more arguments in the following way:

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="customCompound"/>

## Use Cases
- Aggregating values from several observables into one.

- Building derived state from multiple sources.