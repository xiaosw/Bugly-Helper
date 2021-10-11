:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 7b322036ce ^
-token 1670831229 ^
-cookieOld bugly-session=s%%3ANjRD9UhjjIS8ODPERwxiH0-odl9Ztfa2.TD0lQnDkeehSwLgVoqjrCLg%%2FwJ5C3aOl48fRG7VCp88 ^
-cookieNew bugly_session=eyJpdiI6ImlUWU15Zm0zbThoRXBIOGM2YlNVamc9PSIsInZhbHVlIjoiSWtxOHRjVGo5MEpxUk9Ia1QzXC96czRQdWNZWEtzVkw3Q3huUHlVdEpPaG9VZFpFNjliZVE2VGVjbDVLNnBWMnppR0tDajFyc2t3R3ZDbnUyU1JWZktRPT0iLCJtYWMiOiIyNTlhYzQwYTMxZjliOGE1MjA3ZGYwNjQ0NDFkN2U1OTE0MzAxOTRmMjNmNzM4ZTYxMDViMjc0NDhmY2I4NmU1In0%%3D ^
-appVer 1.2.9^&1.3.1 ^
-startDateStr 2021-10-06 ^
-endDateStr 2021-10-11 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so ^
-il2cpp E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-cmd "D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c\toolchains\aarch64-linux-android-4.9\prebuilt\windows-x86_64\bin\aarch64-linux-android-addr2line.exe -C -f -e"