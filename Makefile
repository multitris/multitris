all: gui client server

gui:
	javac javagui/MainWindow.java

client:
	javac javaclient/GUI.java

server:
	make -C multitrisserver

ruby-web:
	make -C client/ruby-web

clean:
	rm -f javaclient/*.class
	rm -f common/*.class
	make -C client/ruby-web clean
	rm -f javagui/*.class
	make -C multitrisserver clean
