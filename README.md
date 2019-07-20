# Jenclight

Light-weight Jenkins client for Java, based on Jenkins REST API

-----

After googling for a few days I found it hard to get a jenkins rest api client for java without a dozen of dependencies, the offical client com.offbytwo.jenkins caused me lots of classpath conflicts with aws sdk, kafka java sdk etc. Instead of rewriting another aws sdk or kafka sdk which to me is too much to complete, jenkins seems to the easy answer. After just 1 day coding, I managed to create this light weight jenkins rest api client ---jenclight. Currently only support basic http authentication, build job with or without parameters.

