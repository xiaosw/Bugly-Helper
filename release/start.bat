:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 1478090358 ^
-token 834880504 ^
-cookieOld bugly-session=s%%3AbUAkW_ftWP2GQHUJxBnzppCgik-pmD2c.x4Ul5DCZxPCAkUO6vWVUgzSAUnVF33edP%%2FRY716ASgc ^
-cookieNew bugly_session=eyJpdiI6Im1NcndNYTBLTGJPaUhzemQ4MGt6WkE9PSIsInZhbHVlIjoidzFNOGNJeE5rWml1UzBOQ2psYmtEN21iZFYwRTJGOWpFN2NzSmxhbkFaS054SGZJNWxcL3pEOHRrdnVcL0dMTFpUcER3bzg2MDFEMXNyUVRMMk9JV0M1UT09IiwibWFjIjoiMWYwZTllMGE5YTIwNDRmZmY2MDgxNTM3MTljMzkzYTFmMzg3YjBjNzUxOTI0N2MyMjMzZDg5OTFmMGUzN2RhOSJ9 ^
-appVer 1.2.9^&1.2.5 ^
-startDateStr 2021-09-22 ^
-endDateStr 2021-09-23 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so ^
-il2cpp E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-cmd "D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c\toolchains\aarch64-linux-android-4.9\prebuilt\windows-x86_64\bin\aarch64-linux-android-addr2line.exe -C -f -e"