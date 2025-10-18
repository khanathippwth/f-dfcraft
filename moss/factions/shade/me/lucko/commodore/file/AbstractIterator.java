/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package moss.factions.shade.me.lucko.commodore.file;

import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class AbstractIterator<T>
implements Iterator<T> {
    private State state = State.NOT_READY;
    private T next;

    protected AbstractIterator() {
    }

    protected abstract T computeNext();

    protected final T endOfData() {
        this.state = State.DONE;
        return null;
    }

    @Override
    public final boolean hasNext() {
        if (this.state == State.FAILED) {
            throw new IllegalStateException();
        }
        switch (this.state) {
            case DONE: {
                return false;
            }
            case READY: {
                return true;
            }
        }
        return this.tryToComputeNext();
    }

    private boolean tryToComputeNext() {
        this.state = State.FAILED;
        try {
            this.next = this.computeNext();
        } catch (Exception exception) {
            throw new RuntimeException("Exception whilst computing next value", exception);
        }
        if (this.state != State.DONE) {
            this.state = State.READY;
            return true;
        }
        return false;
    }

    @Override
    public final T next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        this.state = State.NOT_READY;
        T t = this.next;
        this.next = null;
        return t;
    }

    public final T peek() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }
        return this.next;
    }

    private static enum State {
        READY,
        NOT_READY,
        DONE,
        FAILED;

    }
}

