package io.exis.cards.cards;

import junit.framework.Assert;

/**
 * https://github.com/jdeferred
 *
 * Created by luke on 12/1/15.
 */
public class ValueHolder<T> {
    private T value;

    public ValueHolder() {}

    public ValueHolder(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

    public void clear() {
        this.value = null;
    }

    public void assertEquals(T other) {
        Assert.assertEquals(other, value);
    }
}