package jrails;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

public class Model {
    // Static fields
    private static String className; // Class name of the model
    private static final Map<Integer, Object> dbMap = Collections.synchronizedMap(new HashMap<>()); // In-memory database
    private static int counter = 0; // Counter for generating unique IDs
    private static final String dbName = "./db.txt"; // Database file name
    private static final String separator = " ｜ "; // Field separator for file storage

    // Instance fields
    private int id = 0; // Unique ID for each model instance

    // Set the ID for the model instance
    public void setID(int id) {
        this.id = id;
    }

    /**
     * Writes the first row (field names) to the database file.
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be located
     */
    private static void writeFirstRow() throws IOException, ClassNotFoundException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dbName))) {
            StringBuilder fieldNames = new StringBuilder("id");
            for (Field f : Class.forName(className).getFields()) {
                fieldNames.append(separator).append(f.getName());
            }
            bw.write(fieldNames.toString());
            bw.newLine();
        }
    }

    /**
     * Constructs a string representing the field values of an object.
     * @param id the ID of the object
     * @param o the object whose field values are to be retrieved
     * @return a string containing the field values
     * @throws IllegalAccessException if the field is inaccessible
     */
    public static String getFieldString(int id, Object o) throws IllegalAccessException {
        StringBuilder fieldVals = new StringBuilder(String.valueOf(id));
        for (Field f : o.getClass().getFields()) {
            fieldVals.append(separator).append(f.get(o));
        }
        return fieldVals.toString();
    }

    /**
     * Saves the in-memory database (dbMap) to the database file.
     * @throws IllegalAccessException if the field is inaccessible
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class cannot be located
     */
    private static void saveDBMap() throws IllegalAccessException, IOException, ClassNotFoundException {
        writeFirstRow();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dbName, true))) {
            for (Map.Entry<Integer, Object> entry : dbMap.entrySet()) {
                String str = getFieldString(entry.getKey(), entry.getValue());
                bw.write(str);
                bw.newLine();
            }
        }
    }

    /**
     * Ensures the in-memory database (dbMap) is loaded from the database file.
     * @param cls the class of the model
     * @throws IllegalAccessException if the field is inaccessible
     * @throws IOException if an I/O error occurs
     * @throws InstantiationException if the class cannot be instantiated
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws NoSuchMethodException if a matching method is not found
     * @throws NoSuchFieldException if a field is not found
     */
    private static void maintainDBMap(Class<?> cls) throws IllegalAccessException, IOException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        if (dbMap.isEmpty()) {
            loadDBMap(cls);
        }
    }

    /**
     * Converts the input value to the appropriate field type.
     * @param input the input value
     * @param f the field to set
     * @return the converted value
     */
    private static Object getFieldValue(Object input, Field f) {
        String inType = input.getClass().getSimpleName();
        String type = f.getType().getSimpleName();

        switch (type) {
            case "Integer":
            case "int":
                return parseInteger(input, inType);
            case "String":
                return String.valueOf(input);
            case "Boolean":
            case "boolean":
                return parseBoolean(input, inType);
            default:
                throw new IllegalStateException("Unexpected type: " + type);
        }
    }

    private static Integer parseInteger(Object input, String inType) {
        switch (inType) {
            case "Integer":
            case "int":
                return (Integer) input;
            case "String":
                String str = (String) input;
                if (str.equals("false") || str.isEmpty() || str.equals(" ")) return 0;
                if (str.equals("true")) return 1;
                try {
                    return Integer.valueOf(str);
                } catch (NumberFormatException e) {
                    return 1;
                }
            case "Boolean":
            case "boolean":
                return (Boolean) input ? 1 : 0;
            default:
                throw new IllegalStateException("Unexpected type: " + inType);
        }
    }

    private static Boolean parseBoolean(Object input, String inType) {
        switch (inType) {
            case "Integer":
            case "int":
                return (Integer) input != 0;
            case "String":
                return Boolean.valueOf((String) input);
            case "Boolean":
            case "boolean":
                return (Boolean) input;
            default:
                throw new IllegalStateException("Unexpected type: " + inType);
        }
    }

    /**
     * Loads the database file into the in-memory database (dbMap).
     * @param cls the class of the model
     * @throws IllegalAccessException if the field is inaccessible
     * @throws IOException if an I/O error occurs
     * @throws InstantiationException if the class cannot be instantiated
     * @throws InvocationTargetException if the underlying constructor throws an exception
     * @throws NoSuchMethodException if a matching method is not found
     * @throws NoSuchFieldException if a field is not found
     */
    private static void loadDBMap(Class<?> cls) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        try (BufferedReader br = new BufferedReader(new FileReader(dbName))) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                writeFirstRow();
                return;
            }
            line = br.readLine();
            while (line != null && !line.isEmpty()) {
                String[] fields = line.split(separator);
                int id = Integer.parseInt(fields[0]);
                Object instance = cls.getDeclaredConstructor().newInstance();
                cls.getMethod("setID", int.class).invoke(instance, id);

                int i = 1;
                for (Field f : cls.getFields()) {
                    f.set(instance, getFieldValue(fields[i++], f));
                }
                dbMap.put(id, instance);
                line = br.readLine();
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current model instance to the database.
     */
    public void save() {
        try {
            className = this.getClass().getName();
            File db = new File(dbName);
            if (!db.exists()) {
                writeFirstRow();
            } else {
                maintainDBMap(this.getClass());
            }
            if (this.id == 0) {
                synchronized (Model.class) {
                    counter++;
                    this.id = counter;
                }
                dbMap.put(this.id, this);
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(dbName, true))) {
                    bw.write(getFieldString(this.id, this));
                    bw.newLine();
                }
            } else {
                dbMap.put(this.id, this);
                saveDBMap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the ID of the current model instance.
     * @return the ID of the current model instance
     */
    public int id() {
        return this.id;
    }

    /**
     * Finds and returns a model instance by ID.
     * @param <T> the type of the model
     * @param c the class of the model
     * @param id the ID of the model instance
     * @return the model instance, or null if not found
     */
    public static <T> T find(Class<T> c, int id) {
        try {
            maintainDBMap(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!dbMap.containsKey(id)) {
            return null;
        }
        try {
            Object dbEntry = dbMap.get(id);
            T instance = c.getDeclaredConstructor().newInstance();
            c.getMethod("setID", int.class).invoke(instance, id);
            int i = 0;
            for (Field f : c.getFields()) {
                Field dbField = dbEntry.getClass().getFields()[i++];
                f.set(instance, getFieldValue(dbField.get(dbEntry), f));
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of all model instances.
     * @param <T> the type of the model
     * @param c the class of the model
     * @return a list of all model instances
     */
    public static <T> List<T> all(Class<T> c) {
        try {
            maintainDBMap(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<T> list = new ArrayList<>();
        for (Integer id : dbMap.keySet()) {
            list.add(find(c, id));
        }
        return list;
    }

    /**
     * Deletes the current model instance from the database.
     */
    public void destroy() {
        try {
            maintainDBMap(this.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbMap.remove(this.id);
        try {
            saveDBMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets the database by clearing the in-memory database and the database file.
     */
    public static void reset() {
        dbMap.clear();
        try {
            new PrintWriter(dbName).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
