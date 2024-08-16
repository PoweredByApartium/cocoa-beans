package net.apartium.cocoabeans.data;

import java.util.HashSet;
import java.util.Set;

/* package-private */ class SetTestingManager<E> extends AbstractCollectionManager<Set<E>, E> {

    public SetTestingManager() {
        super(new HashSet<>());
    }
}