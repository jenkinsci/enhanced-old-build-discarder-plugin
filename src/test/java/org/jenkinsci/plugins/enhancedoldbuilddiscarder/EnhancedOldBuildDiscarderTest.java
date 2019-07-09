/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;
import hudson.util.RunList;
import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test for EnhancedOldBuildDiscarder holdMaxBuilds setting
 * author @BenjaminBeggs
 */

public class EnhancedOldBuildDiscarderTest {

	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	@Test
	public void successVsFailure() throws Exception {
		FreeStyleProject project = jRule.createFreeStyleProject("test");
		FreeStyleBuild build1 = project.scheduleBuild2(0).get();
		FreeStyleBuild build2 = project.scheduleBuild2(0).get();
		FreeStyleBuild build3 = project.scheduleBuild2(0).get();
		FreeStyleBuild build4 = project.scheduleBuild2(0).get();
		FreeStyleBuild build5 = project.scheduleBuild2(0).get();
		FreeStyleBuild build6 = project.scheduleBuild2(0).get();
		FreeStyleBuild build7 = project.scheduleBuild2(0).get();
		FreeStyleBuild build8 = project.scheduleBuild2(0).get();
		FreeStyleBuild build9 = project.scheduleBuild2(0).get();
		FreeStyleBuild build10 = project.scheduleBuild2(0).get();
		jRule.assertBuildStatus(Result.SUCCESS, build1);
		jRule.assertBuildStatus(Result.SUCCESS, build2);
		jRule.assertBuildStatus(Result.SUCCESS, build3);
		jRule.assertBuildStatus(Result.SUCCESS, build4);
		jRule.assertBuildStatus(Result.SUCCESS, build5);
		jRule.assertBuildStatus(Result.SUCCESS, build6);
		jRule.assertBuildStatus(Result.SUCCESS, build7);
		jRule.assertBuildStatus(Result.SUCCESS, build8);
		jRule.assertBuildStatus(Result.SUCCESS, build9);
		jRule.assertBuildStatus(Result.SUCCESS, build10);
	}
}
