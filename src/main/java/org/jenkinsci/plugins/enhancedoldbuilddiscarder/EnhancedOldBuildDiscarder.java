package org.jenkinsci.plugins.enhancedoldbuilddiscarder;

import hudson.Extension;
import hudson.model.Job;

import java.io.IOException;

import jenkins.model.BuildDiscarder;
import jenkins.model.BuildDiscarderDescriptor;

import org.kohsuke.stapler.DataBoundConstructor;

public class EnhancedOldBuildDiscarder extends BuildDiscarder {
	
	private boolean discardOnlyOnSuccess;

	@DataBoundConstructor
    public EnhancedOldBuildDiscarder (
    		String daysToKeepStr, 
    		String numToKeepStr, 
    		String artifactDaysToKeepStr, 
    		String artifactNumToKeepStr,
    		boolean discardOnlyOnSuccess) {
//        this (parse(daysToKeepStr),
//        	   parse(numToKeepStr),
//               parse(artifactDaysToKeepStr),
//               parse(artifactNumToKeepStr),
//               discardOnlyOnSuccess);
    }
	
	public EnhancedOldBuildDiscarder(
			int daysToKeep, 
			int numToKeep, 
			int artifactDaysToKeep, 
			int artifactNumToKeep,
			boolean discardOnlyWhenLastBuildIsSuccess) {
		//super(daysToKeep,numToKeep,artifactDaysToKeep,artifactNumToKeep);
		this.discardOnlyOnSuccess = discardOnlyWhenLastBuildIsSuccess;
	}
	
	@Override
	public void perform(Job<?, ?> job) throws IOException, InterruptedException {
//		super.perform(job);
	}

	@Extension
    public static final class EnhancedOldBuildDiscarderDescriptor extends BuildDiscarderDescriptor {
        public String getDisplayName() {
            return "Enhanced Log Rotation";
        }
    }
}
