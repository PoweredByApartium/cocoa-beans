# ❓ Optional Argument

An optional argument has two forms
1. **Optional** `if there is no argument it will just be empty / null`
2. **Invalid** `if parser couldn't be parser for any reason`

We will want to use optional argument when we want to handle some cases when data could be null.

As an example `/give ikfir diamond` We know we just one to give him 1 diamond.
But what if instead we would want to give more so we will run `/give ikfir diamond 32` that will give him 32 diamonds

We use the following code
```java
@Command("give")
public class GiveCommand implements CommandNode {
 
    @SubCommand("<player> <item> <?int>")
    public void give(Sender sender, Player target, Item item, OptionalInt optAmount) {
        int amount = optAmount.orElse(1);
        if (amount <= 0) {
            sender.sendMessage("invalid amount");
            return;
        }
        
        // Do something
    }
    
}
```
If you look at `@SubCommand` You will be able to see a parser named `?int` but that's just are normal int parser
We could have it perform optional for us

### Optional
The symbol for optional is `?` we will need to use it before parser name for example for int parser that keyword is `int`
we will do the following `<?int>`

Code snippet
```java
@Command("example")
public class ExampleCommand implements CommandNode {
 
    @SubCommand("<?int>")
    public void example(Sender sender, OptionalInt optNum) {
        // Do something
    }
    
}
```
When we run `example` without any args it will call `ExampleCommand#example` with optNum as `OptionalInt#empty`
But when we will try to run it with a number as arg, For example `example 42` it will call the same function `ExampleCommand#example` with optNum as 42

Lastly if we will try to run `example 42a` it will not call `ExampleCommand#example` because 42a isn't decimal number same go for 40.1 is not integer.

We will have to use invalid instead
### Invalid
The symbol for invalid is `!` we will need to use it before parser name for example for int parser that keyword is `int`
we will do the following `<!int>`

Code snippet
```java
@Command("example")
public class ExampleCommand implements CommandNode {
 
    @SubCommand("<!int>")
    public void example(Sender sender, OptionalInt optNum) {
        // Do something
    }
    
}
```
This time when we run `example` without any args it will not call `ExampleCommand#example` because parser will not be called for no arguments
and that will not result as invalid parsing.

But when we will try to run `example 45a` it will call `ExampleCommand#example` with optNum `OptionalInt#empty`.
Lastly it will give as same result if parser has parser successfully it will just put parser value.

### How can we use both of them
But what if we will want to use both of them invalid and optional?

We could do it very simply by just using following syntax `?!` or `!?`

Here is another code snippet
```java
@Command("example")
public class ExampleCommand implements CommandNode {
 
    @SubCommand("<?!int>")
    public void example(Sender sender, OptionalInt optNum) {
        // Do something
    }
    
}
```