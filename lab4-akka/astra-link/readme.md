### Replicated zookeeper:
    docker-compose -f distributed-zookeeper.yml up

### Client:
    docker run -it --net z-watcher_default --rm --link homework_zoo1_1:zookeeper zookeeper zkCli.sh -server zookeeper

### Watcher
    sbt run 
