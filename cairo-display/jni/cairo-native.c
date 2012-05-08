/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <time.h>
#include <android/log.h>
#include <android/bitmap.h>

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#include <cairo.h>
#include <cairo-script-interpreter.h>

#define  LOG_TAG    "cairo-display"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

typedef struct csi_context_list_t
{
  struct csi_context_list_t *next;
  cairo_surface_t *surface;
  cairo_t *context;
} csi_context_list_t;

typedef struct
{
  cairo_script_interpreter_t *csi;
  csi_context_list_t *ctx_list;
  int ctx_count;
} csi_data_t;

static cairo_surface_t *_surface_create(
  void *closure, cairo_content_t content, double width, double height, long uid)
{
  LOGI("_surface_create()");
  //csi_data_t *data = (csi_data_t*)closure;
  //return cairo_image_surface_create(CAIRO_FORMAT_ARGB32, width, height);
  cairo_rectangle_t extents = { 0., 0., width, height };
  return cairo_recording_surface_create(content, &extents);
}

static cairo_t *_context_create(
  void *closure, cairo_surface_t *surface)
{
  LOGI("_context_create()");
  csi_data_t *data = (csi_data_t*)closure;
  cairo_t *context = cairo_create(surface);
  csi_context_list_t *list = malloc(sizeof(*list));

  list->next = data->ctx_list;
  list->surface = cairo_surface_reference(surface);
  list->context = context;
  data->ctx_list = list;
  ++data->ctx_count;
  return context;
}

static void _context_destroy(
  void *closure, void *context)
{
  LOGI("_context_destroy()");
  csi_data_t *data = (csi_data_t*)closure;
  for (csi_context_list_t *list, **plist = &data->ctx_list; ((list = *plist) != NULL); plist = &list->next)
  {
    if (list->context != context)
      continue;
    list->context = NULL;
    if (CAIRO_STATUS_SUCCESS != cairo_surface_status(list->surface))
    {
      LOGE("_context_destroy(): surface has a wrong status");
      cairo_surface_destroy(list->surface);
      *plist = list->next;
      free(list);
      --data->ctx_count;
    }
    break;
	}
}

static jfieldID pointerId;

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_Pointer_initClass(
  JNIEnv *env, jclass clazz)
{
  LOGI("Pointer.initClass()");
  pointerId = (*env)->GetFieldID(env, clazz, "pointer", "J");
}

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
  };

  data->csi = cairo_script_interpreter_create();
  data->ctx_list = NULL;
  data->ctx_count = 0;

  cairo_script_interpreter_install_hooks(data->csi, &hooks);
  //(*env)->SetLongField(env, obj, pointerId, (jlong)data); // will be initialized in the constructor
  return (jlong)data;
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_free(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.free()");
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  cairo_script_interpreter_destroy(data->csi); // TODO: handle errors
  free(data);
}

JNIEXPORT jint JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_getLineNumber(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.getLineNumber()");
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  return (jint)cairo_script_interpreter_get_line_number(data->csi);
}

JNIEXPORT jint JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_feed___3B(
  JNIEnv *env, jobject obj, jbyteArray array)
{
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  jsize length = (*env)->GetArrayLength(env, array);
  jbyte *bytes = (*env)->GetByteArrayElements(env, array, NULL); // TODO: handle errors
  LOGI("CairoScriptInterpreter.feed(%.*s)", length, bytes);
  cairo_script_interpreter_feed_string(data->csi, bytes, length); // TODO: handle errors
  (*env)->ReleaseByteArrayElements(env, array, bytes, JNI_ABORT);
}

JNIEXPORT jint JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_getSurfaceCount(
  JNIEnv *env, jobject obj)
{
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  LOGI("CairoScriptInterpreter.getSurfaceCount()=%d", data->ctx_count);
  return data->ctx_count;
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_clearSurfaces(
  JNIEnv *env, jobject obj)
{
  LOGI("CairoScriptInterpreter.clearSurfaces()");
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  while (data->ctx_list != NULL)
  {
    csi_context_list_t *list = data->ctx_list;
    data->ctx_list = list->next;
    cairo_surface_destroy(list->surface);
    free(list);
  }
  data->ctx_count = 0;
}

static void _get_pixels(cairo_surface_t *source, const AndroidBitmapInfo *info, void *pixels)
{
  cairo_format_t format;
  cairo_surface_t *dest;
  cairo_t *ctx;
  cairo_rectangle_t extents;

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

  cairo_recording_surface_get_extents(source, &extents); // TODO: handle errors
  if (info->width != (int)extents.width || info->height != (int)extents.height)
    cairo_scale(ctx, info->width / extents.width, info->height / extents.height);
  cairo_set_source_surface(ctx, source, extents.x, extents.y);
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

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_getSurfaceSize(
  JNIEnv *env, jobject obj, jint index, jdoubleArray array)
{
  LOGI("CairoScriptInterpreter.getSurfaceSize()");
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  for (csi_context_list_t *list = data->ctx_list; list != NULL; list = list->next)
    if (!index--)
    {
      cairo_rectangle_t extents;
      cairo_recording_surface_get_extents(list->surface, &extents); // TODO: handle errors
      jsize length = (*env)->GetArrayLength(env, array);
      jdouble *size = (*env)->GetDoubleArrayElements(env, array, NULL); // TODO: handle errors
      LOGI("CairoScriptInterpreter.getSurfaceSize()=[%f,%f]", extents.width, extents.height);
      if (length >= 2)
      {
        size[0] = extents.width;
        size[1] = extents.height;
      }
      (*env)->ReleaseDoubleArrayElements(env, array, size, 0);
      break;
    }
}

JNIEXPORT void JNICALL Java_net_kiborgov_argo_android_display_cairo_jni_CairoScriptInterpreter_getSurface(
  JNIEnv *env, jobject obj, jint index, jobject bitmap)
{
  LOGI("CairoScriptInterpreter.getSurface()");
  csi_data_t *data = (csi_data_t*)(*env)->GetLongField(env, obj, pointerId);
  for (csi_context_list_t *list = data->ctx_list; list != NULL; list = list->next)
    if (!index--)
    {
      _get_surface(env, list->surface, bitmap);
      break;
    }
}

