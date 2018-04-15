MOPSIM
=======
MOPSim - Agent-based network traffic simulator, focused on rest areas (MOP) usage analysis.
## Usage
To run:
1. Clone from [repository](https://github.com/mopsy-team/Mopsim.git).
2. Download matsim 0.9.0 core library from [here](https://github.com/matsim-org/matsim/releases/download/matsim-0.9.0/matsim-0.9.0.zip).
3. Extract the library.
4. Download commons-math3-3.6.1-bin.tar.gz library from [here](http://ftp.ps.pl/pub/apache//commons/math/binaries/commons-math3-3.6.1-bin.tar.gz).
5. Extract the library.
6. You can edit MOPSim config group in file ```CONF/config.xml``` to set numbers of cars, trucks and buses used in simulation, as well as change default paths to adequate matrices or mop data files.
6. To compile and run MOPSim, execute run_mopsim.sh script with paths to matsim-0.9.0.jar and commons-math3-3.6.1.jar files as parameters. Example usage:
```
./run_mopsim.sh matsim-0.9.0/matsim-0.9.0.jar math/commons-math3-3.6.1.jar
```
5. After execution, directory ```SIMULATIONS``` will contain simulation data.
