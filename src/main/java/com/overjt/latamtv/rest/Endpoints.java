package com.overjt.latamtv.rest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Path("/")
public class Endpoints {

	@GET
	@Produces("text/plain")
	@Path("/catalog/tv/tv_catalog.json")
	public Response catalog() {
		Client client = new Client();
		JSONArray channels_array = client.getChannels().getJSONArray("canales");
		JSONObject response = new JSONObject();
		JSONArray meta_channels = new JSONArray();
		for (int i = 0; i < channels_array.length(); i++) {
			Meta meta_tmp = new Meta();
			JSONObject channel_json = channels_array.getJSONObject(i);
			meta_tmp.setID("latamtv" + channel_json.getString("id"));
			meta_tmp.setName(channel_json.getString("nombre"));
			meta_tmp.setPoster(channel_json.getString("imagen"));
			meta_tmp.setType("tv");
			JSONObject c = new JSONObject(meta_tmp);
			c.put("id", c.getString("ID")); // Hack, JSON serializer always put the key in Uppercase
			meta_channels.put(c);
		}
		response.put("metas", meta_channels);

		return Response.ok(response).build();
	}

	@GET
	@Produces("text/plain")
	@Path("/manifest.json")
	public Response manifest() {
		File f = new File("manifest.json");
		ResponseBuilder response = Response.ok(f);
		return response.build();
	}

	@GET
	@Produces("text/plain")
	@Path("/")
	public Response index() {
		return Response.ok("go to /manifest.json, Stremio addon").build();
	}

	@GET
	@Produces("text/plain")
	@Path("/stream/tv/latamtv{channel_id}.json")
	public Response tvStream(@PathParam("channel_id") String channel_id) throws JSONException, Exception {
		Client client = new Client();
		JSONObject channe_info = client.getChannel(channel_id);
		
		JSONArray streams = new JSONArray();

		String raw_url = new String(client.util.Decrypt(channe_info.getJSONObject("canal").getString("url")));
		String[] urls = raw_url.split("___");

		for (int i = 0; i < urls.length; i++) {
			String stream_url = urls[i];
			JSONObject stream = new JSONObject();
			String title = channe_info.getJSONObject("canal").getString("nombre") + " | LatamTV";
			if(urls.length > 1){
				title += " Opción #" + (i + 1);
			}
			stream.put("url",  stream_url);
			stream.put("title", title);
			streams.put(stream);
		}
		

		JSONObject response = new JSONObject();
		response.put("streams", streams);


		return Response.ok(response).build();

	}	
}
