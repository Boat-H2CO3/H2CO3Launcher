package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import org.koishi.launcher.h2co3launcher.keycodes.H2CO3LauncherKeycodes;
import org.koishi.launcher.h2co3core.fakefx.beans.InvalidationListener;
import org.koishi.launcher.h2co3core.fakefx.beans.Observable;
import org.koishi.launcher.h2co3core.fakefx.beans.property.BooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleBooleanProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.util.fakefx.ObservableHelper;

import java.lang.reflect.Type;
import java.util.Optional;

@JsonAdapter(DirectionEventData.Serializer.class)
public class DirectionEventData implements Cloneable, Observable {
    
    /**
     * Up keycode
     * Default is W
     */
    private final IntegerProperty upKeycodeProperty = new SimpleIntegerProperty(this, "upKeycode", H2CO3LauncherKeycodes.KEY_W);
    /**
     * Down keycode
     * Default is S
     */
    private final IntegerProperty downKeycodeProperty = new SimpleIntegerProperty(this, "downKeycode", H2CO3LauncherKeycodes.KEY_S);
    /**
     * Left keycode
     * Default is A
     */
    private final IntegerProperty leftKeycodeProperty = new SimpleIntegerProperty(this, "leftKeycode", H2CO3LauncherKeycodes.KEY_A);
    /**
     * Right keycode
     * Default is D
     */
    private final IntegerProperty rightKeycodeProperty = new SimpleIntegerProperty(this, "rightKeycode", H2CO3LauncherKeycodes.KEY_D);
    /**
     * Follow option (only rocker style)
     */
    private final ObjectProperty<FollowOption> followOptionProperty = new SimpleObjectProperty<>(this, "followOption", FollowOption.CENTER_FOLLOW);
    /**
     * Double click center to enable sneak
     */
    private final BooleanProperty sneakProperty = new SimpleBooleanProperty(this, "sneak", true);
    /**
     * Sneak keycode
     */
    private final IntegerProperty sneakKeycodeProperty = new SimpleIntegerProperty(this, "sneakKeycode", H2CO3LauncherKeycodes.KEY_LEFTSHIFT);
    private ObservableHelper observableHelper = new ObservableHelper(this);

    public DirectionEventData() {
        addPropertyChangedListener(onInvalidating(this::invalidate));
    }

    public IntegerProperty upKeycodeProperty() {
        return upKeycodeProperty;
    }

    public int getUpKeycode() {
        return upKeycodeProperty.get();
    }

    public void setUpKeycode(int keycode) {
        upKeycodeProperty.set(keycode);
    }

    public IntegerProperty downKeycodeProperty() {
        return downKeycodeProperty;
    }

    public int getDownKeycode() {
        return downKeycodeProperty.get();
    }

    public void setDownKeycode(int keycode) {
        downKeycodeProperty.set(keycode);
    }

    public IntegerProperty leftKeycodeProperty() {
        return leftKeycodeProperty;
    }

    public int getLeftKeycode() {
        return leftKeycodeProperty.get();
    }

    public void setLeftKeycode(int keycode) {
        leftKeycodeProperty.set(keycode);
    }

    public IntegerProperty rightKeycodeProperty() {
        return rightKeycodeProperty;
    }

    public int getRightKeycode() {
        return rightKeycodeProperty.get();
    }

    public void setRightKeycode(int keycode) {
        rightKeycodeProperty.set(keycode);
    }

    public ObjectProperty<FollowOption> followOptionProperty() {
        return followOptionProperty;
    }

    public FollowOption getFollowOption() {
        return followOptionProperty.get();
    }

    public void setFollowOption(FollowOption followOption) {
        followOptionProperty.set(followOption);
    }

    public BooleanProperty sneakProperty() {
        return sneakProperty;
    }

    public boolean isSneak() {
        return sneakProperty.get();
    }

    public void setSneak(boolean sneak) {
        sneakProperty.set(sneak);
    }

    public IntegerProperty sneakKeycodeProperty() {
        return sneakKeycodeProperty;
    }

    public int getSneakKeycode() {
        return sneakKeycodeProperty.get();
    }

    public void setSneakKeycode(int keycode) {
        sneakKeycodeProperty.set(keycode);
    }

    public void addPropertyChangedListener(InvalidationListener listener) {
        upKeycodeProperty.addListener(listener);
        downKeycodeProperty.addListener(listener);
        leftKeycodeProperty.addListener(listener);
        rightKeycodeProperty.addListener(listener);
        followOptionProperty.addListener(listener);
        sneakProperty.addListener(listener);
        sneakKeycodeProperty.addListener(listener);
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
    public DirectionEventData clone() {
        DirectionEventData data = new DirectionEventData();
        data.setUpKeycode(getUpKeycode());
        data.setDownKeycode(getDownKeycode());
        data.setLeftKeycode(getLeftKeycode());
        data.setRightKeycode(getRightKeycode());
        data.setFollowOption(getFollowOption());
        data.setSneak(isSneak());
        data.setSneakKeycode(getSneakKeycode());
        return data;
    }

    public enum FollowOption {
        FIXED,
        CENTER_FOLLOW,
        FOLLOW
    }

    public static class Serializer implements JsonSerializer<DirectionEventData>, JsonDeserializer<DirectionEventData> {
        @Override
        public JsonElement serialize(DirectionEventData src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;
            JsonObject obj = new JsonObject();

            obj.addProperty("upKeycode", src.getUpKeycode());
            obj.addProperty("downKeycode", src.getDownKeycode());
            obj.addProperty("leftKeycode", src.getLeftKeycode());
            obj.addProperty("rightKeycode", src.getRightKeycode());
            obj.addProperty("followOption", src.getFollowOption().toString());
            obj.addProperty("sneak", src.isSneak());
            obj.addProperty("sneakKeycode", src.getSneakKeycode());

            return obj;
        }

        @Override
        public DirectionEventData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                return null;
            JsonObject obj = (JsonObject) json;

            DirectionEventData data = new DirectionEventData();

            data.setUpKeycode(Optional.ofNullable(obj.get("upKeycode")).map(JsonElement::getAsInt).orElse(H2CO3LauncherKeycodes.KEY_W));
            data.setDownKeycode(Optional.ofNullable(obj.get("downKeycode")).map(JsonElement::getAsInt).orElse(H2CO3LauncherKeycodes.KEY_S));
            data.setLeftKeycode(Optional.ofNullable(obj.get("leftKeycode")).map(JsonElement::getAsInt).orElse(H2CO3LauncherKeycodes.KEY_A));
            data.setRightKeycode(Optional.ofNullable(obj.get("rightKeycode")).map(JsonElement::getAsInt).orElse(H2CO3LauncherKeycodes.KEY_D));
            data.setFollowOption(getFollowOption(Optional.ofNullable(obj.get("followOption")).map(JsonElement::getAsString).orElse(FollowOption.CENTER_FOLLOW.toString())));
            data.setSneak(Optional.ofNullable(obj.get("sneak")).map(JsonElement::getAsBoolean).orElse(true));
            data.setSneakKeycode(Optional.ofNullable(obj.get("sneakKeycode")).map(JsonElement::getAsInt).orElse(H2CO3LauncherKeycodes.KEY_LEFTSHIFT));

            return data;
        }

        public FollowOption getFollowOption(String option) {
            if (option.equals(FollowOption.FIXED.toString())) {
                return FollowOption.FIXED;
            } else if (option.equals(FollowOption.FOLLOW.toString())) {
                return FollowOption.FOLLOW;
            } else {
                return FollowOption.CENTER_FOLLOW;
            }
        }
    }

}
