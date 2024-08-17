# 🔄Dynamic annotation parser

**Table of content:**
- [Introduction](#introduction)
- [Bla bla bla](#bla-bla-bla)
- [Usage](#example-of-making-something-with-it-bla-bla)
- [Conclusion](#conclusion)

## Introduction
The Dynamic Annotation Parser is a component that processes 
custom annotations applied to methods or classes to dynamically create argument mappers with [`ParserFactory`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html)

## Bla bla bla

The core of the Dynamic Annotation Parser is the [`ParserFactory`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html) interface. This interface defines a method [`ParserFactory#getArgumentParser`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html), which is responsible for returning a `Collection<ParserResult>` based on the provided command node, annotation, and generic declaration.
### Parameters
 - `commandNode`: The command node that the parser is associated with.
 - `annotation`: The annotation that triggers the parser creation.
 - `obj`: The generic declaration (either a class or method) where the annotation is applied.

### CommandParserFactory
To associate a specific annotation with a [`ParserFactory`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html) implementation,
the [`CommandParserFactory`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/CommandParserFactory.html) annotation is used.
This annotation should be applied to other annotations to indicate which [`ParserFactory`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html) should be used to create the argument parser.


### Scope
The scope of the parser (whether it applies to a method, class) is determined by the Scope enum. This allows fine-grained control over where and how parsers are applied within your command framework.
 - `VARIANT`: Just for that method
 - `CLASS`: For the entire class
 - `ALL`: Could be both for method or class it changes base on `ParseResult`

## Example of making something with it bla bla

<tabs>
<tab title="WithParser">

```java
/**
 * Registers a parser for a specific command
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CommandParserFactory(value = WithParserFactory.class, scope = Scope.ALL)
public @interface WithParser {
    /**
     * Parser to register
     */
    Class<? extends ArgumentParser<?>> value();
}

```

</tab>
<tab title="WithParserFactory">

```java
public class WithParserFactory implements ParserFactory {

  @Override 
  public @Nullable ParserResult getArgumentParser(CommandNode commandNode, Annotation annotation, GenericDeclaration obj) {
    if (!(annotation instanceof WithParser withParser))
        return null;

    try {
        Constructor<?>[] ctors = withParser.value()
                .getDeclaredConstructors();
        
        return new ParserResult(
            newInstance((Constructor<ArgumentParser<?>>[]) ctors),
            obj instanceof Method 
                ? Scope.VARIANT 
                : Scope.CLASS // Whether it for that variant or global 
        );
    } catch (Exception e) {return null;}
    
  }
    
  private ArgumentParser<?> newInstance(Constructor<ArgumentParser<?>>[] ctors) {
        // Do something
  }
}
```

</tab>
</tabs>

## Conclusion
The Dynamic Annotation Parser system streamlines the process of argument parsing by leveraging annotations and associated factories. It provides a robust and extensible way to manage parsers across different parts of your command system, reducing boilerplate code and enhancing maintainability.

