package main;

public interface IEvent {
    void setCancelled(boolean cancelled);

    boolean isCancelled();
}