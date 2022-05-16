#!/bin/bash
# 不要删除行尾的 "\", 否则你懂的
# :: 如果参数带特殊字符，需转义。如 % 转义为 %%
# :: pageSize 最大为 100
# :: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
# :: cookieNew -> https://bugly.qq.com/v2/search
# :: token -> X-token
java -jar Bugly-Helper.jar \
-decode /Users/Master/Documents/Self/Bugly-Helper/release/CallStack.txt \
-unity /Users/Master/Documents/Self/Bugly-Helper/release/libs/arm64-v8a/libunity.sym.so \
-il2cpp /Users/Master/Downloads/libil2cpp4.so \
-cmd "/Users/Master/DevelopTool/Java/NDK/android-ndk-r20/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64/bin/aarch64-linux-android-addr2line -C -f -e"