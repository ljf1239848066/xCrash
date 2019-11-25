// Copyright (c) 2019-present, iQIYI, Inc. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

// Created by caikelun on 2019-03-07.

#ifndef XCC_LOG_H
#define XCC_LOG_H 1

#include <stdarg.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif

#define XCC_LOG_PRIO ANDROID_LOG_VERBOSE

#define XCC_LOG_TAG "xcc_common"

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wgnu-zero-variadic-macro-arguments"
#define XCC_LOG_DEBUG(fmt, ...) do{if(XCC_LOG_PRIO <= ANDROID_LOG_DEBUG) __android_log_print(ANDROID_LOG_DEBUG, XCC_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XCC_LOG_INFO(fmt, ...)  do{if(XCC_LOG_PRIO <= ANDROID_LOG_INFO)  __android_log_print(ANDROID_LOG_INFO,  XCC_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XCC_LOG_WARN(fmt, ...)  do{if(XCC_LOG_PRIO <= ANDROID_LOG_WARN)  __android_log_print(ANDROID_LOG_WARN,  XCC_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#define XCC_LOG_ERROR(fmt, ...) do{if(XCC_LOG_PRIO <= ANDROID_LOG_ERROR) __android_log_print(ANDROID_LOG_ERROR, XCC_LOG_TAG, fmt, ##__VA_ARGS__);}while(0)
#pragma clang diagnostic pop

#ifdef __cplusplus
}
#endif

#endif
