package fi.fmi.avi.converter.tac.lexer.impl;

import fi.fmi.avi.converter.tac.lexer.LexemeVisitor;
/**
 * Created by rinne on 18/01/17.
 */
public abstract class PrioritizedLexemeVisitor implements LexemeVisitor, Comparable<LexemeVisitor> {

    public enum Priority {
        HIGH(1), NORMAL(2), LOW(3);

        private int priority;

        Priority(final int priority) {
            this.priority = priority;
        }

        public int asNumber() {
            return this.priority;
        }
    }

    private Priority priority;

    public PrioritizedLexemeVisitor(final Priority priority) {
        this.priority = priority;
    }

    public PrioritizedLexemeVisitor() {
        this(Priority.NORMAL);
    }

    public Priority getPriority() {
        return this.priority;
    }

    public void setPriority(final Priority prio) {
        this.priority = prio;
    }

    public PrioritizedLexemeVisitor withPriority(Priority prio) {
        this.setPriority(prio);
        return this;
    }

    @Override
    public int compareTo(final LexemeVisitor o) {
        if (o instanceof PrioritizedLexemeVisitor) {
            return this.priority.asNumber() - ((PrioritizedLexemeVisitor) o).priority.asNumber();
        } else {
            return 0;
        }
    }

    public String toString() {
        return new StringBuilder().append("priority:").append(this.priority).toString();
    }
}
