#!/bin/bash
# 不要删除行尾的 "\", 否则你懂的
# 如果参数带特殊字符，需转义。
# pageSize 最大为 100
# cookieOld -> https://bugly.qq.com/v4/api/old/app-list
# cookieNew -> https://bugly.qq.com/v2/search
# token -> X-token
java -jar Bugly-Helper.jar \
-appId b0b1bd57b7 \
-token 2003155421 \
-cookieOld bugly-session=s%3A3JJY1_1vTyIzPfX8jkC3pQjcGrVHWF2E.8ywJtyxK37DkbxNi2OKAonepirf%2Fj6Ioc24rpsIT%2FlE \
-cookieNew bugly_session=eyJpdiI6IlhBXC9rWDJjOWlWVW1LQ3dmS2UyOVRBPT0iLCJ2YWx1ZSI6IndNVzR2NzhFQ3VzYklsVTkxdDhoZlJCVFFiNGh3OXlaQWNjN3dZOHA0U1JsYUFcL1hZWXBlbkVzUVwvaVNsSVlSVXhudStoYThcL2thalJ3V0pSMklUbGFRPT0iLCJtYWMiOiIyNmY0NDU4ZTVkZGJjOTAxZTA1MzlhOGFhN2QxZjVlYzA3ZmEzMjQ2YWFmMjNlZTY3ZGIyMDgzODg5YTVlN2M2In0%3D \
-appVer "1.0.7" \
-startDateStr 2022-06-16 \
-endDateStr 2022-06-16 \
-pageIndex 0 \
-pageSize 100 \
-debug false \
-abis "{\"arm64-v8a\":{\"unity\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so\", \"il2cpp\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so\"}, \"armeabi-v7a\":{\"unity\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so\", \"il2cpp\":\"E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so\"}}" \
-out "E:\Workspace\Demo\Bugly-Helper\release\build" \
-cmd "D:\Dev\NDK\ndk-r19c\android-ndk-r19c\toolchains\aarch64-linux-android-4.9\prebuilt\windows-x86_64\bin\aarch64-linux-android-addr2line.exe -C -f -e"