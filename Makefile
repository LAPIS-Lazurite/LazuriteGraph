CLASSPATH = -classpath ./src
SRCS = $(wildcard ./src/com/lapis_semi/lazurite/LazuriteGraph/*.java)
LIBPATH =  /usr/lib/jvm/jdk-8-oracle-arm-vfp-hflt/jre/lib/ext
JAVAC = /usr/bin/javac
JAVA = /usr/bin/java

all:
	$(JAVAC) $(CLASSPATH) -g -Xlint $(SRCS)

.PHONY: clean
clean:
	rm -f $(SRC)*.class

lib:
	sudo cp lib/RXTXcomm.jar lib/jcommon-1.0.23.jar lib/jfreechart-1.0.19.jar $(LIBPATH)
