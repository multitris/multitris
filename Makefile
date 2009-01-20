all: gui ruby-web

gui:
	make -C javagui

ruby-web:
	make -C client/ruby-web

clean:
	make -C client/ruby-web clean
	make -C javagui clean
