# ℹ️ Command Information

**Table of content:**
- [Introduction](#introduction)
- [Annotations](#annotations)
- [Usage](#usage-of-annotations)
- [Managing multiple classes](#managing-multiple-descriptions-usages-and-long-descriptions)
- [Relevant links](#relevant-links)

## Introduction

In Java applications, managing and documenting commands effectively is crucial for both developers and users. The `CommandInfo` class provides a set of powerful annotations designed to enhance the clarity and usability of command-line interfaces within Java applications. These annotations facilitate comprehensive documentation and streamline the process of defining commands.

By using the annotations provided by `CommandInfo`, developers can:

- Clearly describe the purpose and functionality of each command.
- Specify how commands should be used, including the format and required parameters.
- Offer detailed explanations and warnings about the command’s behavior and implications.

This approach not only improves code readability and maintainability but also ensures that end-users have access to well-documented commands, reducing the likelihood of errors and enhancing overall user experience.

In this document, we will explore the specific annotations available in the `CommandInfo` class:

- `@Description` for providing a brief summary of a command.
- `@Usage` for outlining how the command should be invoked.
- `@LongDescription` for offering a detailed explanation of the command’s functionality.

These annotations can be applied to methods or classes that represent commands, enabling you to build a robust and user-friendly command documentation system in your Java applications.

## Annotations

### 1. `@Description`

The `@Description` annotation is used to provide a brief description of a command. This description should summarize the purpose or function of the command in a single string.

### 2. `@Usage`

The `@Usage` annotation specifies the usage pattern of a command. It provides a concise string that outlines how the command should be invoked or what parameters are required.

### 3. `@LongDescription`

The `@LongDescription` annotation offers a detailed explanation of a command. This annotation allows for a multi-line description, represented as an array of strings, to provide in-depth information about the command's functionality and usage.

## Usage of annotations

Here is code example of how we can use command information
```java
@Description("Manages user accounts.")
@LongDescription({
        "This command manages user accounts within the system. It allows for various operations such as creating, updating, and deleting user accounts.",
        "The command supports the following operations:",
        "- `create <username>`: Creates a new user with the specified username.",
        "- `update <username> <newDetails>`: Updates the details of an existing user.",
        "- `delete <username>`: Deletes the user with the specified username.",
        "Be cautious when using the delete operation, as it will permanently remove the user and their associated data from the system.",
        "Ensure you have appropriate backups and permissions before performing deletions."
})
@Command("user")
public class UserCommand implements CommandNode {
    
    @Description("Deletes a user from the database")
    @Usage("/user delete <username>")
    @LongDescription({
            "Deletes a user from the database.",
            "This command will remove the specified user along with all associated data.",
            "Make sure to backup relevant information before executing this command."
    })
    @SubCommand("delete <user>")
    public void deleteUser(Sender sender, User target) {
        // Do something
    }
    
    // etc...
}
```

We also could get more information about does description by getting `CommandContext#commandInfo`.
For example
```java
@Description("Manages user accounts.")
@Command("user")
public class UserCommand implements CommandNode {

    @Description("Deletes a user from the database")
    @SubCommand("delete <user>")
    public void deleteUser(CommandContext context, Sender sender, User target) {
        String description = context.commandInfo().getDescription().map(Description::value).orElse(null);
        // Do something
    }

    // etc...
}
```

If we want to get `CommandInfo` for the class itself we should call
`commandManger.getCommandInfo(commandName);`

## Managing Multiple Descriptions, Usages, and Long Descriptions
Particularly when dealing with command-line interfaces, there are scenarios where multiple classes might define commands with the same name, or where command classes are extended. The `CommandInfo` class allows for multiple `@Description` annotations to accommodate these situations, ensuring comprehensive and accurate documentation.

### Why Multiple Descriptions?

#### Handling Multiple Classes with the Same Command Name

In complex applications, you may encounter multiple classes that define commands with the same name but in different contexts or with different functionalities. The ability to use multiple `@Description` annotations ensures that each class can provide its own description, making it clear what each version of the command does.

#### Extending Command Classes

When extending a class that defines commands, the subclass may inherit and modify the behavior of the base class commands. Multiple `@Description` annotations can be used to document both the inherited and additional functionalities provided by the subclass. This approach helps to differentiate between the base command’s behavior and any new or overridden functionalities introduced in the subclass.

### Methods

- **`CommandInfo#getDescriptions`**: Returns a list of `Description` annotations associated with a command.
- **`CommandInfo#getUsages`**: Returns a list of `Usage` annotations associated with a command.
- **`CommandInfo#getLongDescriptions`**: Returns a list of `LongDescription` annotations associated with a command.

## Relevant Links
* [javadocs](https://cocoa-beans.apartium.net/%version%/commands/net/apartium/cocoabeans/commands/CommandInfo.html)