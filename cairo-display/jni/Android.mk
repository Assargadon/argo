LOCAL_PATH := $(call my-dir)

include pixman.mk
include cairo.mk
include cairo-script.mk
include $(CLEAR_VARS)

LOCAL_MODULE    := cairo-native
LOCAL_CFLAGS    := -O2 --std=c99 -I. -Ipixman/pixman -Icairo/src -Icairo-extra -Icairo/util/cairo-script -Ipixman-extra -Wno-missing-field-initializers
LOCAL_LDLIBS    := -lm -llog -ljnigraphics -lz
LOCAL_SRC_FILES := cairo-native.c
LOCAL_STATIC_LIBRARIES := libcairo-script libcairo libpixman

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/cpufeatures)

