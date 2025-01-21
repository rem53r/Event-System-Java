# Java Event System

## Overview

A lightweight Java Event System with support for annotation-based event listeners, class registration, and cancelable events.

---

## Features

- **Annotation-Based Listeners**: Use `@EventListener` to define event-handling methods.
- **Event Hooking**: Hook events with `.post(event)` for later use.
- **Class Registration**: Use `.register(class)` and `.unregister(class)` to enable or disable event handling in a class.
- **Cancelable Events**: Allow events to be stopped during propagation.

---

## Usage

### 1. Define a Custom Event

```java
public class CustomEvent extends Event {
   // Your code
}
```

### 2. Create a Listener Class

```java
public class EventListenerClass {

    @EventListener
    public void onEvent(CustomEvent event) {
        if (event.isCancelled()) return;
        System.out.println("Event triggered!");
        event.setCancelled(true); // Optional: Cancel the event
    }
}
```

### 3. Register and Hook Events

```java
YourClass.getEventBus().register(class); // Enable event handling in the class
YourClass.getEventBus().post(event); // Hook the event for use
YourClass.getEventBus().unregister(class); // Disable event handling
```

---

## Example Workflow

```java
YourClass.getEventBus().register(this);
YourClass.getEventBus().post(new CustomEvent());
YourClass.getEventBus().unregister(this);
```

---

## Features Recap

- **`post(Event)`**: Hook an event for listeners.
- **`register(Class)`**: Enable event handling in a class.
- **`unregister(Class)`**: Disable event handling in a class.