#include "com_esur_keccakbenchandroid_FullscreenActivity.h"

#include "KeccakNISTInterface.h"

JNIEXPORT jstring JNICALL Java_com_esur_keccakbenchandroid_FullscreenActivity_nativeSha3(
		JNIEnv *env, jobject obj, jstring input) {

	const char *data = (*env)->GetStringUTFChars(env, input, 0);

	unsigned char result[64];
	unsigned int x;

	memset(result, 0, sizeof result);

	unsigned long len = strlen(data);
	Hash(224, data, len * 8, result);

	unsigned int hash_length = sizeof(result);

	char *ret_buf = malloc(448);

	for (x = 0; x < 28; x++) {
		if (x == 0)
			sprintf(ret_buf, "%02x", result[x]);
		else
			sprintf(ret_buf + strlen(ret_buf), "%02x", result[x]);
	}

	//return [[NSString alloc] initWithCString:ret_buf encoding:NSUTF8StringEncoding];

	return (*env)->NewStringUTF(env, ret_buf);
}
