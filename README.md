# Jenkinsfile Runner for Project Fn

<img src="images/jenkins-fn.png" width="150">

An [Fn Project](http://fnproject.io) function to run Jenkins pipelines. It will process a GitHub webhook, git clone the repository and execute the Jenkinsfile in that git repository.

This function allows `Jenkinsfile` execution without needing a persistent Jenkins master running in the same way as [Jenkins X Serverless](https://medium.com/@jdrawlings/serverless-jenkins-with-jenkins-x-9134cbfe6870), but using the Fn Project platform (and supported providers like [Oracle Functions](https://blogs.oracle.com/cloud-infrastructure/announcing-oracle-functions)) instead of Kubernetes.

# Project Fn vs AWS Lambda

The function is very similar to the one in [jenkinsfile-runner-lambda](https://github.com/carlossg/jenkinsfile-runner-lambda) with just a small change in the signature.
The main difference between Lambda and Fn is in the packaging, as Lambda layers are limited in size and are expanded in `/opt` while Fn allows a custom `Dockerfile` where you can install whatever you want in a much easier way, just need to include the function code and entrypoint from `fnproject/fn-java-fdk`.

# Oracle Functions

[Oracle Functions](https://blogs.oracle.com/cloud-infrastructure/announcing-oracle-functions) is a cloud service providing Project Fn function execution (currently in limited availability).
`jenkinsfile-runner-fn` function runs in Oracle Functions, with the caveat that it needs a syslog server running somewhere to get the logs (see below).

# Limitations

Current implementation limitations:

* `checkout scm` does not work, change it to `sh 'git clone https://github.com/carlossg/jenkinsfile-runner-fn-example.git'`
* Jenkinsfile must use `/tmp` for any tool that needs writing files, see the [example](https://github.com/carlossg/jenkinsfile-runner-fn-example)

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

## Execution

Invoke the function

    cat src/test/resources/github.json | fn invoke jenkinsfile-runner jenkinsfile-runner

## Logging

Get the logs for the last execution

    fn get logs jenkinsfile-runner jenkinsfile-runner $(fn ls calls jenkinsfile-runner jenkinsfile-runner | grep 'ID:' | head -n 1 | sed -e 's/ID: //')

### Syslog

Alternatively, start a syslog server to see the logs

    docker run -d --rm -it -p 5140:514 --name syslog-ng balabit/syslog-ng:latest
    docker exec -ti syslog-ng tail -f /var/log/messages-kv.log

Update the function to send logs to the syslog server

    fn update app jenkinsfile-runner --syslog-url tcp://logs-01.loggly.com:514

## GitHub events

Add a GitHub `json` webhook to your git repo pointing to the function url.
