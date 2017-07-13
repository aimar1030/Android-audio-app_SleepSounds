package com.zenlabs.sleepsounds.plistparser;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * Created by fedoro on 5/12/16.
 */
public class Dict extends PListObject {

    private static final long serialVersionUID = -556589348083152733L;

    public static final String DOT = ".";
    protected Map<String, PListObject> configMap;

    public Dict() {
        configMap = new TreeMap<String, PListObject>();
        setType(PListObjectType.DICT);
    }

    /**
     * Put the config value with the given key.
     *
     * @param key
     * @param value
     */
    public void putConfig(String key, PListObject value) {
        configMap.put(key, value);
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    /**
     * @return the configMap
     */
    public Map<String, PListObject> getConfigMap() {
        return configMap;
    }

    /**
     * @param configMap
     *            the configMap to set
     */
    public void setConfigMap(Map<String, PListObject> configMap) {
        this.configMap = configMap;
    }

    /**
     * Utility method which tokenizes the given keyName using the "." delimiter
     * and then looks up each token in the configuration dictionary. If the
     * token key points to a dictionary then it proceeds to the next token key
     * and looks up value of the token key in the dictionary it found from the
     * previous token key.
     *
     * @param key
     *            The fully qualified key text.
     * @return The Object value associated with the given key, or null if the
     *         key does not exist.
     */
    @SuppressWarnings("unchecked")
    public <E extends PListObject> E getConfigurationObject(String key) {
        StringTokenizer st = new StringTokenizer(key, DOT);

        if (st.hasMoreTokens()) {
            Map<String, PListObject> dict = configMap;
            Object obj;
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                obj = dict.get(token);
                if (obj instanceof Dict) {
                    dict = ((Dict) obj).getConfigMap();
                    continue;
                }
                return (E) obj;
            }
        }
        return (E) configMap.get(key);
    }

    /**
     * Get an String configuration value for the given key.
     *
     * @param key
     *            The text of the key to look up in the configuration
     *            dictionary.
     * @return The String value of the specified key.
     */
    public MyString getConfiguration(String key) {
        return (MyString) getConfigurationObject(key);
    }

    public False getConfigurationFalse(String key)  {
        return (False)getConfigurationObject(key);
    }

    /**
     * Get a String configuration value for the given key. If there is no value
     * for the given key, then return the default value.
     *
     * @param key
     *            The text of the key to look up in the configuration
     *            dictionary.
     * @param defaultValue
     *            The default value to return if they key has no associated
     *            value.
     * @return The String value of the specified key, or defaultValue if the
     *         value for keyName is null.
     */
    public MyString getConfigurationWithDefault(String key,
                                                MyString defaultValue) {
        MyString value = getConfiguration(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    /**
     * Get an Integer configuration value for the given key.
     *
     * @param key
     *            The text of the key to look up in the configuration
     *            dictionary.
     * @return The Integer value of the specified key.
     */
    public Integer getConfigurationInteger(String key) {
        return (Integer) getConfigurationObject(key);
    }

    /**
     * Get an Integer configuration value for the given key. If there is no
     * value for the given key, then return the default value.
     *
     * @param key
     *            The text of the key to look up in the configuration
     *            dictionary.
     * @param defaultValue
     *            The default value to return if they key has no associated
     *            value.
     * @return The Integer value of the specified key, or defaultValue if the
     *         value for keyName is null.
     */
    public Integer getConfigurationIntegerWithDefault(String key,
                                                      Integer defaultValue) {
        Integer value = getConfigurationInteger(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
    }

    public Real getConfigurationReal(String key)    {
        return (Real)getConfigurationObject(key);
    }
    /**
     * Get an Integer configuration value for the given key.
     *
     * @param key
     *            The text of the key to look up in the configuration
     *            dictionary.
     * @return The Integer value of the specified key.
     */
    public Array getConfigurationArray(String key) {
        return (Array) getConfigurationObject(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        Set<String> keys = configMap.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            retVal.append("key=").append(key)
                    .append(configMap.get(key).toString());
        }
        return retVal.toString();
    }
}
