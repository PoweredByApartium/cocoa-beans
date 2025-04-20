# 🗺️ Mapped Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction
`MappedObservable` is a reactive transformation layer, apply a transformation function to values from its source.
This is the core of reactive mapping: taking one observable and projecting it into another value that updates automatically.

## 💡 Purpose
`MappedObservable<F, T>` allows you to define a new observable of type `T`, which is computed from a base observable of type `F` using a mapping function.
This means if you have a value `F`, you can create a new value `T` that always stays in sync with it.

## Usage
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="mapped"/>

## Advantages
- 📝 **Mapped state**: Ideal for mapping object into another object dynamically 
- 👀 **Lazy evaluation**: Only re-computes the derived value when the base observable updates.