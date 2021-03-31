package org.combat.function;

@FunctionalInterface
public interface ThrowableAction {

    void execute() throws Throwable;

    static void execute(ThrowableAction action) throws RuntimeException {
        try {
            action.execute();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
