:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 805749931 ^
-cookieOld bugly-session=s%%3ArsW5lAd3gKgVD1FwDnhWeo2FvhPh5bDv.XdU%%2BGLfaRH9PalZJ5mdQjDIiceDeqPy07YbCGiDeW04 ^
-cookieNew bugly_session=eyJpdiI6IjExbE1GVTc5b0VHR041U2N1WWZzMVE9PSIsInZhbHVlIjoiYXVzbXlkWmJqY21uMjJoeFVGc3g5S09qeEoxZUU5S3N2SSs1TFRhbktSOXo3RTUyc1ZiVVBQWjRJSEVaOTFQN0l6MW9DUUpma05OS1h2NWhKVFZUaGc9PSIsIm1hYyI6IjY2ZDU2YjBkODJhZTY4ZDUwYWEwZmQ2OGU4Y2QzYWRkMmY2YjZhM2RlN2Y3Nzc5NGUwNDZmZWMwNmUzYjI3NDQifQ%%3D%%3D ^
-appVer 1.4.1 ^
-startDateStr 2021-10-28 ^
-endDateStr 2021-10-28 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so ^
-il2cpp E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-cmd "D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c\toolchains\aarch64-linux-android-4.9\prebuilt\windows-x86_64\bin\aarch64-linux-android-addr2line.exe -C -f -e"