# Jenkins-REST

Light-weight Jenkins REST API Client for Java


**no more classpath conflicts !!**

-----

After googling for a few days I found it hard to get a jenkins rest api client for java without a dozen of dependencies, the offical client com.offbytwo.jenkins caused me lots of classpath conflicts with aws sdk, kafka java sdk etc. Most of them are around common utility libs like jackson. To fix these classpath conflicts, Instead of rewriting another aws sdk or kafka sdk which is obviously too much to complete, jenkins seems to be the easy answer.  Here comes this light weight jenkins rest api client,  currently it supports: 

* basic http authentication with account username, password/api-token
* build job with or without parameters
* customizable jenkins log handler ( register callback method which will trigger when log entry is retrieved )
* customizable jenkins job progress handler ( register callback method which will trigger when certain job lifecycle event is reached )
* job timeout config (throw exception after timeout)
* synchronized await until job complete

## Quick Start

update your build.gradle, add the following dependency:
```gradle

compile group: 'io.github.kev1nst', name: 'jenkins-rest', version: '1.0.1'

```

Then in your Java code, simplely add the following:

```java

JobResult result=Jenkins.connect(JENKINS_URL, ACCOUNT, CREDENTIAL).build("folder1/job1").await();
// then start to write whatever code you want to execute after the job is completed

```


the [Demo](https://github.com/kev1nst/jenkins-client/blob/master/src/test/java/io/github/kev1nst/jenkins/Demo.java) class in the source code provides most of the common use cases of how-to


