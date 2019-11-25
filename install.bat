mkdir -p .\libxcrash\src\main\jniLibs\armeabi
mkdir -p .\libxcrash\src\main\jniLibs\armeabi-v7a
mkdir -p .\libxcrash\src\main\jniLibs\arm64-v8a
mkdir -p .\libxcrash\src\main\jniLibs\x86
mkdir -p .\libxcrash\src\main\jniLibs\x86_64

copy .\libxcrash_dumper\libs\armeabi\xcrash_dumper     libxcrash\src\main\jniLibs\armeabi\libxcrash_dumper.so /B
copy .\libxcrash_dumper\libs\armeabi-v7a\xcrash_dumper libxcrash\src\main\jniLibs\armeabi-v7a\libxcrash_dumper.so /B
copy .\libxcrash_dumper\libs\arm64-v8a\xcrash_dumper   libxcrash\src\main\jniLibs\arm64-v8a\libxcrash_dumper.so /B
copy .\libxcrash_dumper\libs\x86\xcrash_dumper         libxcrash\src\main\jniLibs\x86\libxcrash_dumper.so /B
copy .\libxcrash_dumper\libs\x86_64\xcrash_dumper      libxcrash\src\main\jniLibs\x86_64\libxcrash_dumper.so /B
