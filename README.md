# WSO2 Choreo Connect Dynamic Endpoints with Custom Filter

# Building the Custom Filter

Maven and JDK11 is required to build the project. 
```
mvn clean install
```

If you are using the docker-compose setup, copy the jar file to the `docker-compose/resources/enforcer/dropins` directory,
inside the distribution. If you are using kubernetes, make sure that this jar is mounted to the `/home/wso2/lib/dropins`
directory of the enforcer container.

Update the config.toml.

```toml
[[enforcer.filters]]
    # ClassName of the filter
    className = "CustomFilter"
    # Position of the filter within final filter-chain
    position = 2
    # Custom Configurations
    [enforcer.filters.configProperties]
        CustomProperty = "foo"
```

Deploy the choreo-connect distribution and the filter would be engaged in Runtime. (Execute `docker-compose up -d` from
either `docker-compose/choreo-connect` directory or `docker-compose/choreo-connect-with-apim` directory)

Now let's start 2 test echo HTTP services using the following commands.

`docker run -p 8000:8000 -e PORT=8000 --rm -t solsson/http-echo`

`docker run -p 8001:8000 -e PORT=8000 --rm -t solsson/http-echo`

Use the `apictl` command line tool and follow the following steps to deploy the sample API.

1. Initialize a `apictl` project.

`apictl init dynamicEndpoint --oas dynamic-ep-openAPI.yaml`

2. Add environment to apictl

`apictl mg add env dev --adapter https://localhost:9843`

3. Log in to the Choreo Connect cluster

`apictl mg login dev -u admin -p admin -k`

4. Deploy the API

`apictl mg deploy api -f dynamicEndpoint -e dev -k`

5. Generate a sample token

`TOKEN=$(curl -X POST "https://localhost:9095/testkey" -d "scope=read:pets" -H "Authorization: Basic YWRtaW46YWRtaW4=" -k -v)`

6. Invoke the endpoints by passing `x-dynamic-endpoint` header with the related dynamic endpoint name.

For `myDynamicEndpoint` use
`curl -X GET "https://localhost:9095/dynamic-endpoint/echo/1" -H "accept: application/json" -H "Authorization:Bearer $TOKEN" -H "x-dynamic-endpoint: myDynamicEndpoint" -k`

For `myDynamicEndpoint2` use
`curl -X GET "https://localhost:9095/dynamic-endpoint/echo/1" -H "accept: application/json" -H "Authorization:Bearer $TOKEN" -H "x-dynamic-endpoint: myDynamicEndpoint2" -k`
