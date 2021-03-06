import org.example.*

// tag::failed-inferred-task-dep[]
plugins {
    // end::failed-inferred-task-dep[]
    base
// tag::failed-inferred-task-dep[]
    java
}

// end::failed-inferred-task-dep[]

repositories {
    jcenter()
}

dependencies {
    implementation(group = "commons-collections", name = "commons-collections", version = "3.2.2")
    testImplementation(group = "junit", name = "junit", version = "4.+")
}

// tag::custom-task-class[]
task<ProcessTemplates>("processTemplates") {
    templateEngine = TemplateEngineType.FREEMARKER
    templateData = TemplateData("test", mapOf("year" to "2012"))
    outputDir = file("$buildDir/genOutput")

    sources(fileTree("src/templates"))
}
// end::custom-task-class[]

// tag::task-arg-method[]
val copyTemplates by tasks.creating(Copy::class) {
    into("$buildDir/tmp")
    from("src/templates")
}

task<ProcessTemplates>("processTemplates2") {
    // ...
// end::task-arg-method[]
    templateEngine = TemplateEngineType.FREEMARKER
    templateData = TemplateData("test", mapOf("year" to "2012"))
    outputDir = file("$buildDir/genOutput")
// tag::task-arg-method[]
    sources(copyTemplates)
}
// end::task-arg-method[]

// tag::failed-inferred-task-dep[]
task<Instrument>("badInstrumentClasses") {
    classFiles = fileTree(tasks.compileJava.get().destinationDir)
    destinationDir = file("$buildDir/instrumented")
}
// end::failed-inferred-task-dep[]

// tag::inferred-task-dep[]
task<Instrument>("instrumentClasses") {
    classFiles = tasks.compileJava.get().outputs.files
    destinationDir = file("$buildDir/instrumented")
}
// end::inferred-task-dep[]

// tag::inferred-task-dep-with-files[]
task<Instrument>("instrumentClasses2") {
    classFiles = layout.files(tasks.compileJava.get())
    destinationDir = file("$buildDir/instrumented")
}
// end::inferred-task-dep-with-files[]

// tag::inferred-task-dep-with-builtby[]
task<Instrument>("instrumentClassesBuiltBy") {
    classFiles = fileTree(tasks.compileJava.get().destinationDir) {
        builtBy(tasks.compileJava.get())
    }
    destinationDir = file("$buildDir/instrumented")
}
// end::inferred-task-dep-with-builtby[]

// tag::up-to-date-when[]
task<Instrument>("alwaysInstrumentClasses") {
    classFiles = layout.files(tasks.compileJava.get())
    destinationDir = file("$buildDir/instrumented")
    outputs.upToDateWhen { false }
}
// end::up-to-date-when[]

tasks.build { dependsOn("processTemplates", "processTemplates2") }
