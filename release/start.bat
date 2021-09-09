:: 需要配置NDK环境变量 NDK_HOME
:: 不要删除行尾的 "^", 否则你懂的
:: 如果参数带特殊字符，需转义。如 % 转义为 %%
:: pageSize 最大为 100
:: cookieOld -> https://bugly.qq.com/v4/api/old/app-list?userId=5B35F9F18DEDEBEAA5BD9E6C63B7E94C&fsn=1acf31af-ee10-4b4d-92c1-0077cfaeecdc
:: cookieNew -> https://bugly.qq.com/v2/search?start=0&userSearchPage=%2Fv2%2Fcrash-reporting%2Fdashboard%2F2527295ba1&pid=1&platformId=1&date=last_1_hour&sortOrder=desc&useSearchTimes=4&rows=100&sortField=matchCount&appId=2527295ba1&fsn=c55e1474-c385-4d88-9481-887da5cd9b2b
:: token -> X-token
java -jar Bugly-Helper.jar ^
-appId 2527295ba1 ^
-token 1661914764 ^
-cookieOld bugly-session=s%%3AdigUdGpj5fY8qQILz_4irS3UdtJKgvul.Mwq75tsN1z6e72fCIN08SYnKANiaZpZq1NsUgAktyyg ^
-cookieNew bugly_session=eyJpdiI6ImozRTVhSEd2c3RRa0JoU0NFUmNtaEE9PSIsInZhbHVlIjoiUVFweXFkMkF5OEpockxqOTZxdXVzZUVcL2pibDE5SkFJTDJveHM1TlpPc0ExVWZGRkJ6SnFMdHVcL3ZQdHAxOUlVWEkwa0xDanNkN3hsbjZlSTBBQXd0dz09IiwibWFjIjoiMGZkYmU4YzUyNjA2ZDI4MGJmZDkyMWVmZjFmMmJkZmNlMDBhNTNhZmFjMTQ4Zjc1YTFhNDEwYTgzMDkwMWQwNiJ9 ^
-appVer 1.2.6 ^
-startDateStr 2021-09-08 ^
-endDateStr 2021-09-08 ^
-pageIndex 0 ^
-pageSize 100 ^
-unity C:\\Users\\admin\\Downloads\\libunity.sym.so ^
-il2cpp C:\\Users\\admin\\Downloads\\arm64-v8a\\libil2cpp.so ^
-out  C:\\Users\\admin\\Downloads\\Bugly-Helper ^
-ndk D:\Dev\NDK\android-ndk-r19c-windows-x86_64\android-ndk-r19c