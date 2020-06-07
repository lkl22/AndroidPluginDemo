package com.lkl.standaloneplugin;

import org.gradle.api.Project;
import org.gradle.internal.impldep.org.mozilla.javascript.SymbolScriptable;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void customPluginTest() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.lkl.standaloneplugin.custom-plugin");

        System.out.println(project.getDisplayName());
        assertEquals("root project 'test'", project.getDisplayName());
    }
}