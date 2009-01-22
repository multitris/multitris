all: xjavaclient xjavagui xsnakeserver xmultitrisserver

xjavagui:
	javac javagui/MainWindow.java

xjavaclient:
	javac javaclient/GUI.java

xsnakeserver:
	javac snakeserver/SnakeServer.java

xmultitrisserver:
	make -C multitrisserver

clean:
	rm -f javagui/*.class
	rm -f javaclient/*.class
	rm -f common/*.class
	rm -f snakeserver/*.class
	make -C multitrisserver clean
