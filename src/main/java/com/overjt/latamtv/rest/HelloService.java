package com.overjt.latamtv.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.util.Date;
import java.util.HashMap;

@Path("/greet")
public class HelloService {

	@GET
	@Produces("text/plain")
	public Response doGet() {
		Client x = new Client();
		JSONObject channels = x.getChannels();

		
		return Response.ok(channels).build();
	}
}
