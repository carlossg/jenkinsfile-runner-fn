# Jenkinsfile Runner for Fn

<img src="images/jenkins-fn.png" width="150">

An [Fn Project](http://fnproject.io) function to run Jenkins pipelines. It will process a GitHub webhook, git clone the repository and execute the Jenkinsfile in that git repository.

This function allows `Jenkinsfile` execution without needing a persistent Jenkins master running in the same way as [Jenkins X Serverless](https://medium.com/@jdrawlings/serverless-jenkins-with-jenkins-x-9134cbfe6870), but using the Fn Project platform (and supported providers like [Oracle Functions](https://blogs.oracle.com/cloud-infrastructure/announcing-oracle-functions)) instead of Kubernetes.

# Current Status

Running under fn fails and no logs from `jenkinsfile-runnner` are sent to syslog

```
$ cat src/test/resources/github.json | fn invoke jenkinsfile-runner jenkinsfile-runner
{"message":"error receiving function response"}

Fn: Error calling function: status 502
```

syslog:

```
...
tmp dir: /tmp
App root: /app
Executing bootstrap: warDir: /app/jenkins, pluginsDir: /tmp/plugins, jenkinsfile: /tmp/workspace/Jenkinsfile
Feb 12, 2019 7:44:05 AM org.eclipse.jetty.util.log.Log initialized
INFO: Logging initialized @3615ms to org.eclipse.jetty.util.log.Slf4jLog
Syslog connection closed; fd='17', client='AF_INET(172.17.0.1:41118)', local='AF_INET(0.0.0.0:514)'" PID=1 PROGRAM=syslog-ng SOURCE=s_local
```

However running the docker image succeeds

```
cat src/test/resources/github.json | docker run --rm -i jenkinsfile-runner:0.0.1
```

# Limitations

Current implementation limitations:

* `checkout scm` does not work, change it to `sh 'git clone https://github.com/carlossg/jenkinsfile-runner-fn-example.git'`

# Example

See the [jenkinsfile-runner-fn-example](https://github.com/carlossg/jenkinsfile-runner-fn-example) project for an example that is tested and works.

# Extending

You can add your plugins to `plugins.txt`.
You could also add the Configuration as Code plugin for configuration.

Other tools can be added to the `Dockerfile`.

# Installation

[Install Fn](http://fnproject.io/tutorials/install/)

## Building

Build the function

    mvn clean package

## Publishing

Create and deploy the function locally

    fn create app jenkinsfile-runner
    fn --verbose deploy --app jenkinsfile-runner --local

## Logging

Start a syslog server to see the logs

    docker run -d --rm -it -p 5140:514 --name syslog-ng balabit/syslog-ng:latest
    docker exec -ti syslog-ng tail -f /var/log/messages-kv.log

Update the function to send logs to the syslog server

    fn update app jenkinsfile-runner --syslog-url tcp://<YOUR_IP>:5140

## Testing

Invoke the function

    cat src/test/resources/github.json | fn invoke jenkinsfile-runner jenkinsfile-runner

## GitHub events

Add a GitHub `json` webhook to your git repo pointing to the function url.
