Requirements:
--------------------
1. Install Java Development Kit (preferably JDK 1.4.2)
2. Install Java Wireless Toolkit (preferably WTK 2.2)

Configurations:
---------------------
1. In config.bat, set JAVA_HOME to JDK path.
2. Set WTK_HOME to WTK path.

Produce Jar package:
-------------------------------
1. Call make_data.bat to process and produce data in _temp folder.
2. Call make_src.bat to compile source code and produce jar package in _release folder
3. Call obfuscate.bat to produce release version.
4. Generate JAD file using any preferred tool.
