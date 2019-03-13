package net.pms.alexa.handler;

import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.audioplayer.PlayBehavior;
import com.amazon.ask.request.Predicates;
import com.amazon.ask.response.ResponseBuilder;

import net.pms.configuration.RendererConfiguration;
import net.pms.configuration.WebRender;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.RootFolder;
import net.pms.remote.RemoteUtil;

public class BaseRequestHandler implements RequestHandler {
	private String intent = null;
	
	public BaseRequestHandler(String intent) {
		this.intent = intent;
	}

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(Predicates.intentName(intent));
		// intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String speechText = "Playing song";
		ResponseBuilder responseBuilder = input.getResponseBuilder();

		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		Intent intent2 = intentRequest.getIntent();
		switch (intent2.getName()) {
		case "SelectSongIntent":
			String criteria = String.format("dc:title contains \"%s\"", intent2.getSlots().get("Query").getValue());
			RendererConfiguration renderer = RendererConfiguration.getDefaultConf();
			RootFolder root = renderer.getRootFolder();
			List<DLNAResource> resources = root.getDLNAResources("0", true, 0, 1, renderer, criteria);

			if (resources.isEmpty()) {
				speechText = "Nothing found";
			} else {
				DLNAResource resource = resources.get(0);
				responseBuilder
					.withShouldEndSession(true)
					.withSimpleCard(speechText, speechText)
					.addAudioPlayerPlayDirective(PlayBehavior.REPLACE_ALL, 0L, null, resource.getId(),
							"https://3cc4d2c3.ngrok.io/get/" + resource.getId() + "/XPWB.mp3_transcoded_to.mp3");
			}
			break;
		case "ListDevicesIntent":
			speechText = listDevices();
			break;
		}

		return responseBuilder
				.withSpeech(speechText)
				.withReprompt(speechText) // "AMAZON.HelpIntent" "AMAZON.FallbackIntent"
				.build();
	}

	private String listDevices() {
		String speechText;
		List<RendererConfiguration> players = RendererConfiguration.getConnectedControlPlayers();
		if (players.isEmpty()) {
			speechText = "No device found.";
		} else {
			speechText = "Found following devices";
//			speechText += "    Here is a number <w role='amazon:VBD'>read</w> ";
//			speechText += "    as a cardinal number: ";
//			speechText += "    <say-as interpret-as='cardinal'>12345</say-as>. ";
//			speechText += "    <say-as interpret-as='ordinal'>5</say-as>. ";
//			speechText += "    Here is a word spelled out: ";
//			speechText += "    <say-as interpret-as='spell-out'>hello</say-as>. ";

			int i = 1;
			for (RendererConfiguration r : players) {
				speechText += "    <say-as interpret-as='ordinal'>" + i + "</say-as>. ";
				speechText += r.getRendererName();
			}
		}
		return speechText;
	}
}