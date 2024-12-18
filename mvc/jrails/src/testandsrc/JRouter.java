package jrails;

import java.lang.reflect.*;
import java.util.*;

public class JRouter {
    // Define valid HTTP verbs
    private static final String[] VALID_VERBS = { "GET", "HEAD", "POST", "PUT", "DELETE", "CONNECT", "OPTIONS", "TRACE", "PATCH" };
    private static final Set<String> VALID_VERB_SET = new HashSet<>(Arrays.asList(VALID_VERBS));

    /**
     * Mapping router path to controller method name
     * key: verb
     * value: map<path, method_name>
     */
    private static final Map<String, Map<String, String>> ROUTES = new HashMap<>();
    private static String className;

    /**
     * Adds a route to the router.
     *
     * @param verb   the HTTP verb, e.g., "GET", "POST"
     * @param path   the path to the page, e.g., "/path-to-page"
     * @param clazz  the controller class to use
     * @param method the controller method name
     */
    public synchronized void addRoute(String verb, String path, Class<?> clazz, String method) {
        // Validate if the verb is valid
        if (!VALID_VERB_SET.contains(verb)) {
            throw new IllegalArgumentException("Error: Invalid Route Verb!");
        }

        // Store the class name of the controller
        className = clazz.getName();

        // Add the route to the ROUTES map
        ROUTES.computeIfAbsent(verb, k -> new HashMap<>()).put(path, method);
    }

    /**
     * Helper function to get the controller method name based on the verb and path.
     *
     * @param verb the HTTP verb
     * @param path the path to the page
     * @return the controller method name
     */
    private String getControllerMethod(String verb, String path) {
        // Get the routes for the given verb
        Map<String, String> verbRoutes = ROUTES.get(verb);
        if (verbRoutes == null || !verbRoutes.containsKey(path)) {
            throw new UnsupportedOperationException("No route found for verb " + verb + " and path " + path);
        }
        // Return the method name for the given path
        return verbRoutes.get(path);
    }

    /**
     * Gets the route in the form "clazz#method" corresponding to the verb and URN.
     *
     * @param verb the HTTP verb
     * @param path the path to the page
     * @return the route as "clazz#method", or null if no such route exists
     */
    public String getRoute(String verb, String path) {
        try {
            // Return the class name and method name
            return className + "#" + getControllerMethod(verb, path);
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }

    /**
     * Calls the appropriate controller method and returns the result.
     *
     * @param verb   the HTTP verb
     * @param path   the path to the page
     * @param params the parameters to pass to the controller method
     * @return the result of the controller method
     */
    public Html route(String verb, String path, Map<String, String> params) {
        try {
            // Get the controller class and method
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(getControllerMethod(verb, path), Map.class);

            // Invoke the method and return the result
            return (Html) method.invoke(clazz.getDeclaredConstructor().newInstance(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Error invoking controller method", e);
        }
    }
}
