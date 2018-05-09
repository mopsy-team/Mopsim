MOPSIM
=======
MOPSim - Agent-based network traffic simulator, focused on rest areas (MOP) usage analysis.
## Usage
To install maven:
```sudo apt-get install default-jdk```
```sudo apt install maven```

To compile:
```mvn compile```

To increase heapsize (necessary for large simulations):
```export MAVEN_OPTS="-Xmx6g -XX:MaxPermSize=512m"```
(Xmx can be set accordingly to hardware memory size)

To run:
```mvn exec:java```

To configure:
Edit ```src/main/CONF/config.xml``` file.

Expect output data in src/main/SIMULATIONS directory.
