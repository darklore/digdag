package io.digdag.client.config;

import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ConfigElement
{
    public static ConfigElement copyOf(Config mutableConfig)
    {
        return new ConfigElement(mutableConfig.object.deepCopy());
    }

    @JsonCreator
    public static ConfigElement of(ObjectNode node)
    {
        return new ConfigElement(node.deepCopy());
    }

    public static ConfigElement empty()
    {
        return new ConfigElement(JsonNodeFactory.instance.objectNode());
    }

    public static ConfigElement ofMap(Map<String, String> map)
    {
        ObjectNode js = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, String> pair : map.entrySet()) {
            js.put(pair.getKey(), pair.getValue());
        }
        return new ConfigElement(js);
    }

    //public static ConfigElement fromProperties(Properties props)
    //{
    //    Config builder = new Config(new ObjectMapper());
    //    for (String key : props.stringPropertyNames()) {
    //        Config nest = builder;
    //        String[] nestKeys = key.split("\\.");
    //        try {
    //            for (int i = 0; i < nestKeys.length - 1; i++) {
    //                nest = nest.getNestedOrSetEmpty(nestKeys[i]);
    //            }
    //        }
    //        catch (ConfigException e) {
    //            // if nest1.nest2 = 1 and nest1 = 1 are set together, this error happens. keep nest1.
    //            continue;
    //        }
    //        nest.set(nestKeys[nestKeys.length - 1], props.getProperty(key));
    //    }
    //    return new ConfigElement(builder.object);
    //}

    public static ConfigElement fromJson(String json)
    {
        JsonNode js;
        try {
            js = new ObjectMapper().readTree(json);
        }
        catch (IOException ex) {
            throw new ConfigException(ex);
        }
        if (!js.isObject()) {
            throw new ConfigException("Expected an object but got " + js);
        }
        return new ConfigElement((ObjectNode) js);
    }

    private final ObjectNode object;  // this is immutable

    private ConfigElement(ObjectNode node)
    {
        this.object = node.deepCopy();
    }

    public Config toConfig(ConfigFactory factory)
    {
        // this is a optimization of factory.create(object)
        return new Config(factory.objectMapper, object.deepCopy());
    }

    public Properties toProperties()
    {
        Properties props = new Properties();
        Iterator<Map.Entry<String, JsonNode>> ite = object.fields();
        while (ite.hasNext()) {
            Map.Entry<String, JsonNode> pair = ite.next();
            JsonNode value = pair.getValue();
            if (value.isTextual()) {
                props.put(pair.getKey(), value.asText());
            }
            else {
                props.put(pair.getKey(), value.toString());
            }
        }
        return props;
    }

    @JsonValue
    @Deprecated  // this method is only for ObjectMapper
    public ObjectNode getObjectNode()
    {
        return object;
    }

    @Override
    public String toString()
    {
        return object.toString();
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof Config)) {
            return false;
        }
        return object.equals(((Config) other).object);
    }

    @Override
    public int hashCode()
    {
        return object.hashCode();
    }
}
