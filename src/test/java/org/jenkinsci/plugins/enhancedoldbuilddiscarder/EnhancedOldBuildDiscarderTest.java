/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Mockito.*;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Test for EnhancedOldBuildDiscarder holdMaxBuilds setting
 * author @BenjaminBeggs
 */

public class EnhancedOldBuildDiscarderTest {

	@Rule
	public JenkinsRule jRule = new JenkinsRule();

	@Test
	public void holdMaxBuilds() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		FreeStyleProject project = mock(FreeStyleProject.class);
		project = spy(jRule.createFreeStyleProject("test"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse("20120101"));

		FreeStyleBuild build1 = mock(FreeStyleBuild.class);
		build1 = spy(project.scheduleBuild2(0).get());
		when(build1.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build1);

		FreeStyleBuild build2 = mock(FreeStyleBuild.class);
		build2 = spy(project.scheduleBuild2(0).get());
		when(build2.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build2);

		FreeStyleBuild build3 = mock(FreeStyleBuild.class);
		build3 = spy(project.scheduleBuild2(0).get());
		when(build3.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build3);

		FreeStyleBuild build4 = mock(FreeStyleBuild.class);
		build4 = spy(project.scheduleBuild2(0).get());
		when(build4.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build4);

		FreeStyleBuild build5 = mock(FreeStyleBuild.class);
		build5 = spy(project.scheduleBuild2(0).get());
		when(build5.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build5);

		FreeStyleBuild build6 = mock(FreeStyleBuild.class);
		build6 = spy(project.scheduleBuild2(0).get());
		when(build6.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build6);

		FreeStyleBuild build7 = mock(FreeStyleBuild.class);
		build7 = spy(project.scheduleBuild2(0).get());
		when(build7.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build7);

		FreeStyleBuild build8 = mock(FreeStyleBuild.class);
		build8 = spy(project.scheduleBuild2(0).get());
		when(build8.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build8);

		FreeStyleBuild build9 = mock(FreeStyleBuild.class);
		build9 = spy(project.scheduleBuild2(0).get());
		when(build9.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build9);

		FreeStyleBuild build10 = mock(FreeStyleBuild.class);
		build10 = spy(project.scheduleBuild2(0).get());
		when(build10.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build10);

		// are greater in amount than builds present, causing build discard queue to be cleared
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"1", "", "", "",
				false, true));

		publisher.perform(project);
		List<? extends Run<?,?>> buildListPost = project.getBuilds();
		assertEquals(0, buildListPost.size());
	}
}
