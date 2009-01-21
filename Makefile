all: gui ruby-web server

server:
	make -C multitrisserver
gui:
	make -C javagui

ruby-web:
	make -C client/ruby-web

clean:
	make -C javagui clean
	make -C multitrisserver clean
	make -C client/ruby-web clean
