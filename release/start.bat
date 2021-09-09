:: 需要配置NDK环境变量 NDK_HOME
:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 13377926 ^
-cookieOld bugly-session=s%%3AdigUdGpj5fY8qQILz_4irS3UdtJKgvul.Mwq75tsN1z6e72fCIN08SYnKANiaZpZq1NsUgAktyyg ^
-cookieNew bugly_session=eyJpdiI6ImZVVHZLOGE3c2tmQXBhYWNpWHVLWFE9PSIsInZhbHVlIjoiVmYwWGIwT3pDRWtzSGlmeVprVTFtRkR4Rk1rNlZ4d2c1WDd0bnRocXp5M0toMkpQenM2cGtHY3p1bWo1OU1Ub3pVeDhWK09QQ1FZc3NaMVpuV0JzV3c9PSIsIm1hYyI6IjMyZjU3ZDlkMTZmNWMwMjhlMzA0MTU1OGVhOThhYmEzMzhmMTEyMDkxM2I3YmUzZGJlZjkzMjdiZTJkZDI2ZWYifQ%%3D%%3D ^
-appVer 1.2.6 ^
-startDateStr 2021-09-08 ^
-endDateStr 2021-09-08 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity C:\\Users\\admin\\Downloads\\libunity.sym.so ^
-il2cpp C:\\Users\\admin\\Downloads\\arm64-v8a\\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-ndk D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c