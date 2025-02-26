//
// Created by cainiaohh on 2022/10/11.
//

#define EGLAPIENTRY
typedef void *EGLNativeDisplayType;
typedef struct ANativeWindow *EGLNativeWindowType;

#define EGL_SUCCESS 0x3000
#define EGL_NOT_INITIALIZED 0x3001
#define EGL_BAD_ACCESS 0x3002
#define EGL_BAD_ALLOC 0x3003
#define EGL_BAD_ATTRIBUTE 0x3004
#define EGL_BAD_CONFIG 0x3005
#define EGL_BAD_CONTEXT 0x3006
#define EGL_BAD_CURRENT_SURFACE 0x3007
#define EGL_BAD_DISPLAY 0x3008
#define EGL_BAD_MATCH 0x3009
#define EGL_BAD_NATIVE_PIXMAP 0x300a
#define EGL_BAD_NATIVE_WINDOW 0x300b
#define EGL_BAD_PARAMETER 0x300c
#define EGL_BAD_SURFACE 0x300d
#define EGL_CONTEXT_LOST 0x300e
#define EGL_COLOR_BUFFER_TYPE 0x303f
#define EGL_RGB_BUFFER 0x308e
#define EGL_SURFACE_TYPE 0x3033
#define EGL_WINDOW_BIT 0x0004
#define EGL_RENDERABLE_TYPE 0x3040
#define EGL_OPENGL_ES_BIT 0x0001
#define EGL_OPENGL_ES2_BIT 0x0004
#define EGL_OPENGL_BIT 0x0008
#define EGL_ALPHA_SIZE 0x3021
#define EGL_BLUE_SIZE 0x3022
#define EGL_GREEN_SIZE 0x3023
#define EGL_RED_SIZE 0x3024
#define EGL_DEPTH_SIZE 0x3025
#define EGL_STENCIL_SIZE 0x3026
#define EGL_SAMPLES 0x3031
#define EGL_OPENGL_ES_API 0x30a0
#define EGL_OPENGL_API 0x30a2
#define EGL_NONE 0x3038
#define EGL_RENDER_BUFFER 0x3086
#define EGL_SINGLE_BUFFER 0x3085
#define EGL_EXTENSIONS 0x3055
#define EGL_CONTEXT_CLIENT_VERSION 0x3098
#define EGL_NATIVE_VISUAL_ID 0x302e
#define EGL_NO_SURFACE ((EGLSurface) 0)
#define EGL_NO_DISPLAY ((EGLDisplay) 0)
#define EGL_NO_CONTEXT ((EGLContext) 0)
#define EGL_DEFAULT_DISPLAY ((EGLNativeDisplayType) 0)

#define EGL_CONTEXT_OPENGL_FORWARD_COMPATIBLE_BIT_KHR 0x00000002
#define EGL_CONTEXT_OPENGL_CORE_PROFILE_BIT_KHR 0x00000001
#define EGL_CONTEXT_OPENGL_COMPATIBILITY_PROFILE_BIT_KHR 0x00000002
#define EGL_CONTEXT_OPENGL_DEBUG_BIT_KHR 0x00000001
#define EGL_CONTEXT_OPENGL_RESET_NOTIFICATION_STRATEGY_KHR 0x31bd
#define EGL_NO_RESET_NOTIFICATION_KHR 0x31be
#define EGL_LOSE_CONTEXT_ON_RESET_KHR 0x31bf
#define EGL_CONTEXT_OPENGL_ROBUST_ACCESS_BIT_KHR 0x00000004
#define EGL_CONTEXT_MAJOR_VERSION_KHR 0x3098
#define EGL_CONTEXT_MINOR_VERSION_KHR 0x30fb
#define EGL_CONTEXT_OPENGL_PROFILE_MASK_KHR 0x30fd
#define EGL_CONTEXT_FLAGS_KHR 0x30fc
#define EGL_CONTEXT_OPENGL_NO_ERROR_KHR 0x31b3
#define EGL_GL_COLORSPACE_KHR 0x309d
#define EGL_GL_COLORSPACE_SRGB_KHR 0x3089
#define EGL_CONTEXT_RELEASE_BEHAVIOR_KHR 0x2097
#define EGL_CONTEXT_RELEASE_BEHAVIOR_NONE_KHR 0
#define EGL_CONTEXT_RELEASE_BEHAVIOR_FLUSH_KHR 0x2098

typedef int EGLint;
typedef unsigned int EGLBoolean;
typedef unsigned int EGLenum;
typedef void *EGLConfig;
typedef void *EGLContext;
typedef void *EGLDisplay;
typedef void *EGLSurface;

// EGL function pointer typedefs
typedef EGLBoolean (EGLAPIENTRY *PFN_eglGetConfigAttrib)(EGLDisplay, EGLConfig, EGLint, EGLint *);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglGetConfigs)(EGLDisplay, EGLConfig *, EGLint, EGLint *);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglChooseConfig)(EGLDisplay dpy, const EGLint *attrib_list,
                                                      EGLConfig *configs, EGLint config_size,
                                                      EGLint *num_config);

typedef EGLDisplay (EGLAPIENTRY *PFN_eglGetDisplay)(EGLNativeDisplayType);

typedef EGLint (EGLAPIENTRY *PFN_eglGetError)(void);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglInitialize)(EGLDisplay, EGLint *, EGLint *);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglTerminate)(EGLDisplay);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglBindAPI)(EGLenum);

typedef EGLContext (EGLAPIENTRY *PFN_eglCreateContext)(EGLDisplay, EGLConfig, EGLContext,
                                                       const EGLint *);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglDestroySurface)(EGLDisplay, EGLSurface);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglDestroyContext)(EGLDisplay, EGLContext);

typedef EGLSurface (EGLAPIENTRY *PFN_eglCreateWindowSurface)(EGLDisplay, EGLConfig,
                                                             EGLNativeWindowType, const EGLint *);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglMakeCurrent)(EGLDisplay, EGLSurface, EGLSurface,
                                                     EGLContext);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglSwapBuffers)(EGLDisplay, EGLSurface);

typedef EGLBoolean (EGLAPIENTRY *PFN_eglSwapInterval)(EGLDisplay, EGLint);

typedef const char *(EGLAPIENTRY *PFN_eglQueryString)(EGLDisplay, EGLint);

typedef GLFWglproc (EGLAPIENTRY *PFN_eglGetProcAddress)(const char *);

#define eglGetConfigAttrib _glfw.egl.GetConfigAttrib
#define eglGetConfigs _glfw.egl.GetConfigs
#define eglChooseConfig _glfw.egl.ChooseConfig
#define eglGetDisplay _glfw.egl.GetDisplay
#define eglGetError _glfw.egl.GetError
#define eglInitialize _glfw.egl.Initialize
#define eglTerminate _glfw.egl.Terminate
#define eglBindAPI _glfw.egl.BindAPI
#define eglCreateContext _glfw.egl.CreateContext
#define eglDestroySurface _glfw.egl.DestroySurface
#define eglDestroyContext _glfw.egl.DestroyContext
#define eglCreateWindowSurface _glfw.egl.CreateWindowSurface
#define eglMakeCurrent _glfw.egl.MakeCurrent
#define eglSwapBuffers _glfw.egl.SwapBuffers
#define eglSwapInterval _glfw.egl.SwapInterval
#define eglQueryString _glfw.egl.QueryString
#define eglGetProcAddress _glfw.egl.GetProcAddress

#define _GLFW_EGL_CONTEXT_STATE            _GLFWcontextEGL egl
#define _GLFW_EGL_LIBRARY_CONTEXT_STATE    _GLFWlibraryEGL egl


// EGL-specific per-context data
//
typedef struct _GLFWcontextEGL {
    EGLConfig config;
    EGLContext handle;
    EGLSurface surface;

    void *client;

} _GLFWcontextEGL;

// EGL-specific global data
//
typedef struct _GLFWlibraryEGL {
    EGLDisplay display;
    EGLint major, minor;
    GLFWbool prefix;

    GLFWbool KHR_create_context;
    GLFWbool KHR_create_context_no_error;
    GLFWbool KHR_gl_colorspace;
    GLFWbool KHR_get_all_proc_addresses;
    GLFWbool KHR_context_flush_control;

    void *handle;

    PFN_eglGetConfigAttrib GetConfigAttrib;
    PFN_eglGetConfigs GetConfigs;
    PFN_eglChooseConfig ChooseConfig;
    PFN_eglGetDisplay GetDisplay;
    PFN_eglGetError GetError;
    PFN_eglInitialize Initialize;
    PFN_eglTerminate Terminate;
    PFN_eglBindAPI BindAPI;
    PFN_eglCreateContext CreateContext;
    PFN_eglDestroySurface DestroySurface;
    PFN_eglDestroyContext DestroyContext;
    PFN_eglCreateWindowSurface CreateWindowSurface;
    PFN_eglMakeCurrent MakeCurrent;
    PFN_eglSwapBuffers SwapBuffers;
    PFN_eglSwapInterval SwapInterval;
    PFN_eglQueryString QueryString;
    PFN_eglGetProcAddress GetProcAddress;

} _GLFWlibraryEGL;


GLFWbool _glfwInitEGL(void);

void _glfwTerminateEGL(void);

GLFWbool _glfwCreateContextEGL(_GLFWwindow *window,
                               const _GLFWctxconfig *ctxconfig,
                               const _GLFWfbconfig *fbconfig);

