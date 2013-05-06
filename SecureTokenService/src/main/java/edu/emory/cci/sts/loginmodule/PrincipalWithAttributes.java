package edu.emory.cci.sts.loginmodule;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

public class PrincipalWithAttributes implements Principal{

	private Principal principal;
	private Map<String,Object> attributes;
	
	public PrincipalWithAttributes(final String name)
	{
		principal =  new Principal() {
			
			public String getName() {
			
				return name;
			}
		};
		attributes = new HashMap<String, Object>();
	}
	public Principal getPrincipal() {
		return principal;
	}


	public void setPrincipal(Principal principal) {
		this.principal = principal;
	}


	public Map<String, Object> getAttributes() {
		return attributes;
	}


	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}


	public PrincipalWithAttributes(Principal principal)
	{
		this.principal = principal;
		attributes = new HashMap<String, Object>();
	}
	
	public void setAttribute(String name,Object value)
	{
		attributes.put(name, value);
	}
	
	public <T> T getAttribute(String name)
	{
		return (T) attributes.get(name);
	}
	
	
	public String getName() {
		
		return principal.getName();
	}

	public String toString()
	{
		return getName() + ":" + attributes ;
	}
}
