# Smart Car Streaming
This is an application that aims to be a proof of concept for a streaming system, that accepts data from smart cars and calculates different metadata. This is accomplished by a Java application that utilizes Smart Streaming. As a messaging component, kafka and zookeeper is used. 

An example of the function of the application is the location of possible holdups by checking for slow moving vehicles that are clustered closely together. 

As of now, databases are still abstracted away for the sake of simplicity. Of course there could also be additional filtering according to the privacy settings of the user (depending on the cars id), as to where the data is saved, if at all, as described in Assignment 3. 

## Workflow
An exemplary workflow of an item in the system is as follows: 
 * A smartcar sends a post-request to the web-api
 * The web application sends the contents of the request to the kafka topic "carpacket"
 * the spark streaming application consumes the packets of "carpacket"
 * the spark application makes sanity checks, filters them and calculates possible holdup locations
 * holdups are sent to the kafka topic "holdups"
 * the web application is subscribed to the holdups, and whenever a new holdup comes in, it is stored.
 * smart cars and other interested parties query the api for possible holdups (and in future other information)

 ## Data
 The data used for this proof of concept was aquired by the Vehicular mobility trace project in cologne, Germany. It is simulated data based on real data from cologne, to simulate that every car in the city employs sensors. 

 The dataset is available [here](http://kolntrace.project.citi-lab.fr/).

## Components
The project consists of the following components:
### Requester
This is just a basic java-based Requester that is more of a utility tool to test this System. It is just based of pure java without any libraries to make it easily compileable and portable while still being in the java ecosystem. 

It can therefore be built using pure java (javac), but any other requester will suffice, the IP that is to be requested just has to be changed. 

### Webfrontend
The web frontend serves as api endpoint for the cars to post their packets to, and get the information that is calculated by spark. It is also a subscriber and producer for kafka, which is used to communicate with the analysis component. 

The webfrontend can be built by executing 
``` mvn clean package ```
in the \webfrontend folder. Afterwards, this component must be deployed. This is done by uploading the resulting webfrontend.jar to AWS Elastic Beanstalk. For the webfontend, use a Web server environment. As platform, use java, and as application code choose the jar. 
Then you have to add a custom rule to the security group of this instance, to let traffic to the port 8080 pass through. 

### Streaming Component
The streaming component does the actual work of analyzing incoming packages on the kafka topic "carpackets" and providing insights on them. The results are sent back via kafka to the webfrontend component, so that they can be made available via the api. It utilizes stateful spark streaming, therefore checkpoints have to be saved on a hadoop file system. 

The streaming component also is built by 
``` mvn clean package ``` int the \smartcarstreaming folder. Then, you have to deploy it to an aws elasticmapreduce cluster. The problem with this is, that the AWS emr cluster demands at least an m3.large instance for each node, so you will need at least 2 m3.large instances to start the cluster. Create a new spark cluster, upload the smartcarstreaming.war to an S3 bucket, and deploy the war as a spark step. To submit it, you also have to pass the --class parameter to the step, with "SmartCarStreaming" as value, so that spark can find the main class to execute. 

### Kafka + Zookeeper
Kafka instances provisioned by zookeper are used as a basic middleware in this system. Dockerfiles for tose two systems can be found in the kafka-docker subfolder. To deploy them locally, simply invoke docker-compose up, in the directory with the docker-compose.yml. Similarly, to deploy them to AWS, you have to use the ecs-cli command, that is only available in linux as a subpart of the [amazon command line interface](https://aws.amazon.com/de/cli/), and can be found [here](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/ECS_CLI_installation.html). Then, after your aws cli is configured and ready to go, execute the command ```aws ecs-cli compose``` in your local directory, and the two dockers will be started as instances on aws. 

After kafka and zookeeper are running on aws, check their IPs and replace those in the webfrontend SenderConfig file, and in the smartcarstreaming constants file, before packaging and uploading those, so that the right servers are connected. 