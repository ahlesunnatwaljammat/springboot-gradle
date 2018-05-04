Generate springboot project from https://start.spring.io

Create a single Jar with all dependencies
<pre>
task fatJar(type: Jar) {
    manifest {
        attributes 'Implementation-Title': 'Gradle Jar File Example',
            'Implementation-Version': version,
            'Main-Class': 'edu.learn.bootgradle.PostData'
    }

    baseName = project.name + '-all'
    from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
    with jar
}
</pre>

Enabling jar task to create a jar without including the dependencies
<pre>
jar {
    enabled = true
}
</pre>

If project have multiple Main classes then you can specify the your desired main class
<pre>
bootJar {
    mainClassName = 'edu.learn.bootgradle.BootGradleApp'
}
</pre>

Test cases will fail if project have multiple main classes then specify the your desired main class in spring test
<pre>
@SpringBootTest(classes = BootGradleApp.class)
</pre>
