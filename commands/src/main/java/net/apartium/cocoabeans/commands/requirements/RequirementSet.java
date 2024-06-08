/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.CollectionHelpers;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.exception.CommandError;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Immutable set of command requirements
 * @see Requirement
 * @see RequirementFactory
 */
@ApiStatus.Internal
public class RequirementSet implements Set<Requirement> {

    private final Requirement[] requirements;

    public RequirementSet(Collection<Requirement>... requirements) {
        int size = 0;
        for (Collection<Requirement> collection : requirements) {
            size += collection.size();
        }

        this.requirements = new Requirement[size];
        int index = 0;
        for (Collection<Requirement> collection : requirements) {
            for (Requirement requirement : collection) {
                this.requirements[index++] = requirement;
            }
        }
    }

    public RequirementSet(Requirement... requirements) {
        for (int i = 0; i < requirements.length - 1; i++) {
            for (int j = i + 1; j < requirements.length; j++) {
                if (requirements[i].equals(requirements[j])) {
                    throw new RuntimeException("There's duplicated permission in the set: " + requirements[i]);
                }
            }
        }

        this.requirements = Arrays.copyOf(requirements, requirements.length);
    }

    public RequirementSet() {
        this.requirements = new Requirement[0];
    }

    @Override
    public int size() {
        return requirements.length;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object obj) {
        for (Requirement requirement : requirements) {
            if (requirement.equals(obj)) return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return Arrays.toString(requirements);
    }

    @NotNull
    @Override
    public Iterator<Requirement> iterator() {
        return Arrays.stream(requirements).iterator();
    }

    @NotNull
    @Override
    public Requirement @NotNull [] toArray() {
        return Arrays.copyOf(this.requirements, this.requirements.length);
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
        if (ts.length < this.requirements.length)
            return (T[]) Arrays.copyOf(this.requirements, this.requirements.length, ts.getClass());

        System.arraycopy(this.requirements, 0, ts, 0, requirements.length);
        if (ts.length > requirements.length)
            ts[requirements.length] = null;

        return ts;
    }

    @Override
    public boolean add(Requirement permission) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        for (Object value : collection) {
            if (!contains(value)) return false;
        }

        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Requirement> collection) {
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // quick check
        if (o == null || getClass() != o.getClass())
            return false;

        RequirementSet other = (RequirementSet) o;

        if (this.size() != other.size())
            return false;

        if (this.size() == 0)
            return true;


        return CollectionHelpers.equalsArray(this.requirements, other.requirements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(requirements);
    }

    public MeetRequirementResult meetsRequirements(Sender sender, String commandName, String[] args, int depth) {
        for (Requirement requirement : requirements) {
            if (!requirement.meetsRequirement(sender)) {
                RequirementError error = requirement.getError(commandName, args, depth);
                if (error != null)
                    return MeetRequirementResult.ofError(error);

                return MeetRequirementResult.of(false);
            }
        }

        return MeetRequirementResult.of(true);
    }

    public static class MeetRequirementResult {

        private final CommandError error;
        private final boolean meetRequirement;

        private MeetRequirementResult(CommandError error, boolean meetRequirement) {
            this.error = error;
            this.meetRequirement = meetRequirement;
        }

        public CommandError getError() {
            return error;
        }

        public boolean hasError() {
            return error != null;
        }

        public boolean meetRequirement() {
            return meetRequirement;
        }

        public static MeetRequirementResult ofError(CommandError error) {
            return new MeetRequirementResult(error, false);
        }

        public static MeetRequirementResult of(boolean meetRequirement) {
            return new MeetRequirementResult(null, meetRequirement);
        }

    }
}
