package org.koishi.launcher.h2co3.control.data;

import static org.koishi.launcher.h2co3.util.FXUtils.onInvalidating;

import android.graphics.Color;

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
import org.koishi.launcher.h2co3core.fakefx.beans.property.IntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.ObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleIntegerProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleObjectProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.SimpleStringProperty;
import org.koishi.launcher.h2co3core.fakefx.beans.property.StringProperty;
import org.koishi.launcher.h2co3core.util.fakefx.ObservableHelper;

import java.util.Optional;

@JsonAdapter(ControlDirectionStyle.Serializer.class)
public class ControlDirectionStyle implements Cloneable, Observable {

    public static final ControlDirectionStyle DEFAULT_DIRECTION_STYLE = new ControlDirectionStyle("Default");
    /**
     * Style name
     */
    private final StringProperty nameProperty = new SimpleStringProperty(this, "name", "");
    /**
     * Style type
     */
    private final ObjectProperty<Type> styleTypeProperty = new SimpleObjectProperty<>(this, "styleType", Type.BUTTON);
    /**
     * Button style
     */
    private final ObjectProperty<ButtonStyle> buttonStyleProperty = new SimpleObjectProperty<>(this, "buttonStyle", new ButtonStyle());
    /**
     * Rocker style
     */
    private final ObjectProperty<RockerStyle> rockerStyleProperty = new SimpleObjectProperty<>(this, "rockerStyle", new RockerStyle());
    private ObservableHelper observableHelper = new ObservableHelper(this);

    public ControlDirectionStyle(String name) {
        this.nameProperty.set(name);

        addPropertyChangedListener(onInvalidating(this::invalidate));
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public String getName() {
        return nameProperty.get();
    }

    public void setName(String name) {
        nameProperty.set(name);
    }

    public ObjectProperty<Type> styleTypeProperty() {
        return styleTypeProperty;
    }

    public Type getStyleType() {
        return styleTypeProperty.get();
    }

    public void setStyleType(Type type) {
        styleTypeProperty.set(type);
    }

    public ObjectProperty<ButtonStyle> buttonStyleProperty() {
        return buttonStyleProperty;
    }

    public ButtonStyle getButtonStyle() {
        return buttonStyleProperty.get();
    }

    public void setButtonStyle(ButtonStyle buttonStyle) {
        buttonStyleProperty.set(buttonStyle);
        buttonStyle.addListener(onInvalidating(this::invalidate));
    }

    public ObjectProperty<RockerStyle> rockerStyleProperty() {
        return rockerStyleProperty;
    }

    public RockerStyle getRockerStyle() {
        return rockerStyleProperty.get();
    }

    public void setRockerStyle(RockerStyle rockerStyle) {
        rockerStyleProperty.set(rockerStyle);
        rockerStyle.addListener(onInvalidating(this::invalidate));
    }

    public void addPropertyChangedListener(InvalidationListener listener) {
        nameProperty.addListener(listener);
        styleTypeProperty.addListener(listener);
        buttonStyleProperty.addListener(listener);
        rockerStyleProperty.addListener(listener);
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
    public ControlDirectionStyle clone() {
        ControlDirectionStyle style = new ControlDirectionStyle(getName());
        style.setStyleType(getStyleType());
        style.setButtonStyle(getButtonStyle().clone());
        style.setRockerStyle(getRockerStyle().clone());
        return style;
    }

    public enum Type {
        BUTTON,
        ROCKER
    }

    public static class Serializer implements JsonSerializer<ControlDirectionStyle>, JsonDeserializer<ControlDirectionStyle> {
        @Override
        public JsonElement serialize(ControlDirectionStyle src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) return JsonNull.INSTANCE;
            JsonObject obj = new JsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            obj.addProperty("name", src.getName());
            obj.addProperty("styleType", src.getStyleType().toString());
            obj.add("buttonStyle", gson.toJsonTree(src.getButtonStyle()).getAsJsonObject());
            obj.add("rockerStyle", gson.toJsonTree(src.getRockerStyle()).getAsJsonObject());

            return obj;
        }

        @Override
        public ControlDirectionStyle deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                return null;
            JsonObject obj = (JsonObject) json;

            ControlDirectionStyle style = new ControlDirectionStyle(Optional.ofNullable(obj.get("name")).map(JsonElement::getAsString).orElse(""));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            style.setStyleType(Optional.ofNullable(obj.get("styleType")).map(JsonElement::getAsString).orElse(Type.BUTTON.toString()).equals(Type.ROCKER.toString()) ? Type.ROCKER : Type.BUTTON);
            style.setButtonStyle(gson.fromJson(Optional.ofNullable(obj.get("buttonStyle")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new ButtonStyle()).getAsJsonObject()), new TypeToken<ButtonStyle>(){}.getType()));
            style.setRockerStyle(gson.fromJson(Optional.ofNullable(obj.get("rockerStyle")).map(JsonElement::getAsJsonObject).orElse(gson.toJsonTree(new RockerStyle()).getAsJsonObject()), new TypeToken<RockerStyle>(){}.getType()));

            return style;
        }
    }

    @JsonAdapter(ButtonStyle.Serializer.class)
    public static class ButtonStyle implements Cloneable, Observable {

        /**
         * Button interval
         * 10 times the actual value
         */
        private final IntegerProperty intervalProperty = new SimpleIntegerProperty(this, "interval", 50);
        /**
         * Button display text color
         */
        private final IntegerProperty textColorProperty = new SimpleIntegerProperty(this, "textColor", Color.WHITE);
        /**
         * Button display text size
         */
        private final IntegerProperty textSizeProperty = new SimpleIntegerProperty(this, "textSize", 12);
        /**
         * Button stroke width
         * 10 times the actual value
         */
        private final IntegerProperty strokeWidthProperty = new SimpleIntegerProperty(this, "strokeWidth", 10);
        /**
         * Button stroke color
         */
        private final IntegerProperty strokeColorProperty = new SimpleIntegerProperty(this, "strokeColor", Color.DKGRAY);
        /**
         * Button corner radius
         * 10 times the actual value
         */
        private final IntegerProperty cornerRadiusProperty = new SimpleIntegerProperty(this, "cornerRadius", 100);
        /**
         * Button fill color
         */
        private final IntegerProperty fillColorProperty = new SimpleIntegerProperty(this, "fillColor", Color.TRANSPARENT);
        /**
         * Button display text color (pressed)
         */
        private final IntegerProperty textColorPressedProperty = new SimpleIntegerProperty(this, "textColorPressed", Color.WHITE);
        /**
         * Button display text size (pressed)
         */
        private final IntegerProperty textSizePressedProperty = new SimpleIntegerProperty(this, "textSizePressed", 12);
        /**
         * Button stroke width (pressed)
         * 10 times the actual value
         */
        private final IntegerProperty strokeWidthPressedProperty = new SimpleIntegerProperty(this, "strokeWidthPressed", 10);
        /**
         * Button stroke color (pressed)
         */
        private final IntegerProperty strokeColorPressedProperty = new SimpleIntegerProperty(this, "strokeColorPressed", Color.DKGRAY);
        /**
         * Button corner radius (pressed)
         * 10 times the actual value
         */
        private final IntegerProperty cornerRadiusPressedProperty = new SimpleIntegerProperty(this, "cornerRadiusPressed", 100);
        /**
         * Button fill color (pressed)
         */
        private final IntegerProperty fillColorPressedProperty = new SimpleIntegerProperty(this, "fillColorPressed", Color.LTGRAY);
        private ObservableHelper observableHelper = new ObservableHelper(this);

        public ButtonStyle() {
            addPropertyChangedListener(onInvalidating(this::invalidate));
        }

        public IntegerProperty intervalProperty() {
            return intervalProperty;
        }

        public int getInterval() {
            return intervalProperty.get();
        }

        public void setInterval(int interval) {
            intervalProperty.set(interval);
        }

        public IntegerProperty textColorProperty() {
            return textColorProperty;
        }

        public int getTextColor() {
            return textColorProperty.get();
        }

        public void setTextColor(int color) {
            textColorProperty.set(color);
        }

        public IntegerProperty textSizeProperty() {
            return textSizeProperty;
        }

        public int getTextSize() {
            return textSizeProperty.get();
        }

        public void setTextSize(int size) {
            textSizeProperty.set(size);
        }

        public IntegerProperty strokeWidthProperty() {
            return strokeWidthProperty;
        }

        public int getStrokeWidth() {
            return strokeWidthProperty.get();
        }

        public void setStrokeWidth(int strokeWidth) {
            strokeWidthProperty.set(strokeWidth);
        }

        public IntegerProperty strokeColorProperty() {
            return strokeColorProperty;
        }

        public int getStrokeColor() {
            return strokeColorProperty.get();
        }

        public void setStrokeColor(int strokeColor) {
            strokeColorProperty.set(strokeColor);
        }

        public IntegerProperty cornerRadiusProperty() {
            return cornerRadiusProperty;
        }

        public int getCornerRadius() {
            return cornerRadiusProperty.get();
        }

        public void setCornerRadius(int cornerRadius) {
            cornerRadiusProperty.set(cornerRadius);
        }

        public IntegerProperty fillColorProperty() {
            return fillColorProperty;
        }

        public int getFillColor() {
            return fillColorProperty.get();
        }

        public void setFillColor(int fillColor) {
            fillColorProperty.set(fillColor);
        }

        public IntegerProperty textColorPressedProperty() {
            return textColorPressedProperty;
        }

        public int getTextColorPressed() {
            return textColorPressedProperty.get();
        }

        public void setTextColorPressed(int colorPressed) {
            textColorPressedProperty.set(colorPressed);
        }

        public IntegerProperty textSizePressedProperty() {
            return textSizePressedProperty;
        }

        public int getTextSizePressed() {
            return textSizePressedProperty.get();
        }

        public void setTextSizePressed(int sizePressed) {
            textSizePressedProperty.set(sizePressed);
        }

        public IntegerProperty strokeWidthPressedProperty() {
            return strokeWidthPressedProperty;
        }

        public int getStrokeWidthPressed() {
            return strokeWidthPressedProperty.get();
        }

        public void setStrokeWidthPressed(int strokeWidthPressed) {
            strokeWidthPressedProperty.set(strokeWidthPressed);
        }

        public IntegerProperty strokeColorPressedProperty() {
            return strokeColorPressedProperty;
        }

        public int getStrokeColorPressed() {
            return strokeColorPressedProperty.get();
        }

        public void setStrokeColorPressed(int strokeColorPressed) {
            strokeColorPressedProperty.set(strokeColorPressed);
        }

        public IntegerProperty cornerRadiusPressedProperty() {
            return cornerRadiusPressedProperty;
        }

        public int getCornerRadiusPressed() {
            return cornerRadiusPressedProperty.get();
        }

        public void setCornerRadiusPressed(int cornerRadiusPressed) {
            cornerRadiusPressedProperty.set(cornerRadiusPressed);
        }

        public IntegerProperty fillColorPressedProperty() {
            return fillColorPressedProperty;
        }

        public int getFillColorPressed() {
            return fillColorPressedProperty.get();
        }

        public void setFillColorPressed(int fillColorPressed) {
            fillColorPressedProperty.set(fillColorPressed);
        }

        public void addPropertyChangedListener(InvalidationListener listener) {
            intervalProperty.addListener(listener);
            textColorProperty.addListener(listener);
            textSizeProperty.addListener(listener);
            strokeWidthProperty.addListener(listener);
            strokeColorProperty.addListener(listener);
            cornerRadiusProperty.addListener(listener);
            fillColorProperty.addListener(listener);
            textColorPressedProperty.addListener(listener);
            textSizePressedProperty.addListener(listener);
            strokeWidthPressedProperty.addListener(listener);
            strokeColorPressedProperty.addListener(listener);
            cornerRadiusPressedProperty.addListener(listener);
            fillColorPressedProperty.addListener(listener);
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
        public ButtonStyle clone() {
            ButtonStyle style = new ButtonStyle();
            style.setInterval(getInterval());
            style.setTextColor(getTextColor());
            style.setTextSize(getTextSize());
            style.setStrokeColor(getStrokeColor());
            style.setStrokeWidth(getStrokeWidth());
            style.setCornerRadius(getCornerRadius());
            style.setFillColor(getFillColor());
            style.setTextColorPressed(getTextColorPressed());
            style.setTextSizePressed(getTextSizePressed());
            style.setStrokeColorPressed(getStrokeColorPressed());
            style.setStrokeWidthPressed(getStrokeWidthPressed());
            style.setCornerRadiusPressed(getCornerRadiusPressed());
            style.setFillColorPressed(getFillColorPressed());
            return style;
        }

        public static class Serializer implements JsonSerializer<ButtonStyle>, JsonDeserializer<ButtonStyle> {
            @Override
            public JsonElement serialize(ButtonStyle src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) return JsonNull.INSTANCE;
                JsonObject obj = new JsonObject();

                obj.addProperty("interval", src.getInterval());
                obj.addProperty("textColor", src.getTextColor());
                obj.addProperty("textSize", src.getTextSize());
                obj.addProperty("strokeColor", src.getStrokeColor());
                obj.addProperty("strokeWidth", src.getStrokeWidth());
                obj.addProperty("cornerRadius", src.getCornerRadius());
                obj.addProperty("fillColor", src.getFillColor());
                obj.addProperty("textColorPressed", src.getTextColorPressed());
                obj.addProperty("textSizePressed", src.getTextSizePressed());
                obj.addProperty("strokeColorPressed", src.getStrokeColorPressed());
                obj.addProperty("strokeWidthPressed", src.getStrokeWidthPressed());
                obj.addProperty("cornerRadiusPressed", src.getCornerRadiusPressed());
                obj.addProperty("fillColorPressed", src.getFillColorPressed());

                return obj;
            }

            @Override
            public ButtonStyle deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                    return null;
                JsonObject obj = (JsonObject) json;

                ButtonStyle style = new ButtonStyle();

                style.setInterval(Optional.ofNullable(obj.get("interval")).map(JsonElement::getAsInt).orElse(50));
                style.setTextColor(Optional.ofNullable(obj.get("textColor")).map(JsonElement::getAsInt).orElse(Color.WHITE));
                style.setTextSize(Optional.ofNullable(obj.get("textSize")).map(JsonElement::getAsInt).orElse(12));
                style.setStrokeColor(Optional.ofNullable(obj.get("strokeColor")).map(JsonElement::getAsInt).orElse(Color.DKGRAY));
                style.setStrokeWidth(Optional.ofNullable(obj.get("strokeWidth")).map(JsonElement::getAsInt).orElse(10));
                style.setCornerRadius(Optional.ofNullable(obj.get("cornerRadius")).map(JsonElement::getAsInt).orElse(100));
                style.setFillColor(Optional.ofNullable(obj.get("fillColor")).map(JsonElement::getAsInt).orElse(Color.TRANSPARENT));
                style.setTextColorPressed(Optional.ofNullable(obj.get("textColorPressed")).map(JsonElement::getAsInt).orElse(Color.WHITE));
                style.setTextSizePressed(Optional.ofNullable(obj.get("textSizePressed")).map(JsonElement::getAsInt).orElse(12));
                style.setStrokeColorPressed(Optional.ofNullable(obj.get("strokeColorPressed")).map(JsonElement::getAsInt).orElse(Color.DKGRAY));
                style.setStrokeWidthPressed(Optional.ofNullable(obj.get("strokeWidthPressed")).map(JsonElement::getAsInt).orElse(10));
                style.setCornerRadiusPressed(Optional.ofNullable(obj.get("cornerRadiusPressed")).map(JsonElement::getAsInt).orElse(100));
                style.setFillColorPressed(Optional.ofNullable(obj.get("fillColorPressed")).map(JsonElement::getAsInt).orElse(Color.LTGRAY));

                return style;
            }
        }

    }

    @JsonAdapter(RockerStyle.Serializer.class)
    public static class RockerStyle implements Cloneable, Observable {

        /**
         * Percentage rocker size, max is 90%, min is 10%
         * 10 times the actual value (100 - 900)
         */
        private final IntegerProperty rockerSizeProperty = new SimpleIntegerProperty(this, "rockerSize", 400);
        /**
         * Percentage rocker background corner radius, max is 50%, min is 0%
         * 10 times the actual value (0 - 500)
         */
        private final IntegerProperty bgCornerRadiusProperty = new SimpleIntegerProperty(this, "bgCornerRadius", 500);
        /**
         * Rocker background stroke color
         */
        private final IntegerProperty bgStrokeColorProperty = new SimpleIntegerProperty(this, "bgStrokeColor", Color.DKGRAY);
        /**
         * Rocker background stroke width
         * 10 times the actual value
         */
        private final IntegerProperty bgStrokeWidthProperty = new SimpleIntegerProperty(this, "bgStrokeWidth", 20);
        /**
         * Rocker background fill color
         */
        private final IntegerProperty bgFillColorProperty = new SimpleIntegerProperty(this, "bgFillColor", Color.TRANSPARENT);
        /**
         * Percentage rocker corner radius, max is 50%, min is 0%
         * 10 times the actual value (0 - 500)
         */
        private final IntegerProperty rockerCornerRadiusProperty = new SimpleIntegerProperty(this, "rockerCornerRadius", 500);
        /**
         * Rocker stroke color
         */
        private final IntegerProperty rockerStrokeColorProperty = new SimpleIntegerProperty(this, "rockerStrokeColor", Color.DKGRAY);
        /**
         * Rocker stroke width
         * 10 times the actual value
         */
        private final IntegerProperty rockerStrokeWidthProperty = new SimpleIntegerProperty(this, "rockerStrokeWidth", 10);
        /**
         * Rocker fill color
         */
        private final IntegerProperty rockerFillColorProperty = new SimpleIntegerProperty(this, "rockerFillColor", Color.GRAY);
        private ObservableHelper observableHelper = new ObservableHelper(this);

        public RockerStyle() {
            addPropertyChangedListener(onInvalidating(this::invalidate));
        }

        public IntegerProperty rockerSizeProperty() {
            return rockerSizeProperty;
        }

        public int getRockerSize() {
            return rockerSizeProperty.get();
        }

        public void setRockerSize(int rockerSize) {
            rockerSizeProperty.set(rockerSize);
        }

        public IntegerProperty bgCornerRadiusProperty() {
            return bgCornerRadiusProperty;
        }

        public int getBgCornerRadius() {
            return bgCornerRadiusProperty.get();
        }

        public void setBgCornerRadius(int bgCornerRadius) {
            bgCornerRadiusProperty.set(bgCornerRadius);
        }

        public IntegerProperty bgStrokeColorProperty() {
            return bgStrokeColorProperty;
        }

        public int getBgStrokeColor() {
            return bgStrokeColorProperty.get();
        }

        public void setBgStrokeColor(int bgStrokeColor) {
            bgStrokeColorProperty.set(bgStrokeColor);
        }

        public IntegerProperty bgStrokeWidthProperty() {
            return bgStrokeWidthProperty;
        }

        public int getBgStrokeWidth() {
            return bgStrokeWidthProperty.get();
        }

        public void setBgStrokeWidth(int bgStrokeWidth) {
            bgStrokeWidthProperty.set(bgStrokeWidth);
        }

        public IntegerProperty bgFillColorProperty() {
            return bgFillColorProperty;
        }

        public int getBgFillColor() {
            return bgFillColorProperty.get();
        }

        public void setBgFillColor(int bgFillColor) {
            bgFillColorProperty.set(bgFillColor);
        }

        public IntegerProperty rockerCornerRadiusProperty() {
            return rockerCornerRadiusProperty;
        }

        public int getRockerCornerRadius() {
            return rockerCornerRadiusProperty.get();
        }

        public void setRockerCornerRadius(int rockerCornerRadius) {
            rockerCornerRadiusProperty.set(rockerCornerRadius);
        }

        public IntegerProperty rockerStrokeColorProperty() {
            return rockerStrokeColorProperty;
        }

        public int getRockerStrokeColor() {
            return rockerStrokeColorProperty.get();
        }

        public void setRockerStrokeColor(int rockerStrokeColor) {
            rockerStrokeColorProperty.set(rockerStrokeColor);
        }

        public IntegerProperty rockerStrokeWidthProperty() {
            return rockerStrokeWidthProperty;
        }

        public int getRockerStrokeWidth() {
            return rockerStrokeWidthProperty.get();
        }

        public void setRockerStrokeWidth(int rockerStrokeWidth) {
            rockerStrokeWidthProperty.set(rockerStrokeWidth);
        }

        public IntegerProperty rockerFillColorProperty() {
            return rockerFillColorProperty;
        }

        public int getRockerFillColor() {
            return rockerFillColorProperty.get();
        }

        public void setRockerFillColor(int rockerFillColor) {
            rockerFillColorProperty.set(rockerFillColor);
        }

        public void addPropertyChangedListener(InvalidationListener listener) {
            rockerSizeProperty.addListener(listener);
            bgCornerRadiusProperty.addListener(listener);
            bgStrokeWidthProperty.addListener(listener);
            bgStrokeColorProperty.addListener(listener);
            bgFillColorProperty.addListener(listener);
            rockerCornerRadiusProperty.addListener(listener);
            rockerStrokeWidthProperty.addListener(listener);
            rockerStrokeColorProperty.addListener(listener);
            rockerFillColorProperty.addListener(listener);
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
        public RockerStyle clone() {
            RockerStyle style = new RockerStyle();
            style.setRockerSize(getRockerSize());
            style.setBgCornerRadius(getBgCornerRadius());
            style.setBgStrokeWidth(getBgStrokeWidth());
            style.setBgStrokeColor(getBgStrokeColor());
            style.setBgFillColor(getBgFillColor());
            style.setRockerCornerRadius(getRockerCornerRadius());
            style.setRockerStrokeWidth(getRockerStrokeWidth());
            style.setRockerStrokeColor(getRockerStrokeColor());
            style.setRockerFillColor(getRockerFillColor());
            return style;
        }

        public static class Serializer implements JsonSerializer<RockerStyle>, JsonDeserializer<RockerStyle> {
            @Override
            public JsonElement serialize(RockerStyle src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) return JsonNull.INSTANCE;
                JsonObject obj = new JsonObject();

                obj.addProperty("rockerSize", src.getRockerSize());
                obj.addProperty("bgCornerRadius", src.getBgCornerRadius());
                obj.addProperty("bgStrokeWidth", src.getBgStrokeWidth());
                obj.addProperty("bgStrokeColor", src.getBgStrokeColor());
                obj.addProperty("bgFillColor", src.getBgFillColor());
                obj.addProperty("rockerCornerRadius", src.getRockerCornerRadius());
                obj.addProperty("rockerStrokeWidth", src.getRockerStrokeWidth());
                obj.addProperty("rockerStrokeColor", src.getRockerStrokeColor());
                obj.addProperty("rockerFillColor", src.getRockerFillColor());

                return obj;
            }

            @Override
            public RockerStyle deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if (json == JsonNull.INSTANCE || !(json instanceof JsonObject))
                    return null;
                JsonObject obj = (JsonObject) json;

                RockerStyle style = new RockerStyle();

                style.setRockerSize(Optional.ofNullable(obj.get("rockerSize")).map(JsonElement::getAsInt).orElse(400));
                style.setBgCornerRadius(Optional.ofNullable(obj.get("bgCornerRadius")).map(JsonElement::getAsInt).orElse(500));
                style.setBgStrokeWidth(Optional.ofNullable(obj.get("bgStrokeWidth")).map(JsonElement::getAsInt).orElse(20));
                style.setBgStrokeColor(Optional.ofNullable(obj.get("bgStrokeColor")).map(JsonElement::getAsInt).orElse(Color.DKGRAY));
                style.setBgFillColor(Optional.ofNullable(obj.get("bgFillColor")).map(JsonElement::getAsInt).orElse(Color.TRANSPARENT));
                style.setRockerCornerRadius(Optional.ofNullable(obj.get("rockerCornerRadius")).map(JsonElement::getAsInt).orElse(500));
                style.setRockerStrokeWidth(Optional.ofNullable(obj.get("rockerStrokeWidth")).map(JsonElement::getAsInt).orElse(10));
                style.setRockerStrokeColor(Optional.ofNullable(obj.get("rockerStrokeColor")).map(JsonElement::getAsInt).orElse(Color.DKGRAY));
                style.setRockerFillColor(Optional.ofNullable(obj.get("rockerFillColor")).map(JsonElement::getAsInt).orElse(Color.GRAY));

                return style;
            }
        }

    }

}
