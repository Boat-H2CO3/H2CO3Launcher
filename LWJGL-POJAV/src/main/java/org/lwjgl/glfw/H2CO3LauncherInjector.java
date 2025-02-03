package org.lwjgl.glfw;

import static org.lwjgl.system.APIUtil.apiGetFunctionAddress;
import static org.lwjgl.system.APIUtil.apiLog;
import static org.lwjgl.system.JNI.invokePP;
import static org.lwjgl.system.MemoryUtil.memAddressSafe;

import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

/**
 * By Tungsten
 * This class is for H2CO3Launcher.
 */
public class H2CO3LauncherInjector {

    public static final long SetInjectorCallBack = apiGetFunctionAddress(GLFW.GLFW, "pojavSetInjectorCallback");
    public static final long SetHitResultType = apiGetFunctionAddress(GLFW.GLFW, "pojavSetHitResultType");
    private static final String HIT_RESULT_TYPE_UNKNOWN      = "UNKNOWN";
    private static final String HIT_RESULT_TYPE_MISS         = "MISS";
    private static final String HIT_RESULT_TYPE_BLOCK        = "BLOCK";
    private static final String HIT_RESULT_TYPE_BLOCK_OLD    = "TILE";
    private static final String HIT_RESULT_TYPE_ENTITY       = "ENTITY";
    private static final int INJECTOR_LEVEL_0                = 0; // unknown
    private static final int INJECTOR_LEVEL_1                = 1; // 1.0 - 1.2
    private static final int INJECTOR_LEVEL_2                = 2; // 1.3 - 1.6
    private static final int INJECTOR_LEVEL_3                = 3; // 1.7 - 1.13
    private static final int INJECTOR_LEVEL_4                = 4; // 1.13+
    private static final int INJECTOR_MODE_ENABLE            = 1;
    private static final int INJECTOR_MODE_DISABLE           = 0;
    private static boolean get = false;
    private static ClassLoader classLoader;
    private static int level = 0;
    @Nullable
    private static String param0 = null;
    @Nullable
    private static String param1 = null;
    @Nullable
    private static String param2 = null;
    @Nullable
    private static String param3 = null;

    public static void setClassLoader(ClassLoader classLoader) {
        String prop = System.getProperty("h2co3Launcher.injector");
        if (!get && prop != null && !prop.isEmpty()) {
            H2CO3LauncherInjector.classLoader = classLoader;
            String[] props = prop.split(":");
            if (props.length == 5 && (props[0].equals("0") || props[0].equals("1") || props[0].equals("2") || props[0].equals("3") || props[0].equals("4"))) {
                int level = Integer.parseInt(props[0]);
                String param0 = props[1];
                String param1 = props[2];
                String param2 = props[3];
                String param3 = props[4];
                setup(level, param0, param1, param2, param3);
            }
        }
    }

    public static void setup(int level, String param0, String param1, String param2, String param3) {
        H2CO3LauncherInjector.level = level;
        H2CO3LauncherInjector.param0 = param0;
        H2CO3LauncherInjector.param1 = param1;
        H2CO3LauncherInjector.param2 = param2;
        H2CO3LauncherInjector.param3 = param3;
        get = true;
        H2CO3LauncherInjectorCallback callback = new H2CO3LauncherInjectorCallback() {
            @Override
            public void invoke() {
                getHitResultType();
            }
        };
        glfwSetH2CO3LauncherInjectorCallback(callback);
    }

    @Nullable
    @NativeType("H2CO3LauncherInjectorfun")
    public static H2CO3LauncherInjectorCallback glfwSetH2CO3LauncherInjectorCallback(@Nullable @NativeType("H2CO3LauncherInjectorfun") H2CO3LauncherInjectorCallbackI cbfun) {
//        boolean a =true;
//        if (a) {
//            throw new IllegalArgumentException("glfwSetH2CO3LauncherInjectorCallback");
//        }
        return H2CO3LauncherInjectorCallback.createSafe(nglfwSetH2CO3LauncherInjectorCallback(memAddressSafe(cbfun)));
    }

    public static long nglfwSetH2CO3LauncherInjectorCallback(long cbfun) {
        return invokePP(cbfun, SetInjectorCallBack);
    }

    public static void nglfwSetHitResultType(String type) {
        int typeInt;
        switch (type) {
            case HIT_RESULT_TYPE_MISS:
                typeInt = 1;
                break;
            case HIT_RESULT_TYPE_BLOCK:
            case HIT_RESULT_TYPE_BLOCK_OLD:
                typeInt = 2;
                break;
            case HIT_RESULT_TYPE_ENTITY:
                typeInt = 3;
                break;
            default:
                typeInt = 0;
                break;
        }
        JNI.invokeV(typeInt, H2CO3LauncherInjector.SetHitResultType);
    }

    public static void getHitResultType() {
        if (!get) {
            nglfwSetHitResultType(HIT_RESULT_TYPE_UNKNOWN);
            apiLog("H2CO3Launcher Injector not initialized!\n");
            return;
        }
        if (param0 != null && param1 != null && param2 != null && param3 != null) {
            Object type = null;
            boolean success = false;
            try {
                Class<?> minecraftClass = Class.forName(param0, true, classLoader);
                Method method = minecraftClass.getDeclaredMethod(param1);
                method.setAccessible(true);
                Object minecraft = method.invoke(null);
                Field targetField = minecraftClass.getDeclaredField(param2);
                targetField.setAccessible(true);
                Object target = targetField.get(minecraft);
                if (target != null) {
                    switch (level) {
                        case INJECTOR_LEVEL_2:
                        case INJECTOR_LEVEL_3:
                            Field typeField = target.getClass().getDeclaredField(param3);
                            typeField.setAccessible(true);
                            type = typeField.get(target);
                            break;
                        case INJECTOR_LEVEL_4:
                            Method typeMethod = target.getClass().getDeclaredMethod(param3);
                            typeMethod.setAccessible(true);
                            type = typeMethod.invoke(target);
                            break;
                        default:
                            break;
                    }
                }
                success = true;
            } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                apiLog(e.getMessage());
            }
            if (level == INJECTOR_LEVEL_2) {
                if (success && type == null) {
                    nglfwSetHitResultType(HIT_RESULT_TYPE_MISS);
                } else if (success && (type.toString().equals(HIT_RESULT_TYPE_BLOCK_OLD) || type.toString().equals(HIT_RESULT_TYPE_ENTITY))) {
                    nglfwSetHitResultType(type.toString());
                } else {
                    nglfwSetHitResultType(HIT_RESULT_TYPE_UNKNOWN);
                }
            } else {
                if (type != null && (type.toString().equals(HIT_RESULT_TYPE_MISS) || type.toString().equals(HIT_RESULT_TYPE_BLOCK) || type.toString().equals(HIT_RESULT_TYPE_ENTITY))) {
                    nglfwSetHitResultType(type.toString());
                } else {
                    nglfwSetHitResultType(HIT_RESULT_TYPE_UNKNOWN);
                }
            }
        }
    }

}
