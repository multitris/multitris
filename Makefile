all: gui server ruby-web

gui:
	make -C javagui

server:
	make -C multitrisserver

ruby-web:
	make -C client/ruby-web

clean:
	make -C client/ruby-web clean
	make -C javagui clean
	make -C multitrisserver clean
