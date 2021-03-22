package fi.fmi.avi.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BuilderHelper {
    private BuilderHelper() {
        throw new AssertionError();
    }

    /**
     * Return an immutable copy of provided list of elements known to be immutable.
     * If elements may not be immutable, use {@link #toImmutableList(List, Function)} instead.
     *
     * @param list
     *         source list
     * @param <T>
     *         base type of elements
     *
     * @return immutable copy
     */
    public static <T> List<T> toImmutableList(final List<T> list) {
        requireNonNull(list, "list");
        return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(list));
    }

    /**
     * Return an immutable copy of provided list converting each element to immutable.
     *
     * @param list
     *         source list
     * @param toImmutable
     *         function converting element to immutable
     * @param <T>
     *         base type of elements
     * @param <I>
     *         immutable type of elements
     *
     * @return immutable copy
     */
    public static <T, I extends T> List<T> toImmutableList(final List<T> list, final Function<T, I> toImmutable) {
        requireNonNull(list, "list");
        requireNonNull(toImmutable, "toImmutable");
        return list.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(list.stream()//
                .map(toImmutable)//
                .collect(Collectors.toList()));
    }
}
