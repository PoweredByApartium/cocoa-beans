/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.requirements.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RequirementSetTest {

    @Test
    public void constructorSameRequirements() {
        assertThrows(RuntimeException.class, () -> new RequirementSet(new TestRequirement(), new TestRequirement(), new TestRequirement()));
    }

    @Test
    public void constructorAndContains() {
        RandomRequirement randomRequirement = new RandomRequirement(Set.of(new TestSender()));

        RequirementSet requirements = new RequirementSet(new RandomRequirement(new HashSet<>()), randomRequirement);
        assertEquals(requirements.size(), 2);

        assertTrue(requirements.contains(randomRequirement));
    }

    @Test
    public void toArray() {
        AnotherRequirement anotherRequirement0 = new AnotherRequirement();
        AnotherRequirement anotherRequirement1 = new AnotherRequirement();
        AnotherRequirement anotherRequirement2 = new AnotherRequirement();

        RequirementSet requirements = new RequirementSet(anotherRequirement0, anotherRequirement1, anotherRequirement2);
        assertTrue(CollectionHelpers.equalsArray(
                new Requirement[] {anotherRequirement0, anotherRequirement1, anotherRequirement2},
                requirements.toArray()
        ));

        assertTrue(CollectionHelpers.equalsArray(
                new Requirement[] {anotherRequirement0, anotherRequirement1, anotherRequirement2},
                requirements.toArray(new Requirement[0])
        ));

        assertTrue(CollectionHelpers.equalsArray(
                new Requirement[] {anotherRequirement0, anotherRequirement1, anotherRequirement2},
                requirements.toArray(new Requirement[3])
        ));


        assertEquals(requirements.toArray(new Requirement[5]).length, 5);
    }

    @Test
    public void isEmptyTest() {
        RequirementSet requirements = new RequirementSet();
        assertTrue(requirements.isEmpty());
        requirements = new RequirementSet(new AnotherRequirement());
        assertFalse(requirements.isEmpty());
    }

    @Test
    public void addTest() {
        RequirementSet requirements = new RequirementSet();
        assertFalse(requirements.add(new AnotherRequirement()));

        assertEquals(requirements.size(), 0);
    }

    @Test
    public void removeTest() {
        RequirementSet requirements = new RequirementSet(new DifferentRequirement(1), new DifferentRequirement(2));
        assertFalse(requirements.remove(new DifferentRequirement(2)));
    }

    @Test
    public void containsAllTest() {
        RequirementSet requirements = new RequirementSet(new DifferentRequirement(1), new DifferentRequirement(2), new DifferentRequirement(3));

        assertTrue(requirements.containsAll(List.of()));
        assertTrue(requirements.containsAll(List.of(new DifferentRequirement(1))));
        assertTrue(requirements.containsAll(List.of(new DifferentRequirement(1), new DifferentRequirement(2))));
        assertTrue(requirements.containsAll(List.of(new DifferentRequirement(1), new DifferentRequirement(2), new DifferentRequirement(3))));
        assertTrue(requirements.containsAll(List.of(new DifferentRequirement(3), new DifferentRequirement(1), new DifferentRequirement(2))));
        assertTrue(requirements.containsAll(List.of(new DifferentRequirement(2), new DifferentRequirement(1), new DifferentRequirement(2))));

        assertFalse(requirements.containsAll(List.of(new DifferentRequirement(4))));
        assertFalse(requirements.containsAll(List.of(new DifferentRequirement(2), new DifferentRequirement(-21), new AnotherRequirement())));
    }

    @Test
    public void addAllTest() {
        RequirementSet requirements = new RequirementSet();

        assertFalse(requirements.addAll(List.of(new DifferentRequirement(3), new DifferentRequirement(1))));
    }

    @Test
    public void removeAllTest() {
        RequirementSet requirements = new RequirementSet();

        assertFalse(requirements.removeAll(List.of(new DifferentRequirement(1), new DifferentRequirement(2))));
    }

    @Test
    public void retainAllTest() {
        RequirementSet requirements = new RequirementSet();

        assertFalse(requirements.retainAll(List.of(new DifferentRequirement(5))));
    }

    public static class TestRequirement implements Requirement {

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            return RequirementResult.error(new UnmetRequirementResponse(
                    this,
                    context,
                    ""
            ));
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }

    public static class RandomRequirement implements Requirement {

        private final Set<Sender> senders;

        public RandomRequirement(Set<Sender> senders) {
            this.senders = senders;
        }


        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            if (!senders.contains(context.sender()))
                return RequirementResult.error(new UnmetRequirementResponse(
                        this,
                        context,
                        ""
                ));

            return RequirementResult.meet();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            RandomRequirement that = (RandomRequirement) object;
            return Objects.equals(senders, that.senders);
        }

        @Override
        public int hashCode() {
            return Objects.hash(senders);
        }
    }


    public static class AnotherRequirement implements Requirement {

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            return RequirementResult.error(new UnmetRequirementResponse(
                    this,
                    context,
                    ""
            ));
        }

    }

    public record DifferentRequirement(int number) implements Requirement {

        @Override
        public RequirementResult meetsRequirement(RequirementEvaluationContext context) {
            return RequirementResult.error(new UnmetRequirementResponse(
                    this,
                    context,
                    ""
            ));
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            DifferentRequirement that = (DifferentRequirement) object;
            return number == that.number;
        }

        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
    }

}