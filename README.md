# JVM 自动插桩

## 运行方法
```bash
maven package

javac src/main/java/Triangle.java -d .

java 
-javaagent:target/instruments-1.0-SNAPSHOT.jar=Triangle.test \
--add-exports=java.base/sun.nio.ch=ALL-UNNAMED \
--add-opens=java.base/java.lang=ALL-UNNAMED \
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
--add-opens=java.base/java.io=ALL-UNNAMED \
--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED \
Triangle
```
