#run app
`java -Dninja.port=9000 -jar /Users/ssa/devel/mesosconhackaton/fibonacci.web/target/fibonacci.web-1.0-SNAPSHOT.jar`
http://52.19.198.178:9000/calculateFibonacci?number=10

#HAProxy
https://serversforhackers.com/load-balancing-with-haproxy
http://blogs.splunk.com/2015/06/04/configuring-haproxy-splunk-with-rest-api-sdk-compatability/
https://github.com/magneticio/vamp-router

#Docker
https://docs.docker.com/installation/ubuntulinux/
https://github.com/magneticio/vamp-router

#vamp-router
`sudo docker run --net=host magneticio/vamp-router:latest`
sudo docker run -d --net=host magneticio/vamp-router:latest
http://52.19.198.178:10001/v1/rotes

docker ps
sudo docker exec -ti fe8e404ec29d bash

#install java
```
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java8-installer
```

#get ip
`curl http://169.254.169.254/latest/meta-data/public-ipv4`

scp /Users/ssa/devel/mesosconhackaton/fibonacci.web/target/fibonacci.web-1.0-SNAPSHOT.jar haproxy:/home/ubuntu

java -Dninja.port=9000 -jar /home/ubuntu/fibonacci.web-1.0-SNAPSHOT.jar

http://52.19.198.178:8001/calculateFibonacci