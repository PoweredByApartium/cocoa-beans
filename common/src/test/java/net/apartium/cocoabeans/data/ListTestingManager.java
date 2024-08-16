package net.apartium.cocoabeans.data;

import java.util.ArrayList;
import java.util.List;

/* package-private */ class ListTestingManager<E> extends AbstractCollectionManager<List<E>, E> {

    public ListTestingManager() {
        super(new ArrayList<>());
    }
}