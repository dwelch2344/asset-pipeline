asset-pipeline
=========


This project aims to be a simple asset pipeline for JSP applications. It was inspired by the likes of [BakeHouse]. Check out the [demo project] or see the quickstart.

QuickStart
-------

1. Add my GitHub Maven Repository & the *asset-pipeline* dependency to your *pom.xml*. This example uses the Google Closure adapter (which is the only implementation available now)
```xml
    <repositories>
		<repository>
			<id>ntier-repo</id>
			<url>https://github.com/dwelch2344/maven-repo/raw/master/releases</url>
		</repository>
		<repository>
			<id>ntier-snapshot-repo</id>
			<url>https://github.com/dwelch2344/maven-repo/raw/master/snapshots</url>
			<snapshots>
			</snapshots>
		</repository>
	</repositories>

	<dependencies>
		<dependency>
			<groupId>co.ntier.web</groupId>
			<artifactId>asset-pipeline-closure</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
		<!-- Your dependencies HERE! -->
	</dependencies>
```
2. Configure your *ServletContext* as appropriate. The following example uses Spring's JavaConfig to hook things up correctly
```java
public class SomeConfig {
	@Inject 
	private ServletContext ctx;
	
	@PostConstruct
	public void onSetup(){
		// comment the next line out to prevent minification
		ctx.setAttribute(PipelineConstants.IS_PRODUCTION_KEY, true);
		ctx.setAttribute(PipelineConstants.RESOURCE_COMPILER_KEY, new ClosureResourceCompiler());
	}
}
```
3. Declare your resources in your JSP
```jsp
<%@ taglib prefix="pipeline" uri="http://ntier.co/pipeline" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  
  <!-- Generated code will be here -->
  <pipeline:scripts ref="/gen/app.js">
		<pipeline:script src="/resources/app1.js"/>
		<pipeline:script src="/resources/app2.js"/>
		<%-- <pipeline:script src="/resources/app3.js"/> --%>
	</pipeline:scripts>
</head>
```

4. Enjoy!



When developing, just comment out the ``` IS_PRODUCTION_KEY ``` line from the configuration file and you'll get the raw resources. 


 [BakeHouse]: https://github.com/TheMangoFactory/bakehouse
 [demo project]: https://github.com/dwelch2344/asset-pipeline-demo

