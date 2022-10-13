#!/bin/bash
# 不要删除行尾的 "\", 否则你懂的
# 如果参数带特殊字符，需转义。
# pageSize 最大为 100
# cookieOld -> https://bugly.qq.com/v4/api/old/app-list
# cookieNew -> https://bugly.qq.com/v2/search
# token -> X-token
java -jar Bugly-Helper.jar \
-appId b0b1bd57b7 \
-token 1635092223 \
-cookieOld bugly_session=s%3Ad6IijAW67BZv9IJGZGFPbYg6abULEmUY.M%2BGl%2BLLM7FQtFOuOuUn7oLiGmQcBLBPb9qkJpKWTyBw \
-cookieNew bugly_session=eyJpdiI6Imd6WFUrbnRZdjdONEJzTmo1N3l4Rnc9PSIsInZhbHVlIjoiaEJhc256TjlGRjF0N1lJZUxVZFZIQVE1RW54OWwzaE5yVFVaNHBkQU1KWU01dno1M1VWM2ZKdjFuQUFJdjZsamtCdUFoNVR2TCtIUXRkWU9CUU9hSEE9PSIsIm1hYyI6IjJjYWNhYWU2NjM2OGQyN2QxM2Q5ZGE1NzU3NzM3OTk2OTY0MzM1ZWU5ZjA5YTcwYzcwZTYzNmZkZDI0ZjZjOTIifQ%3D%3D \
-appVer "2.0.0" \
-startDateStr 2022-10-10 \
-endDateStr 2022-10-13 \
-pageIndex 0 \
-pageSize 100 \
-debug false \
-abis "{\"arm64-v8a\":{\"unity\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so\", \"il2cpp\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so\"}, \"armeabi-v7a\":{\"unity\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so\", \"il2cpp\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so\"}}" \
-out "E:\Workspace\Demo\Bugly-Helper\release\build" \
-cmd "D:\Dev\NDK\ndk-r19c\android-ndk-r19c\toolchains\aarch64-linux-android-4.9\prebuilt\windows-x86_64\bin\aarch64-linux-android-addr2line.exe -C -f -e"