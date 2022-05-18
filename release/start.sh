#!/bin/bash
# 不要删除行尾的 "\", 否则你懂的
# 如果参数带特殊字符，需转义。
# pageSize 最大为 100
# cookieOld -> https://bugly.qq.com/v4/api/old/app-list
# cookieNew -> https://bugly.qq.com/v2/search
# token -> X-token
java -jar Bugly-Helper.jar \
-appId b0b1bd57b7 \
-token 1514789832 \
-cookieOld bugly-session=s%3A0yzD04jBVgRDVw8h3-VUwFxlZ3Vzj8TD.jCYkSKl95%2BqFxCWpNbBJrGdV873p95svw4idmNzj1ZI \
-cookieNew bugly_session=eyJpdiI6InQyMFpWeUNBMnpWeVBGTGpEaDcwTFE9PSIsInZhbHVlIjoiMEhNZWtGMWpuUHhpM1ExZVFuSkIzOHY2c05FaFdRUldiT2YzS0pNS0JsU3U5XC9YMm9NZ2dmdTJ4bGRnZjVIWUFtUVUrZjlcL2djdDlNdzJ0V0VxQ3ljdz09IiwibWFjIjoiOTJmZmRmNjcyYjc4ZGEwOTBmYjA3YjljYWEzYjAzNmJhMDU2NDMxMWVhZTk4NGYxOWRkOTcyMmJiYzE2MTM0NSJ9 \
-appVer "" \
-startDateStr 2022-05-10 \
-endDateStr 2022-05-19 \
-pageIndex 0 \
-pageSize 100 \
-unity /Users/Master/Documents/Self/Bugly-Helper/release/libs/arm64-v8a/libunity.sym.so \
-il2cpp /Users/Master/Downloads/libil2cpp_105.so \
-out /Users/Master/Documents/Self/Bugly-Helper/release/build \
-cmd "/Users/Master/DevelopTool/Java/NDK/android-ndk-r20/toolchains/aarch64-linux-android-4.9/prebuilt/darwin-x86_64/bin/aarch64-linux-android-addr2line -C -f -e"