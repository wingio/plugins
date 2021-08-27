cd ../buildtool
buildtool -p %1
cd ../builds
adb push %1.zip /storage/emulated/0/Aliucord/plugins
adb shell am force-stop com.aliucord
adb shell monkey -p com.aliucord -c android.intent.category.LAUNCHER 1
cd ../src