
Using the path inference filter
================================

Unfortunately, using the PIF is a bit more tedious than it ought to be for now, as the code assumes a certain layout for the data.
Ph.D. students do not get paid for spending time on software engineering, which is a pity for the end user. Nevertheless, 
the instructions below should allow you to adapt the PIF to your own data without too much frustration.
In order to use the PIF, you need a file containing a description of the road network, and a file containing the GPS data. 
The network description must have a specific filename and be placed in a specific directory.

Network description
--------------------

The network description is encoded using JSON. 
Each line of the file is a valid JSON string that describes one road link of the network. The connections
between the road links are automatically inferred using the link descriptions.
The format accepted is described in this [file](https://github.com/calpath/open-traffic/blob/master/NETCONFIG/src/main/scala/edu/berkeley/path/bots/network/gen/GenericLinkRepresentation.scala)

Here is a valid snippet line:
```json
{"id":{"primary":0,"secondary":1},"length":176,"startNodeID":{"primary":0,"secondary":1},"endNodeID":{"primary":1,"secondary":1},"geom":{"points":[{"lat":6.0618600000000,"lon":49.8306900000000,"srid":4326},{"lat":6.0617100000000,"lon":49.8291200000000,"srid":4326}]},"endFeature":"none","numLanes":1,"speedLimit":2.7500000000000}
```

The network file has to be in a file called *DATA_DIR/network_nidNID_TYPE.json.gz* where:
- DATA_DIR is the location of all the data (this is were the PIF will output the data)
- NID is an integer that identifies the road network
- TYPE is a string that identifies the type of road network being used


Import data description
------------------------

The GPS data must be in a CSV style format. The parser is in this [file](https://github.com/calpath/open-traffic/blob/master/NETCONFIG_IO/src/main/scala/edu/berkeley/path/bots/netconfig/io/json/ImportData.scala)

 Time[YYY-MM-DD HH:MM:SS],id[String],lat[Double],lon[Double],[speed[Float]],[heading[Short]]
 
Here is a valid line:

```csv
2012-04-30 00:00:07,DRIVER1,34.14503,-117.46969,0,0
2012-04-30 00:00:09,DRIVER2,34.14505,-117.46963,0,0
2012-04-30 00:00:11,DRIVER1,34.1457,-117.46961,0,0
```

Using the PIF
--------------

The PIF has to be used in 3 steps:
- importing the GPS data into the proper directory and in the proper format
- running the PIF on the formatted GPS data
- transforming the output of the PIF in a higher-level representation that is more suitable for the end users.

When importing the data, an additional parameter, the *source*, has to be specified. You can use any non-empty string.

Here is the command to import the data:

```bash
MAVEN_OPTS="-Xmx1g -verbose:gc -Dmm.data.dir=DATA_DIR" mvn exec:java -pl NETCONFIG_IO -Dexec.mainClass="netconfig.io.json.ImportProbeData" -Dexec.args="--nid NID --feed SOURCE --file MY_INPUT_FILE.csv.gz --format csv1"
```

This should end with a success message. Running the PIF now should take a little longer:

```bash
MAVEN_OPTS="-Xmx1g -verbose:gc  -Dmm.data.dir=DATA_DIR" mvn exec:java -pl PATH_INFERENCE_IO -Dexec.mainClass="pif.run.RunPif" -Dexec.args="--nid NID --feed SOURCE --net-type TYPE"
```
The PIF main program has a few options to process only a selected range in the data, or to use multple threads an once. Look at the [main file]() for 
more information.

The PIF can be very memory-consumming. If you see that java spends a lot of time in the garbage collector process, increase the amount of memory with the -Xmx parameter.

At this point, you will have transformed the data into a low-level map-matched output. You will probably want a more user-friendly representation, which you can get by running:

```bash
MAVEN_OPTS="-Xmx1g -verbose:gc  -Dmm.data.dir=DATA_DIR" mvn exec:java -pl PATH_INFERENCE_IO -Dexec.mainClass="pif.run.MapDataGeneric" -Dexec.args="--nid NID --feed SOURCE --net-type TYPE --actions traj,tspot,routett --extended-info true"
```

All the output is stored in `DATA_DIR/SOURCE`.

Data output description
------------------------
