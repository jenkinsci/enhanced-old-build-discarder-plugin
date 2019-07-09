/**
 *
 */
package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

	private FreeStyleProject projectInstantiation() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		FreeStyleProject project = mock(FreeStyleProject.class);
		project = spy(jRule.createFreeStyleProject("test"));
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse("20110101"));

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

		return project;
	}

	@Test
	public void testPerformHoldMaxBuildsFirstCnd() throws Exception {
		// testing for circumstance where builds to be discarded
		// are greater in amount than builds present, causing build discard queue to be cleared
		FreeStyleProject p = spy(projectInstantiation());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		Calendar cal = new GregorianCalendar();
		cal.setTime(sdf.parse("20120101"));

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"10", "20", "", "",
				false, true));
		when(publisher.cal()).thenReturn(cal);
		publisher.perform(p);
		List<? extends Run<?,?>> buildListPost = p.getBuilds();
		assertEquals(10, buildListPost.size());
	}

	@Test
	public void testPerformHoldMaxBuildsSecondCnd() throws Exception {
		// testing for circumstance where only max build quantity is kept
		// while remaining build history is cleared since it exceeds max age
		FreeStyleProject p = spy(projectInstantiation());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		Calendar cal = new GregorianCalendar();
		cal.setTime(sdf.parse("20120101"));

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"10", "5", "", "",
				false, true));
		when(publisher.cal()).thenReturn(cal);
		publisher.perform(p);
		List<? extends Run<?,?>> buildListPost = p.getBuilds();
		assertEquals(5, buildListPost.size());
	}

	@Test
	public void testPerformHoldMaxBuildsThirdCnd() throws Exception {
		// testing for circumstance where no builds are cleared since logs are beneath max age
		// despite exceeding max build quantity
		FreeStyleProject p = spy(projectInstantiation());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		Calendar cal = new GregorianCalendar();
		cal.setTime(sdf.parse("20120101"));

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"100000", "5", "", "",
				false, true));
		when(publisher.cal()).thenReturn(cal);
		publisher.perform(p);
		List<? extends Run<?,?>> buildListPost = p.getBuilds();
		assertEquals(10, buildListPost.size());
	}

	@Test
	public void testPerformHoldMaxBuildsFourthCnd() throws Exception {
		// testing for circumstance where max builds is undeclared and days to keep is 1
		// forcing deletion of all builds except the last successful one
		FreeStyleProject p = spy(projectInstantiation());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
		Calendar cal = new GregorianCalendar();
		cal.setTime(sdf.parse("20120101"));

		EnhancedOldBuildDiscarder publisher = spy(new EnhancedOldBuildDiscarder(
				"1", "", "", "",
				false, true));

		when(publisher.cal()).thenReturn(cal);
		publisher.perform(p);
		List<? extends Run<?,?>> buildListPost = p.getBuilds();
		assertEquals(1, buildListPost.size());
	}
}
