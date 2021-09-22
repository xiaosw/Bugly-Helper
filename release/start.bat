:: 如果不加 -ndk 参数，需要配置NDK环境变量 NDK_HOME 或 NDK_ANDROID
:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 1449968486 ^
-cookieOld bugly-session=s%%3AcXeZaxMaOgFEbJK0Iw5rFTvoLApAK0od.jkEfCwp9FqBeQLwW9Erjfja44xVXkV40T31YggEBbgo ^
-cookieNew bugly_session=eyJpdiI6ImdsNU9iUHY4cm9zZ3RaS2xBZWxBcHc9PSIsInZhbHVlIjoidlA5aVJCd3lKNmZyUUhMTkNtT2pGaDRjQkJBZkpxT3grN0FkeCtLUDN2NWs2ZjZxdExiREo0V1BpZWt0Z29PWWp3WEFrNVJ2K3JpSjRyQXhBdlwvNkFBPT0iLCJtYWMiOiI2YTJjNDk1ZWU0NDMxZGM2OTY5N2RmNmZhMjZkYTk3YzNiNmY3ZDVkZjhiNzJhY2Y2NTgyYjk5ZGI2YTRiZTNiIn0%%3D ^
-appVer 1.2.9 ^
-startDateStr 2021-09-18 ^
-endDateStr 2021-09-22 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so ^
-il2cpp E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-ndk D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c