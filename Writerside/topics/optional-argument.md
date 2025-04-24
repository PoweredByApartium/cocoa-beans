# ❓ Optional Argument

An optional argument can have 2 forms:
1. **Optional** `if the argument is not specified it will just be empty / null`
2. **Invalid** `if parser couldn't parse given argument`

We can use optional arguments where the last arguments are not required. 

Imagine a players runs the command `/give ikfir diamond`. We accept an optional int param but it wasn't specified, so the code falls back to 1.
The player can still get more by running `/give ikfir diamond 32`.

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
Look closely at the `@SubCommand` annotation: the last argument contains a `?` operator, which tells the command system it is an optional argument. Optional arguments can also use java's Optional classes to provide null safety.

### Optional
As mentioned above, optional arguments can be set by using the `?` operator.
For example, `<int>` is the normal form where the optional form is `<?int>`

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

However, if we will try to run `example 42a` it will not call `ExampleCommand#example`, because 42a isn't a valid `int`. To avoid failing parsing, we can use failsafe optional arguments.

### Fail-safe optional arguments
Fail safe optional arguments can be indicated with the `!` operator, for example:
* `<int>` for normal args
* `<?int>` for optional args
* `<!int>` for fail-safe optional args
* `<?!int>` for a combination of optional and fail safe (more on that later)

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
and that will not result in invalid parsing.

But when we will try to run `example 45a` the system will call `ExampleCommand#example` with optNum `OptionalInt#empty`.
Lastly it will give as same result if parser has parser successfully it will just put parser value.

Of course, if we call `example 45` the system will call `ExampleCommand#example` with 45 as argument.  

### How can we use both of them
Using fail-safe arguments might not make much sense on their one, but we can combine them with optional arguments. 
It will make the command variation run if either the argument was not specified, or the player specified an invalid argument. 
We could do it very simply by just using following syntax `?!` or `!?`

```java
@Command("example")
public class ExampleCommand implements CommandNode {
 
    @SubCommand("<?!int>")
    public void example(Sender sender, OptionalInt optNum) {
        // Do something
    }
    
}
```