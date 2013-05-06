package edu.emory.cci.sts.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class ApplicationLocator extends Application{

	public Set<Class<?>> getClasses() {
		           Set<Class<?>> s = new HashSet<Class<?>>();
		           s.add(HelloWorld.class);
		           s.add(STSRest.class);
		           return s;
		       }
}
