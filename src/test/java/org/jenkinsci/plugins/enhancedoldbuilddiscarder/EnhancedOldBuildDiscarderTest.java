/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
	public void holdMaxBuilds() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		FreeStyleProject project = mock(FreeStyleProject.class);
		project = jRule.createFreeStyleProject("test");
		Calendar cal = Calendar.getInstance();

		FreeStyleBuild build1 = mock(FreeStyleBuild.class);
		build1 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120101"));
		when(build1.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build1);

		FreeStyleBuild build2 = mock(FreeStyleBuild.class);
		build2 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120102"));
		when(build2.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build2);

		FreeStyleBuild build3 = mock(FreeStyleBuild.class);
		build3 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120103"));
		when(build3.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build3);

		FreeStyleBuild build4 = mock(FreeStyleBuild.class);
		build4 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120104"));
		when(build4.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build4);

		FreeStyleBuild build5 = mock(FreeStyleBuild.class);
		build5 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120105"));
		when(build5.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build5);

		FreeStyleBuild build6 = mock(FreeStyleBuild.class);
		build6 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120106"));
		when(build6.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build6);

		FreeStyleBuild build7 = mock(FreeStyleBuild.class);
		build7 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120107"));
		when(build7.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build7);

		FreeStyleBuild build8 = mock(FreeStyleBuild.class);
		build8 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120108"));
		when(build8.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build8);

		FreeStyleBuild build9 = mock(FreeStyleBuild.class);
		build9 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120109"));
		when(build9.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build9);

		FreeStyleBuild build10 = mock(FreeStyleBuild.class);
		build10 = spy(project.scheduleBuild2(0).get());
		cal.setTime(sdf.parse("20120110"));
		when(build10.getTimestamp()).thenReturn(cal);
		jRule.assertBuildStatus(Result.SUCCESS, build10);

		// are greater in amount than builds present, causing build discard queue to be cleared
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"10", "20", "", "",
				false, true));

		publisher.perform(project);
	}
}
