package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.fakefx.collections.FXCollections;
import org.koishi.launcher.h2co3core.fakefx.collections.ObservableList;
import org.koishi.launcher.h2co3core.util.fakefx.ObservableHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Optional;

@JsonAdapter(ButtonEventData.Serializer.class)
public class ButtonEventData implements Cloneable, Observable {

    /**
     * Control mouse pointer
     */
    private final BooleanProperty pointerFollowProperty = new SimpleBooleanProperty(this, "pointerFollow", false);
    /**
     * Movable
     */
    private final BooleanProperty movableProperty = new SimpleBooleanProperty(this, "movable", false);
    /**
     * Press event
     */
    private final ObjectProperty<Event> pressEventProperty = new SimpleObjectProperty<>(this, "pressEvent", new Event());
    /**
     * Long press event
     */
    private final ObjectProperty<Event> longPressEventProperty = new SimpleObjectProperty<>(this, "longPressEvent", new Event());
    /**
     * Click event
     */
    private final ObjectProperty<Event> clickEventProperty = new SimpleObjectProperty<>(this, "clickEvent", new Event());
    /**
     * Click event
     */
    private final ObjectProperty<Event> doubleClickEventProperty = new SimpleObjectProperty<>(this, "doubleClickEvent", new Event());
    private ObservableHelper observableHelper = new ObservableHelper(this);

    public ButtonEventData() {
        addPropertyChangedListener(onInvalidating(this::invalidate));
    }

    public BooleanProperty pointerFollowProperty() {
        return pointerFollowProperty;
    }
    
    public boolean isPointerFollow() {
        return pointerFollowProperty.get();
    }
    
    public void setPointerFollow(boolean pointerFollow) {
        pointerFollowProperty.set(pointerFollow);
    }
    
    public BooleanProperty movableProperty() {
        return movableProperty;
    }

    public boolean isMovable() {
        return movableProperty.get();
    }

    public void setMovable(boolean movable) {
        movableProperty.set(movable);
    }

    public ObjectProperty<Event> pressEventProperty() {
        return pressEventProperty;
    }

    public Event getPressEvent() {
        return pressEventProperty.get();
    }

    public void setPressEvent(Event event) {
        pressEventProperty.set(event);
    }

    public ObjectProperty<Event> longPressEventProperty() {
        return longPressEventProperty;
    }

    public Event getLongPressEvent() {
        return longPressEventProperty.get();
    }

    public void setLongPressEvent(Event event) {
        longPressEventProperty.set(event);
    }

    public ObjectProperty<Event> clickEventProperty() {
        return clickEventProperty;
    }

    public Event getClickEvent() {
        return clickEventProperty.get();
    }

    public void setClickEvent(Event event) {
        clickEventProperty.set(event);
    }

    public ObjectProperty<Event> doubleClickEventProperty() {
        return doubleClickEventProperty;
    }

    public Event getDoubleClickEvent() {
        return doubleClickEventProperty.get();
    }

    public void setDoubleClickEvent(Event event) {
        doubleClickEventProperty.set(event);
    }

    public void addPropertyChangedListener(InvalidationListener listener) {
        pointerFollowProperty.addListener(listener);
        movableProperty.addListener(listener);
        pressEventProperty.addListener(listener);
        longPressEventProperty.addListener(listener);
        clickEventProperty.addListener(listener);
        doubleClickEventProperty.addListener(listener);
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
        observableHelper.invalidate();
    }

    @Override
    public ButtonEventData clone() {
        ButtonEventData data = new ButtonEventData();
        data.setPointerFollow(isPointerFollow());
        data.setMovable(isMovable());
        data.setPressEvent(getPressEvent().clone());
        data.setLongPressEvent(getLongPressEvent().clone());
        data.setClickEvent(getClickEvent().clone());
        data.setDoubleClickEvent(getDoubleClickEvent().clone());
        return data;
    }

    public static class Serializer implements JsonSerializer<ButtonEventData>, JsonDeserializer<ButtonEventData> {
        @Override
        public JsonElement serialize(ButtonEventData src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;
            JsonObject obj = new JsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            obj.addProperty("pointerFollow", src.isPointerFollow());
            obj.addProperty("Movable", src.isMovable());
            obj.add("pressEvent", gson.toJsonTree(src.getPressEvent()).getAsJsonObject());
            obj.add("longPressEvent", gson.toJsonTree(src.getLongPressEvent()).getAsJsonObject());
            obj.add("clickEvent", gson.toJsonTree(src.getClickEvent()).getAsJsonObject());
            obj.add("doubleClickEvent", gson.toJsonTree(src.getDoubleClickEvent()).getAsJsonObject());

            return obj;
        }

        @Override
        public ButtonEventData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                return null;
            JsonObject obj = (JsonObject) json;

            ButtonEventData data = new ButtonEventData();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            data.setPointerFollow(Optional.ofNullable(obj.get("pointerFollow")).map(JsonElement::getAsBoolean).orElse(false));
            data.setMovable(Optional.ofNullable(obj.get("Movable")).map(JsonElement::getAsBoolean).orElse(false));
            data.setPressEvent(gson.fromJson(Optional.ofNullable(obj.get("pressEvent")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new Event()).getAsJsonObject()), new TypeToken<Event>(){}.getType()));
            data.setLongPressEvent(gson.fromJson(Optional.ofNullable(obj.get("longPressEvent")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new Event()).getAsJsonObject()), new TypeToken<Event>(){}.getType()));
            data.setClickEvent(gson.fromJson(Optional.ofNullable(obj.get("clickEvent")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new Event()).getAsJsonObject()), new TypeToken<Event>(){}.getType()));
            data.setDoubleClickEvent(gson.fromJson(Optional.ofNullable(obj.get("doubleClickEvent")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new Event()).getAsJsonObject()), new TypeToken<Event>(){}.getType()));

            return data;
        }
    }

    @JsonAdapter(Event.Serializer.class)
    public static class Event implements Cloneable, Observable {

        /**
         * Keep pressing
         */
        private final BooleanProperty autoKeepProperty = new SimpleBooleanProperty(this, "autoKeep", false);
        /**
         * Keep clicking
         */
        private final BooleanProperty autoClickProperty = new SimpleBooleanProperty(this, "autoClick", false);
        /**
         * Open menu
         */
        private final BooleanProperty openMenuProperty = new SimpleBooleanProperty(this, "openMenu", false);
        /**
         * Switch touch mode
         */
        private final BooleanProperty switchTouchModeProperty = new SimpleBooleanProperty(this, "switchTouchMode", false);
        /**
         * Switch mouse mode
         */
        private final BooleanProperty switchMouseModeProperty = new SimpleBooleanProperty(this, "switchMouseMode", false);
        /**
         * Input words
         */
        private final BooleanProperty inputProperty = new SimpleBooleanProperty(this, "input", false);
        /**
         * Open quick input dialog
         */
        private final BooleanProperty quickInputProperty = new SimpleBooleanProperty(this, "quickInput", false);
        /**
         * Output text
         */
        private final StringProperty outputTextProperty = new SimpleStringProperty(this, "outputText", "");
        /**
         * Output keycodes
         */
        private final ObservableList<Integer> outputKeycodesList = FXCollections.observableList(new ArrayList<>());
        /**
         * Switch view group visibility
         */
        private final ObservableList<String> bindViewGroupList = FXCollections.observableList(new ArrayList<>());
        private ObservableHelper observableHelper = new ObservableHelper(this);

        public Event() {
            addPropertyChangedListener(onInvalidating(this::invalidate));
        }

        public BooleanProperty autoKeepProperty() {
            return autoKeepProperty;
        }

        public boolean isAutoKeep() {
            return autoKeepProperty.get();
        }

        public void setAutoKeep(boolean autoKeep) {
            autoKeepProperty.set(autoKeep);
        }

        public BooleanProperty autoClickProperty() {
            return autoClickProperty;
        }

        public boolean isAutoClick() {
            return autoClickProperty.get();
        }

        public void setAutoClick(boolean autoClick) {
            autoClickProperty.set(autoClick);
        }

        public BooleanProperty openMenuProperty() {
            return openMenuProperty;
        }

        public boolean isOpenMenu() {
            return openMenuProperty.get();
        }

        public void setOpenMenu(boolean openMenu) {
            openMenuProperty.set(openMenu);
        }

        public BooleanProperty switchTouchModeProperty() {
            return switchTouchModeProperty;
        }

        public boolean isSwitchTouchMode() {
            return switchTouchModeProperty.get();
        }

        public void setSwitchTouchMode(boolean switchTouchMode) {
            switchTouchModeProperty.set(switchTouchMode);
        }

        public BooleanProperty switchMouseModeProperty() {
            return switchMouseModeProperty;
        }

        public boolean isSwitchMouseMode() {
            return switchMouseModeProperty.get();
        }

        public void setSwitchMouseMode(boolean switchMouseMode) {
            switchMouseModeProperty.set(switchMouseMode);
        }

        public BooleanProperty inputProperty() {
            return inputProperty;
        }

        public boolean isInput() {
            return inputProperty.get();
        }

        public void setInput(boolean input) {
            inputProperty.set(input);
        }

        public BooleanProperty quickInputProperty() {
            return quickInputProperty;
        }

        public boolean isQuickInput() {
            return quickInputProperty.get();
        }

        public void setQuickInput(boolean quickInput) {
            quickInputProperty.set(quickInput);
        }

        public StringProperty outputTextProperty() {
            return outputTextProperty;
        }

        public String getOutputText() {
            return outputTextProperty.get();
        }

        public void setOutputText(String outputText) {
            outputTextProperty.set(outputText);
        }

        public ObservableList<Integer> outputKeycodesList() {
            return outputKeycodesList;
        }

        public void setOutputKeycodes(ObservableList<Integer> outputKeycodes) {
            outputKeycodesList.setAll(outputKeycodes);
        }

        public ObservableList<String> bindViewGroupList() {
            return bindViewGroupList;
        }

        public void setBindViewGroup(ObservableList<String> bindViewGroup) {
            bindViewGroupList.setAll(bindViewGroup);
        }

        public void addPropertyChangedListener(InvalidationListener listener) {
            autoKeepProperty.addListener(listener);
            autoClickProperty.addListener(listener);
            openMenuProperty.addListener(listener);
            switchTouchModeProperty.addListener(listener);
            switchMouseModeProperty.addListener(listener);
            inputProperty.addListener(listener);
            quickInputProperty.addListener(listener);
            outputTextProperty.addListener(listener);
            outputKeycodesList.addListener(listener);
            bindViewGroupList.addListener(listener);
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
            observableHelper.invalidate();
        }

        @Override
        public Event clone() {
            Event event = new Event();
            event.setAutoKeep(isAutoKeep());
            event.setAutoClick(isAutoClick());
            event.setOpenMenu(isOpenMenu());
            event.setSwitchTouchMode(isSwitchTouchMode());
            event.setSwitchMouseMode(isSwitchMouseMode());
            event.setInput(isInput());
            event.setQuickInput(isQuickInput());
            event.setOutputText(getOutputText());
            event.setOutputKeycodes(outputKeycodesList());
            event.setBindViewGroup(bindViewGroupList());
            return event;
        }

        public static class Serializer implements JsonSerializer<Event>, JsonDeserializer<Event> {
            @Override
            public JsonElement serialize(Event src, Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) return JsonNull.INSTANCE;
                JsonObject obj = new JsonObject();

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                obj.addProperty("autoKeep", src.isAutoKeep());
                obj.addProperty("autoClick", src.isAutoClick());
                obj.addProperty("openMenu", src.isOpenMenu());
                obj.addProperty("switchTouchMode", src.isSwitchTouchMode());
                obj.addProperty("switchMouseMode", src.isSwitchMouseMode());
                obj.addProperty("input", src.isInput());
                obj.addProperty("quickInput", src.isQuickInput());
                obj.addProperty("outputText", src.getOutputText());
                obj.add("outputKeycodes", gson.toJsonTree(new ArrayList<>(src.outputKeycodesList()), new TypeToken<ArrayList<Integer>>(){}.getType()).getAsJsonArray());
                obj.add("bindViewGroup", gson.toJsonTree(new ArrayList<>(src.bindViewGroupList()), new TypeToken<ArrayList<String>>(){}.getType()).getAsJsonArray());

                return obj;
            }

            @Override
            public Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                    return null;
                JsonObject obj = (JsonObject) json;

                Event event = new Event();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                event.setAutoKeep(Optional.ofNullable(obj.get("autoKeep")).map(JsonElement::getAsBoolean).orElse(false));
                event.setAutoClick(Optional.ofNullable(obj.get("autoClick")).map(JsonElement::getAsBoolean).orElse(false));
                event.setOpenMenu(Optional.ofNullable(obj.get("openMenu")).map(JsonElement::getAsBoolean).orElse(false));
                event.setSwitchTouchMode(Optional.ofNullable(obj.get("switchTouchMode")).map(JsonElement::getAsBoolean).orElse(false));
                event.setSwitchMouseMode(Optional.ofNullable(obj.get("switchMouseMode")).map(JsonElement::getAsBoolean).orElse(false));
                event.setInput(Optional.ofNullable(obj.get("input")).map(JsonElement::getAsBoolean).orElse(false));
                event.setQuickInput(Optional.ofNullable(obj.get("quickInput")).map(JsonElement::getAsBoolean).orElse(false));
                event.setOutputText(Optional.ofNullable(obj.get("outputText")).map(JsonElement::getAsString).orElse(""));
                event.setOutputKeycodes(FXCollections.observableList(gson.fromJson(Optional.ofNullable(obj.get("outputKeycodes")).map(JsonElement::getAsJsonArray).orElse(new JsonArray()), new TypeToken<ArrayList<Integer>>(){}.getType())));
                event.setBindViewGroup(FXCollections.observableList(gson.fromJson(Optional.ofNullable(obj.get("bindViewGroup")).map(JsonElement::getAsJsonArray).orElse(new JsonArray()), new TypeToken<ArrayList<String>>(){}.getType())));

                return event;
            }
        }
        
    }

}
