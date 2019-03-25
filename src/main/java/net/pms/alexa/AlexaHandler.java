package net.pms.alexa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
//import com.amazon.ask.Skills;
import com.amazon.ask.model.RequestEnvelope;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.ResponseEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.pms.alexa.handler.LaunchRequestHandler;
import net.pms.alexa.handler.SessionEndedRequestHandler;
import net.pms.remote.RemoteUtil;

public class AlexaHandler implements HttpHandler {
	private List<RequestHandler> handlers = new ArrayList<RequestHandler>();
	
	public AlexaHandler() {
		handlers.add(new LaunchRequestHandler());
		handlers.add(new SessionEndedRequestHandler());
//		handlers.add(new HelpIntentHandler());
//		handlers.add(new CancelandStopIntentHandler());
//		handlers.add(new HelloWorldIntentHandler());
	}

	@Override
	public void handle(HttpExchange t) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.findAndRegisterModules();
		RequestEnvelope requestEnvelope = objectMapper.readValue(t.getRequestBody(), RequestEnvelope.class);
		
		HandlerInput input = HandlerInput.builder().withRequestEnvelope(requestEnvelope).build();
		Optional<Response> response = Optional.empty();
		for (RequestHandler handler : handlers) {
			if (handler.canHandle(input)) {
				response = handler.handle(input);
				break;
			}
		}
		
		ResponseEnvelope resp = ResponseEnvelope.builder().withResponse(response.get()).build();
		String res = objectMapper.writeValueAsString(resp);

		RemoteUtil.respond(t, res, 200, "application/json");
	}
}
