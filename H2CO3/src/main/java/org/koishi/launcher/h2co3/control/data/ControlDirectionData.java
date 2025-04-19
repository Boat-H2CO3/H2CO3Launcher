package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.util.fakefx.ObservableHelper;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;

@JsonAdapter(ControlDirectionData.Serializer.class)
public class ControlDirectionData implements Cloneable, Observable, CustomControl {

    /**
     * Base info data
     * Contains position and size
     */
    public final ObjectProperty<BaseInfoData> baseInfoProperty = new SimpleObjectProperty<>(this, "baseInfo", new BaseInfoData());
    /**
     * Event data
     */
    public final ObjectProperty<DirectionEventData> eventProperty = new SimpleObjectProperty<>(this, "event", new DirectionEventData());
    /**
     * Unique id
     */
    private final String id;
    /**
     * Control direction style
     */
    private final ObjectProperty<ControlDirectionStyle> styleProperty = new SimpleObjectProperty<>(this, "style", ControlDirectionStyle.DEFAULT_DIRECTION_STYLE);
    private ObservableHelper observableHelper = new ObservableHelper(this);

    public ControlDirectionData(String id) {
        this.id = id;

        addPropertyChangedListener(onInvalidating(this::invalidate));
    }

    public String getId() {
        return id;
    }

    public boolean equals(ControlButtonData data) {
        return data.getId().equals(id);
    }

    public ObjectProperty<ControlDirectionStyle> styleProperty() {
        return styleProperty;
    }

    public ControlDirectionStyle getStyle() {
        return styleProperty.get();
    }

    public void setStyle(ControlDirectionStyle style) {
        styleProperty.set(style);
    }

    public ObjectProperty<BaseInfoData> baseInfoProperty() {
        return baseInfoProperty;
    }

    public BaseInfoData getBaseInfo() {
        return baseInfoProperty.get();
    }

    public void setBaseInfo(BaseInfoData baseInfo) {
        baseInfoProperty.set(baseInfo);
    }

    public ObjectProperty<DirectionEventData> eventProperty() {
        return eventProperty;
    }

    public DirectionEventData getEvent() {
        return eventProperty.get();
    }

    public void setEvent(DirectionEventData event) {
        eventProperty.set(event);
    }

    public void addPropertyChangedListener(InvalidationListener listener) {
        styleProperty.addListener(listener);
        baseInfoProperty.addListener(listener);
        eventProperty.addListener(listener);
    }

    @Override
    public void addListener(InvalidationListener listener) {
        observableHelper.addListener(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        observableHelper.removeListener(listener);
    }

    private void invalidate() {
        try {
            observableHelper.invalidate();
        } catch (NullPointerException ignore) {
        }
    }

    @Override
    public ControlDirectionData clone() {
        ControlDirectionData data = new ControlDirectionData(UUID.randomUUID().toString());
        data.setStyle(getStyle().clone());
        data.setBaseInfo(getBaseInfo().clone());
        data.setEvent(getEvent().clone());
        return data;
    }

    @Override
    public ViewType getType() {
        return ViewType.CONTROL_DIRECTION;
    }

    @Override
    public String getViewId() {
        return getId();
    }

    @Override
    public CustomControl cloneView() {
        ControlDirectionData clone = clone();
        clone.getBaseInfo().setXPosition(0);
        clone.getBaseInfo().setYPosition(0);
        return clone;
    }

    public static class Serializer implements JsonSerializer<ControlDirectionData>, JsonDeserializer<ControlDirectionData> {
        @Override
        public JsonElement serialize(ControlDirectionData src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;
            JsonObject obj = new JsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            obj.addProperty("id", src.getId());
            obj.addProperty("style", src.getStyle().getName());
            obj.add("baseInfo", gson.toJsonTree(src.getBaseInfo()).getAsJsonObject());
            obj.add("event", gson.toJsonTree(src.getEvent()).getAsJsonObject());

            return obj;
        }

        @Override
        public ControlDirectionData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                return null;
            JsonObject obj = (JsonObject) json;

            ControlDirectionData data = new ControlDirectionData(Optional.ofNullable(obj.get("id")).map(JsonElement::getAsString).orElse(UUID.randomUUID().toString()));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!DirectionStyles.isInitialized()) {
                DirectionStyles.init();
            }
            if (obj.get("style").toString().contains("\"name\"")) {
                data.setStyle(gson.fromJson(Optional.ofNullable(obj.get("style")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(ControlDirectionStyle.DEFAULT_DIRECTION_STYLE).getAsJsonObject()), new TypeToken<ControlDirectionStyle>(){}.getType()));
                DirectionStyles.addStyle(data.getStyle());
            } else {
                data.setStyle(DirectionStyles.findStyleByName(obj.get("style").getAsString()));
            }
            data.setBaseInfo(gson.fromJson(Optional.ofNullable(obj.get("baseInfo")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new BaseInfoData()).getAsJsonObject()), new TypeToken<BaseInfoData>(){}.getType()));
            data.setEvent(gson.fromJson(Optional.ofNullable(obj.get("event")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new DirectionEventData()).getAsJsonObject()), new TypeToken<DirectionEventData>(){}.getType()));

            return data;
        }
    }

}
