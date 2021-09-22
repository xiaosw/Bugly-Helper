:: 需要配置NDK环境变量 NDK_HOME
:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list
:: cookieNew -> https://bugly.qq.com/v2/search
:: token -> X-token
java -jar Bugly-Helper.jar ^
-decode E:\Workspace\Demo\Bugly-Helper\release\CallStack.txt ^
-unity E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libunity.sym.so ^
-il2cpp E:\Workspace\Demo\Bugly-Helper\release\libs\arm64-v8a\libil2cpp.so ^
-ndk D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c