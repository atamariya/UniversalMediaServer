package net.pms.alexa.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.audioplayer.PlayBehavior;
import com.amazon.ask.request.Predicates;
import com.amazon.ask.response.ResponseBuilder;

import net.pms.alexa.Utterance;
import net.pms.configuration.RendererConfiguration;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.RootFolder;

public class BaseRequestHandler implements RequestHandler {
	private String intentName = null;
	
	public BaseRequestHandler(String intent) {
		this.intentName = intent;
	}

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(Predicates.intentName(intentName));
		// intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String speechText = Utterance.get(Utterance.DEFAULT);
		ResponseBuilder responseBuilder = input.getResponseBuilder();
		Map<String, Object> attributes = input.getAttributesManager().getSessionAttributes();

		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent = intentRequest.getIntent();
		switch (intent.getName()) {
		case "SelectSongIntent":
			speechText = playSong(responseBuilder, intent);
			break;
		case "ListDevicesIntent":
			speechText = listDevices(attributes);
			break;
		}

		return responseBuilder
				.withSpeech(speechText)
				.withReprompt(speechText) // "AMAZON.HelpIntent" "AMAZON.FallbackIntent"
				.build();
	}

	private String playSong(ResponseBuilder responseBuilder, Intent intent) {
		String speechText = null;
		String criteria = String.format("dc:title contains \"%s\"", intent.getSlots().get("Query").getValue());
		RendererConfiguration renderer = RendererConfiguration.getDefaultConf();
		RootFolder root = renderer.getRootFolder();
		List<DLNAResource> resources = root.getDLNAResources("0", true, 0, 1, renderer, criteria);

		if (resources.isEmpty()) {
			speechText = Utterance.get("song.not.found");
		} else {
			DLNAResource resource = resources.get(0);
			String title = "Playing";
			speechText = "Playing " + resource.getDisplayName();
			responseBuilder
				.withShouldEndSession(true)
				.withSimpleCard(title, speechText)
				.addAudioPlayerPlayDirective(PlayBehavior.REPLACE_ALL, 0L, null, resource.getId(), resource.getURL(""));
		}
		return speechText;
	}

	private String listDevices(Map<String, Object> attributes) {
		String speechText = null;
		List<RendererConfiguration> players = RendererConfiguration.getConnectedControlPlayers();
		if (players.isEmpty()) {
			speechText = Utterance.get("device.not.found");
		} else {
			speechText = "Found following devices";
//			speechText += "    Here is a number <w role='amazon:VBD'>read</w> ";
//			speechText += "    as a cardinal number: ";
//			speechText += "    <say-as interpret-as='cardinal'>12345</say-as>. ";
//			speechText += "    <say-as interpret-as='ordinal'>5</say-as>. ";
//			speechText += "    Here is a word spelled out: ";
//			speechText += "    <say-as interpret-as='spell-out'>hello</say-as>. ";

			int i = 1;
			Map<String, String> renderers = new HashMap<String, String>();
			for (RendererConfiguration r : players) {
				speechText += "    <say-as interpret-as='ordinal'>" + i + "</say-as>. ";
				speechText += r.getRendererName();
				renderers.put(r.getUUID(), r.getRendererName());
			}
			attributes.put("renderers", renderers);
		}
		return speechText;
	}
}