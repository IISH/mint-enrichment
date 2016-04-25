package gr.ntua.ivml.mint.enrichment.util;

import java.io.*;
import java.util.Properties;

public class Config {
    private final static String PROPERTIES = "pi.properties";
    private static Properties properties = new Properties(System.getProperties());

    public static String get(String key) {
        checkAndRead();
        return properties.getProperty(key);
    }

    static boolean getBoolean(String key) {
        String result = Config.get(key);
        if (result != null) {
            if (result.equalsIgnoreCase("true") || result.equalsIgnoreCase("yes") || result.equalsIgnoreCase("1")) {
                return true;
            }
        }
        return false;
    }

    public static int getInt(String key) {
        String result = Config.get(key);
        if (result != null) {
            return Integer.parseInt(result);
        }
        return 0;
    }

    public static boolean has(String key) {
        checkAndRead();
        return properties.containsKey(key);
    }

    public static String get(String key, String defaultValue) {
        checkAndRead();
        return properties.getProperty(key, defaultValue);
    }

    private static void checkAndRead() {
        readProperties();
    }

    /**
     * readProperties
     * <p>
     * Read the properties from a file or else use the default from the classpath.
     */
    private static void readProperties() {
        final String file = System.getProperty(PROPERTIES);
        if (file == null) {
            try {
                final InputStream inputStream = Config.class.getClassLoader().getResourceAsStream(PROPERTIES);
                if (inputStream == null) {
                    System.err.println(PROPERTIES + " not loaded");
                }
                properties.load(inputStream);
            }
            catch (Exception e) {
                throw new Error("Configuration file " + PROPERTIES + " not found in CLASSPATH", e);
            }
        }
        else {
            try {
                final InputStream inputStream = new FileInputStream(file);
                properties.load(inputStream);
            }
            catch (IOException e) {
                throw new Error("Configuration file " + file + " not loaded.", e);
            }
        }

    }

    private static String[] decomposeJsonQuery(String query) {
        return query.split("\\.");
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                reader.close();
            }
            finally {
                is.close();
            }

            return writer.toString();
        }
        else {
            return "";
        }
    }
}
