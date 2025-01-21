package main;

import lombok.Getter;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<? extends Event>, List<EventHandler>> handlers = new ConcurrentHashMap<>();

    public void register(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventListener.class)) {
                if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Invalid event listener method: " + method);
                }

                EventListener annotation = method.getAnnotation(EventListener.class);
                Priority priority = annotation.priority();

                EventHandler handler = new EventHandler(listener, method, priority);

                Class<? extends Event> eventType = method.getParameterTypes()[0].asSubclass(Event.class);
                handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);

                handlers.get(eventType).sort(Comparator.comparing(EventHandler::getPriority));
            }
        }
    }

    public void unregister(Object listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventListener.class)) {
                if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    throw new IllegalArgumentException("Invalid event listener method: " + method);
                }

                Class<? extends Event> eventType = method.getParameterTypes()[0].asSubclass(Event.class);
                List<EventHandler> eventHandlers = handlers.get(eventType);

                if (eventHandlers != null) {
                    eventHandlers.removeIf(handler -> handler.listener.equals(listener));
                    if (eventHandlers.isEmpty()) {
                        handlers.remove(eventType);
                    }
                }
            }
        }
    }

    public void post(Event event) {
        List<EventHandler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null) {
            for (EventHandler handler : eventHandlers) {
                try {
                    handler.invoke(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class EventHandler {
        private final Object listener;
        private final Method method;
        @Getter
        private final Priority priority;

        public EventHandler(Object listener, Method method, Priority priority) {
            this.listener = listener;
            this.method = method;
            this.priority = priority;
            this.method.setAccessible(true);
        }

        public void invoke(Event event) throws Exception {
            method.invoke(listener, event);
        }
    }
}
