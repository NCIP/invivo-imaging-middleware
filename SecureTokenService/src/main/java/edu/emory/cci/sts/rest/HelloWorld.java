/*L
* Copyright The Ohio State University
* Copyright Emory University
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.io/invivo-imaging-middleware/LICENSE.txt for details.
*/

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
