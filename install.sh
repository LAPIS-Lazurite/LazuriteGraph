#! /bin/bash

echo '*********************************************************'
echo '**** Install/Update LazuriteGraph for Raspberry Pi ******'
echo '*********************************************************'
echo ''
echo ''
echo 'STEP1:: get or update LazDriver from github'
cd ~/driver
if [ -e 'LazDriver' ]; then
	echo 'update LazDriver'
	cd LazDriver
	git pull
else
	echo 'downloading LazDriver'
	git clone git://github.com/LAPIS-Lazurite/LazDriver
	cd LazDriver
fi
echo ''
echo ''
echo 'STEP2:: build LazDriver'
make

echo ''
echo ''
echo 'STEP3:: installing rxtx serial library'
sudo apt-get install librxtx-java

echo ''
echo ''
echo 'STEP4:: get or update LazDriver from LazuriteJava'
cd ~
if [ ! -e 'java' ]; then
	mkdir java
fi
cd java
if [ -e 'LazuriteJava' ]; then
	cd LazuriteJava
	git pull
else
	git clone git://github.com/LAPIS-Lazurite/LazuriteJava
	cd LazuriteJava
fi

echo ''
echo ''
echo 'STEP6:: get external libraries'
if [ -e jna-4.2.2.jar ];  then
	echo
else
wget https://maven.java.net/content/repositories/releases/net/java/dev/jna/jna/4.2.2/jna-4.2.2.jar
sudo cp jna-4.2.2.jar /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/ext
fi
if [ -e jnaerator-runtime-0.12.jar ]; then
	echo
else
wget central.maven.org/maven2/com/nativelibs4java/jnaerator-runtime/0.12/jnaerator-runtime-0.12.jar
sudo cp jnaerator-runtime-0.12.jar /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/ext
fi

echo ''
echo ''
echo 'STEP6:: build LazuriteJava'
make
echo ''
echo ''
echo 'STEP7:: Install LazuriteJava'
make install
echo ''
echo ''
echo 'STEP8:: get LazuriteGraph'
cd ~/java
if [ -e 'LazuriteGraph' ]; then
	cd LazuriteGraph
	git pull
else
	git clone git://github.com/LAPIS-Lazurite/LazuriteGraph
	cd LazuriteGraph
fi
echo ''
echo ''
echo 'STEP9:: install external library'
make install
echo ''
echo ''
echo 'STEP10:: build LazuriteGraph'
make
echo ''
echo 'STEP11:: enabling SubGHz tab'
cp lib/linux/graph.pref .
echo ''
echo ''
echo 'Complete !!'
echo ''



