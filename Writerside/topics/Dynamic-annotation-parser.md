# 🔄Dynamic annotation parser

**Table of content:**
- [Introduction](#introduction)
- [Declaring your annotation](#declaring-your-annotation)

## Introduction
The Dynamic Annotation Parser is a feature of the command system that registers argument parsers into your commands by declaring them in annotations. \
\
This is a powerful features that allows command authors to use parameterized argument parsers without having to manually register them for the whole command manager. \
\
This is done by creating an annotation, that is itself annotated with [`CommandParserFactory`](https://cocoa-beans.apartium.net/%version%/commands/net/apartium/cocoabeans/commands/parsers/CommandParserFactory.html). This way, the annotation will specify a [`ParserFactory`](https://cocoa-beans.apartium.net/%version%/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html) class to be constructed. The class will do the heavy lifting and contain the logic for constructing the argument mappers. 

## Declaring your annotation

The core of the Dynamic Annotation Parser is the [`ParserFactory`](https://cocoa-beans.apartium.net/%version%/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html) interface. This interface defines a method [`ParserFactory#getArgumentParser`](https://cocoa-beans.apartium.net/snapshot/commands/net/apartium/cocoabeans/commands/parsers/ParserFactory.html), which is responsible for returning a `Collection<ParserResult>` based on the provided command node, annotation, and generic declaration.
### Parameters
 - `commandNode`: The command node that the parser is associated with.
 - `annotation`: The annotation that triggers the parser creation.
 - `obj`: The generic declaration (either a class or method) where the annotation is applied.

An example annotation can be:
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithParser {

    Class<? extends ArgumentParser<?>> value();

    int priority() default 0;
}
```

### CommandParserFactory
We have created the annotation, but for it to actually do something we need to create a factory. 
\
Lets create a simple factory that uses the argument parser class objects to create a new argument parser instance, and register it to the command.

```java
public class WithParserFactory implements ParserFactory {

    @Override
    public @NotNull List<ParserResult> getArgumentParser(
            CommandNode commandNode, // the node where the annotation is declared
            Annotation annotation, // the annotation, in our case an instance of WithParser
            GenericDeclaration obj) { // the element the annotation is applied to, either class or a method
        try {
            // take an arg-less constructor from the specified argument parser
            Constructor<? extends ArgumentParser<?>> ctor = (Constructor<? extends ArgumentParser<?>>[]) withParser.value().getDeclaredConstructor();
            // if annotation is applied on a method, scope should be a variant
            // otherwise, it should be a class
            Scope scope = obj instanceof Method ? Scope.VARIANT : Scope.CLASS;
            
            // ParseResult contains two fields:
            // - the argument parser instance
            // - the scope of the parser (see relevant section)
            // this method can return multiple parsers, but in this case we only return one
            
            return List.of(new ParserResult(ctor.newInstance(), scope));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return List.of();
        }
    }
}

```

Now we also have to specify the factory in the annotation:
```java
@CommandParserFactory(value = WithParserFactory.class, scope = Scope.ALL) // <-- This is the factory we just created
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithParser {

    Class<? extends ArgumentParser<?>> value();

    int priority() default 0;
}
```

### Scope
The scope of the parser (whether it applies to a method, class) is determined by the Scope enum. This allows fine-grained control over where and how parsers are applied within your command framework.
 - `VARIANT`: Just for a specific sub command
 - `CLASS`: For the entire class
 - `ALL`: Could be both for method or class it changes base on `ParseResult`