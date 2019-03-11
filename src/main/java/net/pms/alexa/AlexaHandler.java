package net.pms.alexa;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
//import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.ResponseEnvelope;
import com.amazon.ask.response.ResponseBuilder;
import com.amazon.ask.util.JacksonSerializer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.pms.alexa.handler.LaunchRequestHandler;
import net.pms.alexa.handler.SessionEndedRequestHandler;
import net.pms.network.HTTPResource;
import net.pms.remote.RemoteUtil;
import net.pms.remote.RemoteWeb;

public class AlexaHandler implements HttpHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlexaHandler.class);
	
	private boolean initialised = false;
	private boolean authenticated = false;
	private RemoteWeb parent;
	private List<RequestHandler> handlers = new ArrayList<RequestHandler>();
	private JacksonSerializer serializer = new JacksonSerializer();
	private ObjectMapper objectMapper = new ObjectMapper();
	
	static class User {
		public String userId, name, email;

		public User() {
		}
	}

	public AlexaHandler(RemoteWeb parent) {
		this.parent = parent;
	}
	
	
	public void init() {
		if (!initialised) {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.findAndRegisterModules();

			try {
				Properties mapping = new Properties();
				mapping.load(parent.getResources().getInputStream("resources/alexa.config.properties"));
				
				handlers.add(new LaunchRequestHandler());
				handlers.add(new SessionEndedRequestHandler());
//				handlers.add(new HelpIntentHandler());
//				handlers.add(new CancelandStopIntentHandler());
				
				for (Object key : mapping.keySet()) {
					String intent = (String) key;
					String className = mapping.getProperty(intent);
					if (className.isEmpty())
						continue;
					
					Class<?> cl = Class.forName(className);
					Constructor<?> cons = cl.getConstructor(String.class);
					RequestHandler handler = (RequestHandler) cons.newInstance(intent);
					handlers.add(handler);
				}
				
				initialised = true;
			} catch (Exception e) {
				LOGGER.error("Failed to load properties: {}", e.getMessage());
			}
			
		}
	}
	
	@Override
	public void handle(HttpExchange t) throws IOException {
		InputStream requestBody = t.getRequestBody();
		String res = handle(requestBody);
		RemoteUtil.respond(t, res, 200, "application/json");
	}
	
	public String handle(InputStream requestBody) throws IOException{
		init();

		RequestEnvelope requestEnvelope = objectMapper.readValue(requestBody, RequestEnvelope.class);
		
		// Authorize
		String accessToken = requestEnvelope.getContext().getSystem().getUser().getAccessToken();
		if (!authenticated && accessToken != null) {
			String amznProfileUrl = "https://api.amazon.com/user/profile?access_token=%s";
			amznProfileUrl = String.format(amznProfileUrl, accessToken);
			InputStream is = HTTPResource.downloadAndSend(amznProfileUrl, false);
			User user = objectMapper.readValue(is, User.class);

			if ("atamariya@gmail.com".equals(user.email)) {
				LOGGER.debug("valid user");
				authenticated = true;
			} else {
				authenticated = false;
			}
		}

		Optional<Response> response = Optional.empty();
		HandlerInput input = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
		if (authenticated) {
			for (RequestHandler handler : handlers) {
				if (handler.canHandle(input)) {
					response = handler.handle(input);
					break;
				}
			}
		} else {
			String speechText = "Please log in";
			ResponseBuilder responseBuilder = input.getResponseBuilder();
			response = responseBuilder
					.withSpeech(speechText)
					.withLinkAccountCard()
					.build();
		}
		
		ResponseEnvelope resp = ResponseEnvelope.builder().withResponse(response.get()).build();
		String res = objectMapper.writeValueAsString(resp);
		return res;
	}
}
