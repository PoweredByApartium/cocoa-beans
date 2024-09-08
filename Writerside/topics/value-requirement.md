# 🔏 Value Requirement

**Table of content:**
- [Introduction](#introduction)
- [Usage](#example-usage)
- [Advantages](#advantages)

## Introduction
The Value Requirement feature introduces a mechanism for retrieving specific values from a requirement, enhancing the flexibility and efficiency of our method handling. This feature aims to address scenarios where it is essential to extract and utilize parameter values from a requirement, such as user authentication details.

## Example Usage 🗿
If we want to create an `@InGame` requirement that will give us `Game` and `GamePlayer` we will need to pass `RequirementResult.Value` with requirement meets as follows
```java
public class InGameImpl implements Requirement {

    private final InGame inGame;

    public InGameImpl(InGame inGame) {
        this.inGame = inGame;
    }
    
    @Override
    public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
        // Do all check and get the game and game player
        if (/* check if not in game*/)
            return RequirementResult.error(error(new BadCommandResponse()));
        
        // otherwise we have game and game player
        return RequirementResult.meet(
                RequirementResult.valueOf(game, Game.class),
                RequirementResult.valueOf(gamePlayer, GamePlayer.class)
        );
    }
}
```

When we want to use it in a command we will do the following thing
```java
@InGame
@SubCommand("test")
public void test(Sender sender, Game game, GamePlayer gamePlayer) {
    // Do something
}
```

In the example above,
the method test is defined with annotations indicating it should be executed within the game context
The Value Requirement feature will enable you to retrieve specific values related to the Game (Like Game & GamePlayer).

## Advantages 💡

 - **Reduced Calculation** 📊: By directly accessing required values from the `RequirementResult`, we minimize the need for additional computations or data retrieval steps.
 - **Decreased Code Duplication** 📉: This feature eliminates repetitive code patterns by streamlining how values are accessed and utilized within methods.
 - **Increased Simplicity** 👍: Simplifies method implementation by allowing direct access to necessary parameter values, improving code readability and maintainability.
