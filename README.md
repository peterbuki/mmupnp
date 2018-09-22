# mmupnp
[![license](https://img.shields.io/github/license/ohmae/mmupnp.svg)](./LICENSE)
[![GitHub release](https://img.shields.io/github/release/ohmae/mmupnp.svg)](https://github.com/ohmae/mmupnp/releases)
[![GitHub issues](https://img.shields.io/github/issues/ohmae/mmupnp.svg)](https://github.com/ohmae/mmupnp/issues)
[![GitHub closed issues](https://img.shields.io/github/issues-closed/ohmae/mmupnp.svg)](https://github.com/ohmae/mmupnp/issues)
[![Build Status](https://travis-ci.org/ohmae/mmupnp.svg?branch=develop)](https://travis-ci.org/ohmae/mmupnp)
[![codecov](https://codecov.io/gh/ohmae/mmupnp/branch/develop/graph/badge.svg)](https://codecov.io/gh/ohmae/mmupnp)
[![Maven Repository](https://img.shields.io/badge/maven-jcenter-brightgreen.svg)](https://bintray.com/ohmae/maven/net.mm2d.mmupnp)
[![Maven metadata URI](https://img.shields.io/maven-metadata/v/https/jcenter.bintray.com/net/mm2d/mmupnp/maven-metadata.xml.svg)](https://bintray.com/ohmae/maven/net.mm2d.mmupnp)

Universal Plug and Play (UPnP) ControlPoint library for Java.

## Feature
- Pure Java implementation.
- Available in both Java application and Android apps.
- Easy to use
- High response

Of course, this also can be used from kotlin.

## Requirements
- Java 8 or later (or Java 6 or later with retrolambda)
- Android Gradle Plugin 3.0.0 or later

## Restrictions
- This library support only ControlPoint functions.
There is no way to make Device. If you need it, please select another library.
- Some functions that are not widely used are not implemented.
  - Multicast eventing

## Example of use
Android App
- DMS Explorer --
[[Google Play](https://play.google.com/store/apps/details?id=net.mm2d.dmsexplorer)]
[[Source Code](https://github.com/ohmae/DmsExplorer)]

Sample App

|![](docs/img/1.png)|![](docs/img/2.png)|
|-|-|

## How to use

You can download this library from jCenter. (since Ver.1.7.0)

```gradle
repositories {
    jcenter()
}
```

Add dependencies, as following.

```gradle
dependencies {
    implementation 'net.mm2d:mmupnp:1.8.1'
}
```

### Test release

This library is currently under development of 2.0.0.
Currently alpha version, and I'm making destructive change.

It is distributed in this maven repository.

```gradle
repositories {
    maven {
        url 'https://ohmae.github.com/mmupnp/maven'
    }
}
dependencies {
    implementation 'net.mm2d:mmupnp:2.0.0-alpha6'
}
```

### Initialize and Start

```java
ControlPoint cp = ControlPointFactory.create();
cp.initialize();
// adding listener if necessary.
cp.addDiscoveryListener(...);
cp.addNotifyEventListener(...);
cp.start();
...
```

If you want to specify the network interface, describe the following.

```java
NetworkInterface ni = NetworkInterface.getByName("eth0");
ControlPoint cp = ControlPointFactory.create(ni);
```

By default ControlPoint will work with dual stack of IPv4 and IPv6.
If you want to operate with IPv4 only, specify the protocol as follows.

```java
ControlPoint cp = ControlPointFactory.create(Protocol.IP_V4_ONLY);
```

### M-SEARCH
Call ControlPoint#search() or ControlPoint#search(String).

```java
cp.search();                   // Default ST is ssdp:all
```

```java
cp.search("upnp:rootdevice"); // To use specific ST. In this case "upnp:rootdevice"
```

These methods send one M-SEARCH packet to all interfaces.

### Invoke Action
For example, to invoke "Browse" (ContentDirectory) action...

```java
...
Device mediaServer = cp.getDevice(UDN);           // get device by UDN
Action browse = mediaServer.findAction("Browse"); // find "Browse" action
Map<String, String> arg = new HashMap<>();        // setup arguments
arg.put("ObjectID", "0");
arg.put("BrowseFlag", "BrowseDirectChildren");
arg.put("Filter", "*");
arg.put("StartingIndex", "0");
arg.put("RequestedCount", "0");
arg.put("SortCriteria", "");
Map<String, String> result = browse.invoke(arg);  // invoke action
String resultXml = result.get("Result");          // get result
...
```

### Event Subscription
For example, to subscribe ContentDirectory's events...

```java
...
// add listener to receive event
cp.addNotifyEventListener(new NotifyEventListener(){
  public void onNotifyEvent(Service service, long seq, String variable, String value) {
    ...
  }
});
Device mediaServer = cp.getDevice(UDN);          // get device by UDN
Service cds = mediaServer.findServiceById(
  "urn:upnp-org:serviceId:ContentDirectory");    // find Service by ID
cds.subscribe(); // Start subscribe
...
cds.unsubscribe(); // End subscribe
```

### Stop and Terminate

```java
...
cp.stop();
cp.removeDiscoveryListener(...);
cp.removeNotifyEventListener(...);
cp.terminate();
```

It is not possible to re-initialize.
When you want to reset, try again from the constructor call.

### Debug log output

This library use [log library](https://github.com/ohmae/log),

If you want to enable debug log.

```java
Log.initialize(true, true);
```

If you want send log to some library. eg.

```kotlin
Log.setPrint { level, tag, message ->
    if (level >= Log.DEBUG) {
        SomeLogger.send("$tag $message")
    }
}
```

Please see [log library](https://github.com/ohmae/log) for more details

### Documents

I described Javadoc comments. Please refer to it for more information.
- [Javadoc in Japanese](https://ohmae.github.io/mmupnp/javadoc/)

## Author
大前 良介 (OHMAE Ryosuke)
http://www.mm2d.net/

## License
[MIT License](./LICENSE)
