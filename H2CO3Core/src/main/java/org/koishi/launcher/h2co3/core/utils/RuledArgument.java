package org.koishi.launcher.h2co3.core.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class RuledArgument implements Argument {

    private final List<CompatibilityRule> rules;
    private final List<String> value;

    public RuledArgument() {
        this(null, null);
    }

    public RuledArgument(List<CompatibilityRule> rules, List<String> args) {
        this.rules = rules;
        this.value = args;
    }

    public List<CompatibilityRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public List<String> getValue() {
        return Collections.unmodifiableList(value);
    }

    @Override
    public Object clone() {
        return new RuledArgument(
                rules == null ? null : new ArrayList<>(rules),
                value == null ? null : new ArrayList<>(value)
        );
    }

    @Override
    public List<String> toString(Map<String, String> keys, Map<String, Boolean> features) {
        if (CompatibilityRule.appliesToCurrentEnvironment(rules, features) && value != null)
            return value.stream()
                    .filter(Objects::nonNull)
                    .map(StringArgument::new)
                    .map(str -> str.toString(keys, features).get(0))
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

    public static class Serializer implements JsonSerializer<RuledArgument>, JsonDeserializer<RuledArgument> {
        @Override
        public JsonElement serialize(RuledArgument src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("rules", context.serialize(src.rules));
            obj.add("value", context.serialize(src.value));
            return obj;
        }

        @Override
        public RuledArgument deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();

            List<CompatibilityRule> rules = context.deserialize(obj.get("rules"), new TypeToken<List<CompatibilityRule>>() {
            }.getType());

            JsonElement valuesElement;
            if (obj.has("values")) {
                valuesElement = obj.get("values");
            } else if (obj.has("value")) {
                valuesElement = obj.get("value");
            } else {
                throw new JsonParseException("RuledArguments instance does not have either value or values member.");
            }

            List<String> values;
            if (valuesElement.isJsonPrimitive()) {
                values = Collections.singletonList(valuesElement.getAsString());
            } else {
                values = context.deserialize(valuesElement, new TypeToken<List<String>>() {
                }.getType());
            }

            return new RuledArgument(rules, values);
        }

    }
}
