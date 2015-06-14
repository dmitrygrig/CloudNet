# Overview
**CloudNet** is a powerful java framework for cloud computing simulations. It allows for cloud providers to test their infrastructure in repeatable and controllable way, in order to find and avoid performance bottlenecks, evaluate different cloud management scenarios under varying geo-aware, load and pricing conditions. The most important features of **CloudNet** that distinguish it among other similar frameworks are the simulation of distributed DCs, computing costs of cooling, scheduling energy power outages, usage of synthetic and real weather data, and modeling various energy price strategies under several SLA policies. 

Cloudnet was designed and implemented on the basis of loose-coupling paradigms that assumes decoupling of different simulated components from each other and their communication through the Message-oriented middleware (MOM). The dependent components of the system interact with each other through interfaces that allows simple extension of almost each part of the framework and highly configurable possibilities that captures many simulation use cases.

# Getting started
The following listing represents use case of simulation of a IaaS cloud with one single DC and one host inside it:
```java
// Create simulation clock with granularity (simulation step) of 1 sec
SimClock clock = new SimClock(TimeFrame.Sec);

// Create new IaaS cloud with elastic load balancer based on First-Fit algorithm
Cloud cloud = new IaaSCloud(1, clock, 
	new ElasticityManagerFirstFitOptimistic(new AlwaysVmMigrationPolicy()));

// attach new passive monitor with batch csv writer (batch size=1000)
cloud.setMonitor(new PassiveMonitoringSystem(
	new CsvHistoryWriter("resources/cloud.csv", 
						"out/dcs.csv", 
						"out/pms.csv", 
						"out/vms.csv", 1000, false)));

// Create new data center (DC) in Oslo
Datacenter dc = Datacenter.forLocation(1, clock, new Oslo());

// Set model for cooling of the cloud infrastructure that uses cold air 
// for cooling when outside temperature is less than 10 degrees, 
// mechanical cooling infrastructure if temperate is higher than 18 degrees, 
// or mixed cooling of both otherwise.
dc.setCoolingModel(
	new MixedCoolingModel(10.0, 18.0, 
							new AirCoolingModel(), 
							new MechanicalCoolingModel()));

// add one "HP ProLiant DL580 G3" to the DC
Pm pm = new Pm(1, clock, new PmSpecPowerHpProLiantMl110G3PentiumD930());
pm.setMipsProvisioner(new GreedyProvisioner());
pm.setRamProvisioner(new GreedyProvisioner());
pm.setSizeProvisioner(new GreedyProvisioner());
pm.setBwProvisioner(new GreedyProvisioner());
dc.addPm(pm);

// Create new IaaS cloud scheduler that schedules once 2 VMs 
// with characteristics of VM A1 from Microsoft Azure.
Scheduler scheduler = new IaaSScheduler(new VmGeneratorOnce(new VmSpecAzureA1(), 2));

// Create new simulation engine that will execute 3600 simulation steps
SimEngine engine = new SimEngineSimple(clock, scheduler, cloud, 3600);

// Perform simulations
engine.start();
engine.stop();

// print results
LOGGER.info("Total Costs: %.2f", cloud.getCosts());
``` 

# Architecture
The framework consists of the following parts: 
* **Simulation core**: different implementations of simulation engines as well as simulation clock that shows the actual simulation time. The actual simulation time can be polled by any object but can only be set by the simulation engine responsible for this.
* **Cloud domain**: all main cloud entities, models and interfaces.
* **Physical models package**: implementation of various physical models (resource utilization, cooling, weather, power outages, energy prices, etc.) that are utilized during runtime of cloud infrastructure.
* **MOM**: communication between different loose-coupled components of a simulated cloud.
* **Monitoring infrastructure**: attachable observers that monitor ans make snaphots of cloud entity states and log them into different output formats for further analysis.
* **Data-collecting utilities**: a set of utilities that are responsible for downloading and transformation of various weather data from different Web services.
* **Locations**: a set of locations with pre-configured physical models based on real statistics.
* **SLAs**: a set of cloud service Service level agreements (SLAs)
* **Data prediction package**:  a package for resource utilization time series prediction 
