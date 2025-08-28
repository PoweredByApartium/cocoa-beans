package net.apartium.cocoabeans.commands.spigot.parsers;

public record Meow(String cat, int age, Gender gender) {

    public enum Gender {
        MALE, FEMALE, OTHER
    }

    @Override
    public String toString() {
        return "cat: " + cat + ", age: " + age + ", gender: " + gender;
    }
}
