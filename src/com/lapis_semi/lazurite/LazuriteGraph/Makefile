CLASSPATH = -classpath ../../../../
SRCS = $(wildcard ./*.java)
JAVAC = /usr/bin/javac
JAVA = /usr/bin/java

all:
	$(JAVAC) $(CLASSPATH) -g -Xlint $(SRCS)

.PHONY: clean
clean:
	rm -f *.class

