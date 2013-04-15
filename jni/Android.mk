LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := KHash
LOCAL_SRC_FILES := KHash.c genKat.c KeccakDuplex.c KeccakF-1600-reference.c KeccakNISTInterface.c KeccakSponge.c displayIntermediateValues.c

include $(BUILD_SHARED_LIBRARY)