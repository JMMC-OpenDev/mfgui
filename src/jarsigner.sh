DESTDIR=/home/mella/jmmc/ysDev/html/
DESTDIR=~/ysDev/html/
jarsigner ../lib/mfgui.jar mykey && cp -f ../lib/mfgui.jar $DESTDIR/jar/ && echo ok
cp -f fr/jmmc/mf/gui/Releases.html $DESTDIR && echo release copied into $DESTDIR
