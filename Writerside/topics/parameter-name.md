# 📮 Parameter name

<sup>
Available Since 0.0.37
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Usage](#usage)
- [Notes](#notes)
- [Advantages](#advantages)

## Introduction
The parameter name feature is available since `0.0.37` and allows you to set the name of a parameter in a command for more readability.
The syntax is as follows `<name: parser>` name as the parameter name and parser as the argument parser keyword.
It is recommended to use the `@Param` annotation to set the name of a parameter in a command.

## Usage
Here is an example of using the parameter name feature
```java
@SubCommand("send <target: player> <message: string>")
public sendMessage(CommandSender sender @Param("target") Player target, @Param("message") String myMessage) {
  // Do something
}
```
As you can see in the example above, we use the `@Param` annotation to set the name of the parameter.
And we declare the parameter name in the `@SubCommand` annotation by using the following syntax `<name: parser>` we could also work with the old syntax when it just a parser keyword for example
```java
@SubCommand("send <player> <string>")
public sendMessage(CommandSender sender, Player target, String myMessage) {
    // Do something
}
```
And it will work as well like it did before.
We could also combine the old syntax with the new one as follows
```java
@SubCommand("send <target: player> <string>")
public sendMessage(CommandSender sender, @Param("target") Player target, String myMessage) {
    // Do something
}
```

## Notes
The parameter name will throw an exception if there is duplicate parameter names in the same command.
Also the parameter name work with the new command lexer so if you want to change the syntax have a look there.

## Advantages 💡
 - **Improved Readability** 📖: The parameter name feature provides a clear and descriptive name for each parameter, making the command structure more readable and self-explanatory.
 - **Improved Maintainability** 👍: By using the parameter name feature, you can easily identify the purpose and type of each parameter, reducing the need for additional comments or documentation, and you can move parameters around in `@SubCommand` without having to update the method itself.