LIBCAIROSCRIPT_SRC = \
    cairo/util/cairo-script/cairo-script-file.c \
    cairo/util/cairo-script/cairo-script-hash.c \
    cairo/util/cairo-script/cairo-script-interpreter.c \
    cairo/util/cairo-script/cairo-script-objects.c \
    cairo/util/cairo-script/cairo-script-operators.c \
    cairo/util/cairo-script/cairo-script-scanner.c \
    cairo/util/cairo-script/cairo-script-stack.c \
    $(NULL)


LIBCAIROSCRIPT_CFLAGS:=                                         \
    -DPACKAGE_VERSION="\"argo-cairo-display\""                  \
    -DPACKAGE_BUGREPORT="\"http://github.com/Assargadon/argo\"" \
    -DHAVE_STDINT_H=1


include $(CLEAR_VARS)

LOCAL_MODULE    := libcairo-script
LOCAL_CFLAGS    := -O2 $(LIBCAIROSCRIPT_CFLAGS) -Ipixman/pixman -Icairo/src -Icairo-extra -Icairo/util/cairo-script -Ipixman-extra -Wno-missing-field-initializers
LOCAL_LDFLAGS   := -lz
LOCAL_SRC_FILES := $(LIBCAIROSCRIPT_SRC)
LOCAL_STATIC_LIBRARIES := cairo

include $(BUILD_STATIC_LIBRARY)
