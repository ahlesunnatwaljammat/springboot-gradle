Generate springboot project from https://start.spring.io

//create a single Jar with all dependencies
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

//Enabling jar task to create a jar without including the dependencies
jar {
    enabled = true
}

//If project have multiple Main classes then you can specify the your desired main class
bootJar {
    mainClassName = 'edu.learn.bootgradle.BootGradleApp'
}

//Test cases will fail if project have multiple main classes then specify the your desired main class in spring test
@SpringBootTest(classes = BootGradleApp.class)