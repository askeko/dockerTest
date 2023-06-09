# Docker session
## Build the application docker container
To start the build process, run the following command from within the "application" folder:

`docker build -t \_docker\_hub\_username\_/\_name\_of\_container\_:\_version\_ .`

This will build the docker container in the current directory and tag it with according to the parameter defined in `-t`. An example of a real world build is: 

`docker build -t rsenetcompany/fklub:1.0.0 .`

This builds a container under the `rsenetcompany` user under the `fklub` repository on docker hub and tagged with version 1.0.0

## Push the docker container
When the container image is built, it needs to be pushed to docker hub. This can be done by running:

`docker push \_docker\_hub\_username\_/\_name\_of\_container\_:\_version\_`

Example: `docker push rsenetcompany/fklub:1.0.0`

This will push the image to docker hub and look like this:

```
The push refers to repository [docker.io/rsenetcompany/fklub]
efeb58a2f8ee: Pushing [=====================>                             ]  8.946MB/20.89MB
dc9fa3d8b576: Layer already exists
27ee19dc88f2: Layer already exists
c8dd97366670: Layer already exists
```

If the system complains about missing login, you need to run:

`docker login`

and enter the username and password given for the user in docker hub and them attempt to push again. 

Ps. if using a private registry, it will be necessary to provide the full url to the `docker login` command.

## Start the docker container locally
The container can be started immediately after the image is built locally so it is not required to be pushed to an external registry. To start the application locally, use the following command:

`docker run -it -p 8080:8080 \_docker\_hub\_username\_/\_name\_of\_container\_:\_version\_`

Example: `docker run -it -p 8080:8080 rsenetcompany/fklub:1.0.0` 

Then the application can be accessed on: `http://app.127.0.0.1.nip.io:8080/`


# Kubernetes session

## Starting local kubernetes cluster
To start a local kubernetes cluster based on the k3d distribution, run the following command in the current directory from powershell

`.\bin\k3d.exe cluster create --config .\config.yml`

When the cluster is started, check that you are able to communicate with the cluster by retrieving a list of all active nodes in the cluster:

`.\bin\kubectl.exe get nodes`

which should yield a result like this:
```
NAME                     STATUS   ROLES                  AGE     VERSION
k3d-mycluster-server-0   Ready    control-plane,master   2m15s   v1.25.6+k3s1
k3d-mycluster-agent-1    Ready    <none>                 2m10s   v1.25.6+k3s1
k3d-mycluster-agent-0    Ready    <none>                 2m10s   v1.25.6+k3s1
```

## Deploy application to kubernetes
Interaction with kubernetes is normally done using the cli tool `kubectl` and the same applies for deployment. Open powershell inside the directory `application/k8s-simple` and run `..\..\bin\kubectl.exe apply -k .`. 

This will read the "kustomize.yaml" file and deploy it to the current cluster. 

Ps. "Kustomize" is just another tool to help us merge multiple kubernetes configuration files and deploy in a single command. The clean kubernetes method is to run `kubectl apply -f deployment.yaml` and then do it for the other yaml files individually. 

The result looks like this:
```
PS C:\...\k8s\application\k8s-simple> ..\..\bin\kubectl.exe apply -k .
service/application-svc created
deployment.apps/application-deployment created
ingress.networking.k8s.io/application-ingress created
```

To check if everything has started, the following commands can be executed.

Get pods:
```
PS C:\...\k8s\application\k8s-simple> ..\..\bin\kubectl.exe get pods
NAME                                     READY   STATUS    RESTARTS   AGE
application-deployment-cd6bb685b-c4pl6   1/1     Running   0          2m7s
```


Get services:
```
PS C:\...\k8s\application\k8s-simple> ..\..\bin\kubectl.exe get svc
NAME              TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)    AGE
application-svc   ClusterIP   10.43.223.85   <none>        8080/TCP   2m17s
```

Get Ingress:
```
PS C:\...\k8s\application\k8s-simple> ..\..\bin\kubectl.exe get ingress
NAME                  CLASS     HOSTS                  ADDRESS                            PORTS   AGE
application-ingress   traefik   app.127.0.0.1.nip.io   172.19.0.3,172.19.0.4,172.19.0.5   80      2m29s
```

In this scenario it will now be possible to open the url `http://app.127.0.0.1.nip.io:8080/` in your browser and see the hostname of the container serving the request. 

Ps. the reason for adding port `:8080` is because we configured the port `8080` to be mapped to port `80` inside the cluster, see `config.yml` so this is just applicable for this local environment.

## Pretty overview of kubernetes (K9S)
To get an easy overview of what is running in your cluster, the tool `k9s` is one of many good tools to interact with the cluster and see what is running. 

An alternative is named `openlens` if text based UI is not to your liking.

# Kubernetes Cleanup
To delete the local cluster again, run the following command:

`.\bin\k3d.exe cluster delete mycluster`