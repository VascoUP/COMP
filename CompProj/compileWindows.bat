cd src
call jjtree regex2auto.jjt
call javacc regex2auto.jj
call dir /s /B *.java > ../sources.txt
cd ..
call mkdir bin
cd bin
call mkdir programs
cd ..
call javac -d ./bin/ -classpath ".;./lib/*" @sources.txt
call del sources.txt