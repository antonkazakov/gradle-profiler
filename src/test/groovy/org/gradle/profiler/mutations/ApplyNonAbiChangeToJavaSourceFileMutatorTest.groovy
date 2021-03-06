package org.gradle.profiler.mutations

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static com.github.javaparser.JavaParser.parse

class ApplyNonAbiChangeToJavaSourceFileMutatorTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder()

    def "changes the first method in the source file"() {
        def sourceFile = tmpDir.newFile("Thing.java")
        sourceFile.text = "class Thing { public void existingMethod() { }}"
        def mutator = new ApplyNonAbiChangeToJavaSourceFileMutator(sourceFile)
        mutator.timestamp = 1234

        when:
        mutator.beforeBuild()

        then:
        parse(sourceFile) == parse('class Thing { public void existingMethod() { System.out.println("_1234_1");}}')

        when:
        mutator.beforeBuild()

        then:
        parse(sourceFile) == parse('class Thing { public void existingMethod() { System.out.println("_1234_2");}}')

        when:
        mutator.beforeBuild()

        then:
        parse(sourceFile) == parse('class Thing { public void existingMethod() { System.out.println("_1234_3");}}')
    }

    def "does not work with Java files that do not contain a method"() {
        def sourceFile = tmpDir.newFile("Thing.java")
        sourceFile.text = "class Thing { }"
        def mutator = new ApplyNonAbiChangeToJavaSourceFileMutator(sourceFile)
        mutator.timestamp = 1234

        when:
        mutator.beforeBuild()

        then:
        IllegalArgumentException t = thrown()
        t.message == "No methods to change in " + sourceFile
    }
}
