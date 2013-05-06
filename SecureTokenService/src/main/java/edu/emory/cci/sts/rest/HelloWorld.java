package edu.emory.cci.sts.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

@Path("helloWorld")
public class HelloWorld  {

	@GET
	public String info()
	{
		return "HelloWorld";
	}
	
}
