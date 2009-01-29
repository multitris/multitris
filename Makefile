all: xjavaclient xjavagui xsnakeserver xmultitrisserver ruby-web ruby-lib-doc

xjavagui:
	javac javagui/MainWindow.java

xjavaclient:
	javac javaclient/GUI.java

xsnakeserver:
	javac snakeserver/SnakeServer.java

xmultitrisserver:
	make -C multitrisserver

ruby-web:
	make -C client/ruby-web

ruby-lib-doc:
	rdoc -o ruby-lib/doc -S -N -U -d ruby-lib

clean:
	rm -f javagui/*.class
	rm -f javaclient/*.class
	rm -f common/*.class
	rm -f snakeserver/*.class
	make -C multitrisserver clean
	make -C client/ruby-web clean
