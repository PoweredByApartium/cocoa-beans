# 🗺️ Mapped Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction
Mapped observable is a reactive transformation layer, apply a transformation function to values from its source.
This is the core of reactive mapping: taking one observable and transforming it into another value that updates automatically.

## Usage
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="mapped"/>

## Advantages
- 📝 **Mapped state**: Ideal for mapping object into another object dynamically 
- 👀 **Lazy evaluation**: Only re-computes the derived value when the base observable updates.