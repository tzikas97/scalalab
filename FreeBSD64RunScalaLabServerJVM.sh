java -server -d64   -XX:+UseNUMA -XX:+UseParallelGC -XX:+UseCompressedOops   -XX:MaxInlineSize=500  -XX:+AggressiveOpts -Djava.library.path=.:./lib -Xss5m -Xms4000m -Xmx23500m -jar ScalaLab.jar &
