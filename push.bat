echo Building %1
echo Pushing to device
adb push ./%1/build/%1.zip /storage/emulated/0/Aliucord/plugins
echo Restarting Aliucord
adb shell am force-stop com.aliucord
adb shell monkey -p com.aliucord -c android.intent.category.LAUNCHER 1