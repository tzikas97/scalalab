java -server  -d64  -XX:+UseNUMA -XX:+UseParallelGC -XX:+DoEscapeAnalysis    -XX:+UseCompressedOops -XX:+AggressiveOpts -Djava.library.path=./lib;. -Xss5m -Xms3000m -Xmx23000m -jar ScalaLab.jar
