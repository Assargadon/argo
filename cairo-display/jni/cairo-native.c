/*
 * This file is a part of AR-Go project
 * Copyright (C) 2012 Roman Vorobets
 */

#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include <cairo.h>
#include <cairo-script-interpreter.h>

#define  LOG_TAG    "cairo-display"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct
{
  cairo_script_interpreter_t *csi;
  jobject listener;
  JNIEnv *env; // FIXME
} csi_data_t;

static cairo_surface_t *_surface_create(
  void *closure, cairo_content_t content, double width, double height, long uid)
{
  LOGI("_surface_create()");
  csi_data_t *data = (csi_data_t*)closure;
  return cairo_image_surface_create(CAIRO_FORMAT_ARGB32, width, height);
}

static cairo_t *_context_create(
  void *closure, cairo_surface_t *surface)
{
  LOGI("_context_create()");
  csi_data_t *data = (csi_data_t*)closure;
  cairo_t *context = cairo_create(surface);

  return context;
}

static void _context_destroy(
  void *closure, void *context)
{
  LOGI("_context_destroy()");
  csi_data_t *data = (csi_data_t*)closure;
}

static jfieldID pointerId;

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Pointer_initClass(
  JNIEnv *env, jclass clazz)
{
  LOGI("Pointer.initClass()");
  pointerId = (*env)->GetFieldID(env, clazz, "pointer", "J");
}

static jclass androidBitmap;
static jmethodID androidBitmapCreateBitmap;
static jobject androidBitmapConfig_ARGB_8888;
static jobject androidBitmapConfig_RGB_565;

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Surface_initClass(
  JNIEnv *env, jclass clazz)
{
  LOGI("Surface.initClass()");
  jclass config = (*env)->FindClass(env, "android/graphics/Bitmap$Config");
  jfieldID config_ARGB_8888 = (*env)->GetStaticFieldID(env, config, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
  jfieldID config_RGB_565 = (*env)->GetStaticFieldID(env, config, "RGB_565", "Landroid/graphics/Bitmap$Config;");
  androidBitmapConfig_ARGB_8888 = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, config, config_ARGB_8888));
  androidBitmapConfig_RGB_565 = (*env)->NewGlobalRef(env, (*env)->GetStaticObjectField(env, config, config_RGB_565));
  androidBitmap = (*env)->FindClass(env, "android/graphics/Bitmap");
  androidBitmapCreateBitmap = (*env)->GetStaticMethodID(env, androidBitmap, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
  if (androidBitmap)
    androidBitmap = (*env)->NewGlobalRef(env, androidBitmap);
}

JNIEXPORT jlong JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Surface_init__J(
  JNIEnv *env, jobject obj, jlong pointer)
{
  LOGI("Surface.init(%lld)", pointer);
  cairo_surface_t *surface = (cairo_surface_t*)(intptr_t)pointer;
  surface = cairo_surface_reference(surface);
  return (jlong)(intptr_t)surface;
}

#define surface_data_ptr(env, obj) ((cairo_surface_t*)(intptr_t)(*(env))->GetLongField((env), (obj), pointerId))

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Surface_free(
  JNIEnv *env, jobject obj)
{
  LOGI("Surface.free()");
  cairo_surface_t *surface = surface_data_ptr(env, obj);
  cairo_surface_destroy(surface);
}

static void _get_surface(JNIEnv *env, cairo_surface_t *surface, jobject bitmap);

JNIEXPORT jobject JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Surface_getBitmap(
  JNIEnv *env, jobject obj)
{
  LOGI("Surface.getBitmap()");
  cairo_surface_t *surface = surface_data_ptr(env, obj);
  int width = cairo_image_surface_get_width(surface);
  int height = cairo_image_surface_get_height(surface);
  jobject config = NULL;
  switch (cairo_image_surface_get_format(surface))
  {
  case CAIRO_FORMAT_ARGB32:
    config = androidBitmapConfig_ARGB_8888;
    break;
  case CAIRO_FORMAT_RGB16_565:
    config = androidBitmapConfig_RGB_565;
    break;
  default:
    return NULL;
  }
  jobject bitmap = (*env)->CallStaticObjectMethod(env, androidBitmap, androidBitmapCreateBitmap, width, height, config);
  _get_surface(env, surface, bitmap);
  return bitmap;
}

static jmethodID csiListenerOnCopyPage;
static jmethodID csiListenerOnShowPage;

static void _show_page(
  void *closure, cairo_t *context)
{
  LOGI("_show_page()");
  csi_data_t *data = (csi_data_t*)closure;
  if (NULL != data->listener)
    (*data->env)->CallVoidMethod(data->env, data->listener, csiListenerOnShowPage, (jlong)(intptr_t)cairo_get_target(context));
}

static void _copy_page(
  void *closure, cairo_t *context)
{
  LOGI("_copy_page()");
  csi_data_t *data = (csi_data_t*)closure;
  if (NULL != data->listener)
  {
    (*data->env)->CallVoidMethod(data->env, data->listener, csiListenerOnCopyPage, (jlong)(intptr_t)cairo_get_target(context));
  }
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_initClass(
  JNIEnv *env, jclass clazz)
{
  LOGI("CairoScriptInterpreter.initClass()");
  jclass listener = (*env)->FindClass(env, "net/kiborgov/argo/android/display/cairo/jni/CairoScriptInterpreter$Listener");
  csiListenerOnCopyPage = (*env)->GetMethodID(env, listener, "onCopyPage", "(J)V");
  csiListenerOnShowPage = (*env)->GetMethodID(env, listener, "onShowPage", "(J)V");
}

#define csi_data_ptr(env, obj) ((csi_data_t*)(intptr_t)(*(env))->GetLongField((env), (obj), pointerId))

JNIEXPORT jlong JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_init(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.init()");
  csi_data_t *data = malloc(sizeof(*data));
  const cairo_script_interpreter_hooks_t hooks = {
    .closure = data,
    .surface_create = _surface_create,
    .context_create = _context_create,
    .context_destroy = _context_destroy,
    .show_page = _show_page,
    .copy_page = _copy_page,
  };

  data->csi = cairo_script_interpreter_create();
  data->listener = NULL;

  cairo_script_interpreter_install_hooks(data->csi, &hooks);
  //(*env)->SetLongField(env, obj, pointerId, (jlong)data); // the field will be initialized in the constructor
  return (jlong)(intptr_t)data;
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_free(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.free()");
  csi_data_t *data = csi_data_ptr(env, obj);
  cairo_script_interpreter_destroy(data->csi); // TODO: handle errors

  if (NULL != data->listener)
    (*env)->DeleteGlobalRef(env, data->listener);
  free(data);
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_setup(
  JNIEnv *env, jobject obj, jobject listener)
{
  LOGI("CairoScriptInterpreter.setup()");
  csi_data_t *data = csi_data_ptr(env, obj);

  if (NULL != data->listener) // should not be NULL, but just in case...
    (*env)->DeleteGlobalRef(env, data->listener);
  if (NULL != listener)
    data->listener = (*env)->NewGlobalRef(env, listener); else
    data->listener = NULL;
}

JNIEXPORT jint JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_getLineNumber(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.getLineNumber()");
  csi_data_t *data = csi_data_ptr(env, obj);

  return (jint)cairo_script_interpreter_get_line_number(data->csi);
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_feed___3BII(
  JNIEnv *env, jobject obj, jbyteArray array, jint start, jint length)
{
  csi_data_t *data = csi_data_ptr(env, obj);
  jbyte *bytes = (jbyte*)malloc(length);

  (*env)->GetByteArrayRegion(env, array, start, length, bytes); // TODO: handle errors
  data->env = env; // FIXME: nasty workaround!
  LOGI("CairoScriptInterpreter.feed(%.*s, %d, %d)", length, bytes, start, length);
  cairo_script_interpreter_feed_string(data->csi, bytes, length); // TODO: handle errors
  free(bytes);
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_feed___3B(
  JNIEnv *env, jobject obj, jbyteArray array)
{
  csi_data_t *data = csi_data_ptr(env, obj);
  jsize length = (*env)->GetArrayLength(env, array);
  jbyte *bytes = (*env)->GetByteArrayElements(env, array, NULL); // TODO: handle errors

  data->env = env; // FIXME: nasty workaround!
  LOGI("CairoScriptInterpreter.feed(%.*s)", length, bytes);
  cairo_script_interpreter_feed_string(data->csi, bytes, length); // TODO: handle errors
  (*env)->ReleaseByteArrayElements(env, array, bytes, JNI_ABORT);
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_feed__Ljava_lang_String_2(
  JNIEnv *env, jobject obj, jobject string)
{
  csi_data_t *data = csi_data_ptr(env, obj);
  jsize length = (*env)->GetStringLength(env, string);
  const jchar *chars = (*env)->GetStringChars(env, string, NULL); // TODO: handle errors

  data->env = env; // FIXME: nasty workaround!
  LOGI("CairoScriptInterpreter.feed(%.*s)", length, string);
  cairo_script_interpreter_feed_string(data->csi, (const char*)chars, length); // TODO: handle errors
  (*env)->ReleaseStringChars(env, string, chars);
}

static void _get_pixels(cairo_surface_t *source, const AndroidBitmapInfo *info, void *pixels)
{
  cairo_format_t format;
  cairo_surface_t *dest;
  cairo_t *ctx;
  double x = 0, y = 0;

  switch (info->format)
  {
  case ANDROID_BITMAP_FORMAT_RGB_565:
    format = CAIRO_FORMAT_RGB16_565;
    break;
  case ANDROID_BITMAP_FORMAT_RGBA_8888:
    format = CAIRO_FORMAT_RGB24;
    break;
  default:
    // TODO: handle errors
    LOGE("_get_pixels(): invalid format");
    return;
  }

  dest = cairo_image_surface_create_for_data(pixels, format, info->width, info->height, info->stride);
  ctx = cairo_create(dest);

  switch (cairo_surface_get_type(source))
  {
#if 0
  case CAIRO_SURFACE_TYPE_RECORDING:
    {
      cairo_rectangle_t extents;

      cairo_recording_surface_get_extents(source, &extents); // TODO: handle errors
      if (info->width != (int)extents.width || info->height != (int)extents.height)
        cairo_scale(ctx, info->width / extents.width, info->height / extents.height);
      x = extents.x;
      y = extents.y;
      break;
    }
#endif
  case CAIRO_SURFACE_TYPE_IMAGE:
    {
      int width = cairo_image_surface_get_width(source);
      int height = cairo_image_surface_get_height(source);

      if (info->width != width || info->height != height)
        cairo_scale(ctx, (double)info->width / width, (double)info->height / height);
      break;
    }
  }
  cairo_set_source_surface(ctx, source, x, y);
  cairo_paint(ctx);

  cairo_destroy(ctx);
  cairo_surface_destroy(dest);
}

static void _get_surface(JNIEnv *env, cairo_surface_t *surface, jobject bitmap)
{
  AndroidBitmapInfo info;
  void *pixels;

  LOGI("_get_surface()");
  if (AndroidBitmap_getInfo(env, bitmap, &info) < 0)
  {
    LOGE("_get_surface(): unable to get Android Bitmap info");
    return; // TODO: handle errors
  }

  if (info.format != ANDROID_BITMAP_FORMAT_RGB_565 && info.format != ANDROID_BITMAP_FORMAT_RGBA_8888)
  {
    LOGE("_get_surface(): invalid bitmap format");
    return; // TODO: handle errors
  }

  if (AndroidBitmap_lockPixels(env, bitmap, &pixels) < 0)
  {
    LOGE("_get_surface(): unable to lock pixels");
    return; // TODO: handle errors
  }

  _get_pixels(surface, &info, pixels);

  AndroidBitmap_unlockPixels(env, bitmap);
}

