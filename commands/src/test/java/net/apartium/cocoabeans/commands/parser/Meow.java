package net.apartium.cocoabeans.commands.parser;

public record Meow(String cat, int age, Gender gender) {

    public enum Gender {
        MALE, FEMALE, OTHER
    }

}
