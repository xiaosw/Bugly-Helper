:: 如果不加 -ndk 参数，需要配置NDK环境变量 NDK_HOME 或 NDK_ANDROID
:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 1056935136 ^
-cookieOld bugly-session=s%%3A9rqhBQRF3jn5nuo2cmvgelowmf4k1HU4.j62OQv3AYC7GCdlZBaDpMyIwuhxgFNVvibkc9XqVmgo ^
-cookieNew bugly_session=eyJpdiI6Imt6VGJUSWFkbkJVYXpqMmlNTnpya0E9PSIsInZhbHVlIjoibmhpSW5ubjByM1FmWDZSdVRFZ2I3ZXA0Mk9pVkd4R2lmUmhRcUJSeDlGRkc1bEhhdUpGQlBhd0NMaTBZK2JwSSt5OEh2VjB0MGllRHk2NGFnXC9IY0hnPT0iLCJtYWMiOiI3ZDUzNzk3MzMwNDk3M2IyNWMxMjM4MmRjMWUxZjc3MmVhZWMxYzdjNTY1OWIyN2RhMmQyODRmYzI3ZjA0YWQ1In0%%3D ^
-appVer 1.2.5^&1.2.6^&1.2.7_ProductA64_release^&1.2.8_ProductA64_release ^
-startDateStr 2021-09-09 ^
-endDateStr 2021-09-09 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity C:\\Users\\admin\\Downloads\\libunity.sym.so ^
-il2cpp C:\\Users\\admin\\Downloads\\arm64-v8a\\libil2cpp.so ^
-out E:\Workspace\Demo\Bugly-Helper\release\\build ^
-ndk D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c