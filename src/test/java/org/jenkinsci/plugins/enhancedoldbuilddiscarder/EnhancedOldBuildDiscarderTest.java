/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;
import hudson.util.RunList;

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
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse("20120101"));
		when(build1.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120102"));
		when(build2.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120103"));
		when(build3.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120104"));
		when(build4.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120105"));
		when(build5.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120106"));
		when(build6.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120107"));
		when(build7.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120108"));
		when(build8.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120109"));
		when(build9.getTimestamp()).thenReturn(cal);
		cal.setTime(sdf.parse("20120110"));
		when(build10.getTimestamp()).thenReturn(cal);

		// are greater in amount than builds present, causing build discard queue to be cleared
		EnhancedOldBuildDiscarder publisher = (new EnhancedOldBuildDiscarder(
				"10", "20", "", "",
				false, true));

		publisher.perform(project);

		//for (int i = 0; i < 10; i++) {
		//	verify(buildListHMS.get(i), never()).delete();
	//	}

	}
}
