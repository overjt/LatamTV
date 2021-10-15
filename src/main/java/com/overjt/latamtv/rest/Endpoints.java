package com.overjt.latamtv.rest;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/")
public class Endpoints {

	private static String SERVER_HOSTNAME = "https://tvlatam.herokuapp.com";

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
			meta_tmp.setDescription(channel_json.getString("nombre"));
			meta_tmp.setPoster(channel_json.getString("imagen"));
			meta_tmp.setType("tv");
			JSONObject c = new JSONObject(meta_tmp);
			c.put("id", c.getString("ID")); // Hack, JSON serializer always put the key in Uppercase
			meta_channels.put(c);
		}
		response.put("metas", meta_channels);
		response.put("cacheMaxAge", "1");


		return Response.ok(response).build();
	}

	@GET
	@Produces("text/plain")
	@Path("/meta/tv/latamtv{channel_id}.json")
	public Response meta_channel(@PathParam("channel_id") String channel_id) {
		Client client = new Client();
		JSONObject channe_info = client.getChannel(channel_id).getJSONObject("canal");

		JSONObject meta = new JSONObject();
		meta.put("id", "latamtv" + channe_info.getString("id"));
		meta.put("type", "tv");
		meta.put("name", channe_info.getString("nombre") + " | LatamTV");
		meta.put("poster", channe_info.getString("imagen"));
		meta.put("background", channe_info.getString("imagen"));
		meta.put("description", channe_info.getString("nombre"));


		JSONObject response = new JSONObject();
		response.put("meta", meta);
		response.put("cacheMaxAge", "1");

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
	@Path("/media/logo.png")
	@Produces("image/png")
	public Response logo() {
		File f = new File("logo.png");
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
	@Path("/proxy/{proxy_filename}")
	@Produces("application/vnd.apple.mpegurl")
	public Response proxy(@QueryParam("url") String url, @QueryParam("referer") String referer,
			@QueryParam("proxy_step") String proxy_step, @QueryParam("ua") String user_agent, @Context UriInfo uriInfo, @QueryParam("stream_type") String stream_type)
			throws IllegalArgumentException, UriBuilderException, UnsupportedEncodingException {
		HashMap<String, String> headers = new HashMap<String, String>();

		if (referer != null && !referer.isEmpty()) {
			headers.put("Referer", referer);
		} else {
			referer = "";
		}

		if (user_agent != null && !user_agent.isEmpty()) {
			headers.put("User-Agent", user_agent);
		} else {
			user_agent = "";
		}

		ProxyClient proxy_client = new ProxyClient();

		if (stream_type.equals("7")){
			return Response.ok(proxy_client.streamWebContents(url, headers)).build();
		}
		
		String body = proxy_client.getWebContents(url, headers);
		

		if (proxy_step != null && !proxy_step.isEmpty()) {
			if (!proxy_step.equals("3")) {
				String[] parts_url = url.split("/");
				String tmp_str = "latamtv_temp" + UriBuilder.fromUri(SERVER_HOSTNAME).path("/proxy/latamtv_filename_temp")
						.queryParam("proxy_step", Integer.parseInt(proxy_step) + 1).queryParam("referer", referer)
						.queryParam("ua", user_agent).build().toString();
				body = body.replaceAll("(?m)^(?![http:https])(\\w.+)", tmp_str + "&url="
						+ URLEncoder.encode(String.join("/", Arrays.copyOf(parts_url, parts_url.length - 1)), "UTF-8")
						+ "/$1");
				body = body.replaceAll("(?m)^(?!latamtv_temp)(\\w.+)", tmp_str + "&url=$1");
				body = body.replaceAll("(?m)(^latamtv_temp)", "");
				String[] body_lines = body.split("\n");
				for (int i = 0; i < body_lines.length; i++) {
					body_lines[i] = body_lines[i].replace("latamtv_filename_temp", body_lines[i].split("/")[body_lines[i].split("/").length - 1]);
				}
				body = String.join("\n", body_lines);
			}
		} else {
			return Response.noContent().build();
		}
		return Response.ok(body).build();
	}

	@GET
	@Produces("text/plain")
	@Path("/stream/tv/latamtv{channel_id}.json")
	public Response tvStream(@PathParam("channel_id") String channel_id, @Context UriInfo uriInfo)
			throws JSONException, Exception {
		Client client = new Client();
		JSONObject channe_info = client.getChannel(channel_id);

		JSONArray streams = new JSONArray();

		String raw_url = new String(client.util.Decrypt(channe_info.getJSONObject("canal").getString("url")));
		String[] urls = raw_url.split("___");
		String stream_type = channe_info.getJSONObject("canal").getString("tipo");
		String stream_ua = channe_info.getJSONObject("canal").getString("ua");
		String title = channe_info.getJSONObject("canal").getString("nombre") + " | LatamTV";

		if (stream_type.equals("5")) {
			String stream_url = urls[0];
			JSONObject stream = new JSONObject();
			String referer = "";

			if (urls.length > 1) {
				try {
					referer = urls[1].split(",")[1];
				} catch (Exception e) {
				}
			}

			String url_filename = "";
			try {
				String[] parts_url = stream_url.split("/");
				url_filename = parts_url[parts_url.length - 1];
			} catch (Exception e) {
				url_filename = "";
			}

			stream_url = UriBuilder.fromUri(SERVER_HOSTNAME).path("/proxy/" + url_filename)
					.queryParam("proxy_step", "1").queryParam("url", stream_url).queryParam("ua", stream_ua)
					.queryParam("referer", referer).build().toString();

			stream.put("url", stream_url);
			stream.put("title", title);
			streams.put(stream);
		} else {
			for (int i = 0; i < urls.length; i++) {
				String stream_url = urls[i];
				JSONObject stream = new JSONObject();
				String stream_title = title;
				if (urls.length > 1) {
					stream_title += " Opción #" + (i + 1);
				}

				String url_filename = "";
				try {
					String[] parts_url = stream_url.split("/");
					url_filename = parts_url[parts_url.length - 1];
				} catch (Exception e) {
					url_filename = "";
				}

				if ((stream_type.equals("0") || stream_type.equals("7")) && !stream_ua.isEmpty()) {
					stream_url = UriBuilder.fromUri(SERVER_HOSTNAME).path("/proxy/" + url_filename)
							.queryParam("proxy_step", "1").queryParam("url", stream_url).queryParam("ua", stream_ua)
							.queryParam("stream_type", stream_type)
							// .queryParam("referer", "")
							.build().toString();
				}
				stream.put("url", stream_url);
				stream.put("title", stream_title);
				streams.put(stream);
			}
		}

		JSONObject response = new JSONObject();
		response.put("streams", streams);
		response.put("cacheMaxAge", "1");

		return Response.ok(response).build();

	}
}
