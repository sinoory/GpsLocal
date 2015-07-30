

if [ -z "$1"  ] ; then
    devs=""
elif [ "$1" = "e" ] ; then
    devs=" -s emulator-5554 "
else
    devs=" -s 43c720140df62d7 "
fi
echo "$devs"
adb $devs push assets/html/userlist.html /sdcard/sin/gps/html/
adb $devs push assets/html/linelist.html /sdcard/sin/gps/html/
adb $devs push assets/html/serverbuslist.html /sdcard/sin/gps/html/

adb $devs push assets/js/jquery_mini.js /sdcard/sin/gps/js/

