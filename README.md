# workshop-elasticsearch
Workshop med Elasticsearch avholdt 26.11.15

# Conduct Elesticsearch demo

# Build the code
## maven
```
$ git checkout https://[username]@repo-dev.smartly.no/scm/san/conduct.git
$ mvn clean install
```

## docker
(http://www.infoq.com/articles/docker-executable-images)
```
$ docker login
$ docker pull conductdocker/emdemo
$ docker run -p 0.0.0.0:8080:8080 -v $HOME/.m2:/root/.m2 conductdocker/eldemo
```

# Run the Demo
## maven
```
$ mvn clean install -P demo
```
#### maven with external properties-file
```
$ mvn clean install -P demo -Dapp.properties=/tmp/app_DEMO.properties
```

## docker
```
$ docker login
$ docker pull conductdocker/eldemo
$ docker run -p 0.0.0.0:8080:8080 -v $HOME/.m2:/root/.m2 conductdocker/eldemo demo
```

# Run the Test (with external Elasticsearch)
*start Elasticsearch - se under*
## maven
```
$ mvn clean install -P test
```
#### maven with external properties-file
```
$ mvn clean install -P demo -Dapp.properties=/tmp/app_DEMO.properties
```

## docker
```
$ docker login
$ docker run --link elastic1 -p 0.0.0.0:8080:8080 \
 -v $HOME/.m2:/root/.m2 \
 -v /usr/share/conduct/logs:/usr/share/conduct/logs \
 -v /usr/share/conduct/db:/user/share/conduct/db \
 conductdocker/lyse-conduct test
```
#### docker with external properties-file
```
$ docker run --link elastic1 -p 0.0.0.0:8080:8080 \
 -v $HOME/.m2:/root/.m2 \
 -v /usr/share/conduct/logs:/usr/share/conduct/logs \
 -v /usr/share/conduct/config:/usr/share/conduct/config \
 -v /usr/share/conduct/db:/user/share/conduct/db \
 conductdocker/lyse-conduct test -Dsmartly.conduct.system.properties=/usr/share/conduct/config/conduct_TEST.properties
```

# Rest api
Example commands. See the source code `no.conduct.elasticsearch.main.Camel`
* demo deviceId = 11221212

### Send SimpleAlarmEvent with HTTP-PUT
Address syntax is _/event/handle_ with a SimpleAlarmEvent json payload

#### Send panic button event
`$ curl --header "X-Username: test" -X PUT -d '{ "origin_id" : 11221212, "origin_type" : "mod_alarm", "event_type" : "alarm", "event_sub_type" : "situation", "event_value" : "panic" }' http://localhost:9080/event/handle`

#### Send shut the door event
`$ curl --header "X-Username: test" -X PUT -d '{ "origin_id" : 11221212, "origin_type" : "bin_open", "event_type" : "binary", "event_sub_type" : "open", "event_value" : false }' http://localhost:9080/event/handle`

#### Send open the door event
`$ curl --header "X-Username: test" -X PUT -d '{ "origin_id" : 11221212, "origin_type" : "bin_open", "event_type" : "binary", "event_sub_type" : "open", "event_value" : true }' http://localhost:9080/event/handle`

#### Get ConductEvent by event id
`$ curl -X GET http://localhost:9080/event/1`

#### Get ConductEvent by device serialnumber
`$ curl -X GET http://localhost:8080/event/device/11221212`

#### Get All ConductEvents
`$ curl -X GET http://localhost:8080/event/all`

#### Get Active ConductEvents
`$ curl -X GET http://localhost:8080/event/active`

#### device commands
`$ curl -X GET http://localhost:8080/device/11221212`
`$ curl -X PUT http://localhost:8080/device/11221212/account/61050854`

#### User commands
`$ curl -X GET http://localhost:8080/user/61050854`

## Rest api for Json Schema
Json Schema for all the Classes who is involved (All is HTTP GET), and the rest address is /schema/${class_name}
#### Events
* ConductEvent is the standard event for the Conduct domain
* SimpleAlarmEvent is a striped down version av ConductEvent
```
/schema/ConductEvent
/schema/SimpleAlarmEvent
```

# Start Elasticsearch
(test with external elasticsearch)
```
$ docker run --name elastic1 -p 9200:9200 -p 9300:9300 -d \
 -v /usr/share/elasticsearch/config:/usr/share/elasticsearch/config \
 -v /usr/share/elasticsearch/data:/usr/share/elasticsearch/data \
 -v /usr/share/elasticsearch/logs:/usr/share/elasticsearch/logs \
 elasticsearch
```
#### test elasticsearch
`$ curl http://127.0.0.1:9200/_nodes/process?pretty`


# Docker and firewalld
```
firewall-cmd --permanent --zone=trusted --add-interface=docker0
firewall-cmd --permanent --zone=trusted --add-port=4243/tcp
firewall-cmd --reload
```
