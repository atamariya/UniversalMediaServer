package net.pms.alexa.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.DialogState;
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
		// AMAZON.YesIntent and AMAZON.NoIntent
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String speechText = Utterance.get(Utterance.DEFAULT);
		ResponseBuilder responseBuilder = input.getResponseBuilder();
		Map<String, Object> attributes = input.getAttributesManager().getSessionAttributes();

		IntentRequest intentRequest = (IntentRequest) input.getRequestEnvelope().getRequest();
		DialogState dialogState = intentRequest.getDialogState();
		Intent intent = intentRequest.getIntent();
		switch (intent.getName()) {
		case "SelectSongIntent":
			speechText = playSong(responseBuilder, intent, attributes);
			break;
		case "ListDevicesIntent":
			speechText = listDevices(attributes);
			break;
		case "NameDeviceIntent":
		case "AMAZON.YesIntent":
		case "AMAZON.NoIntent":
			speechText = nameDevices(responseBuilder, intent, dialogState, attributes);
			break;
		case "AMAZON.FallbackIntent":
			speechText = Utterance.get(Utterance.START);
			break;
		}

		return responseBuilder
				.withSpeech(speechText)
				.withReprompt(speechText) // "AMAZON.HelpIntent" "AMAZON.FallbackIntent"
				.build();
	}

	private String nameDevices(ResponseBuilder responseBuilder, Intent intent, DialogState dialogState, Map<String, Object> attributes) {
		String speechText = null;
		Map<String, String> renderers = null;
		Map<String, String> named = (Map<String, String>) attributes.get("renderers.named");
		boolean found = false;
		boolean rename = false;
		
		if (intent.getName().equals("AMAZON.NoIntent")) {
			return Utterance.get("ok");
		}
		
		if (intent.getName().equals("AMAZON.YesIntent")) {
			if (named != null)
				named.clear();
			rename = true;
			intent = Intent.builder().withName("NameDeviceIntent")
					.build();
//			responseBuilder.addDelegateDirective(intent);
		}
		if (dialogState.equals(DialogState.STARTED)) {
			List<RendererConfiguration> players = RendererConfiguration.getConnectedControlPlayers();
			if (!players.isEmpty()) {
				renderers = new HashMap<String, String>();
				for (RendererConfiguration r : players) {
					String uuid = r.getUUID();
					if (uuid != null) {
						renderers.put(uuid, r.getRendererName());
					}
				}
				
				if (renderers.size() > 0) {
					found = true;
					attributes.put("renderers", renderers);
				}
			}
			if (!found) {
				// No usable device found
				speechText = Utterance.get("device.not.found");
				
//				responseBuilder.addDelegateDirective(intent);
				return speechText;
			}

		} else {
			speechText = " ";
			String uuid = (String) attributes.get("uuid");
			String name = intent.getSlots().get("Name").getValue();
			if (named == null) {
				named = new HashMap<String, String>();
				attributes.put("renderers.named", named);
			}
			if (name != null)
				named.put(name, uuid);
			
			renderers = (Map<String, String>) attributes.get("renderers");
		}

		int i = 0;
		for (String key : renderers.keySet()) {
			if (named == null || !named.containsValue(key)) {
				attributes.put("uuid", key);
				i++;
			}
		}
		
		if (i == 0) {
			// all the devices have been named
			if (dialogState.equals(DialogState.STARTED)) {
				speechText = String.format("Found %d unnamed devices. Would you like to rename?", i);
			} else {
				speechText = Utterance.get("naming.success");
			}
		} else {
			if (!rename && dialogState.equals(DialogState.STARTED)) {
				speechText = String.format("Found %d unnamed devices. ", i);
			} else {
				speechText = "";
			}
			// Prompt to device name
			speechText += Utterance.get("prompt.device.name") + " " + renderers.get(attributes.get("uuid"));

			responseBuilder.addElicitSlotDirective("Name", intent);
		}

		return speechText;
	}

	private String playSong(ResponseBuilder responseBuilder, Intent intent, Map<String, Object> attributes) {
		String speechText = null;
		String criteria = String.format("dc:title contains \"%s\"", intent.getSlots().get("Query").getValue());
		RendererConfiguration renderer = RendererConfiguration.getDefaultConf();
		RootFolder root = renderer.getRootFolder();
		List<DLNAResource> resources = root.getDLNAResources("0", true, 0, 1, renderer, criteria);

		if (resources.isEmpty()) {
			speechText = Utterance.get("song.not.found");
		} else {
			DLNAResource resource = resources.get(0);
			if (resource.isFolder()) {
				resource = resource.getChildren().get(0);
			}
			String title = "Playing ";
			speechText = title + resource.getDisplayName();
//			responseBuilder.addElicitSlotDirective("DeviceId", intent);
//			responseBuilder.addDelegateDirective(updatedIntent);
			
			// Find target renderer
			String device = intent.getSlots().get("Device").getValue();
			String uid = null;
			if (device != null && attributes.containsKey("renderers.named")) {
				uid = (String) ((Map)attributes.get("renderers.named")).get(device);
				attributes.put("renderer.preferred", uid);
			}
			if (attributes.get("renderer.preferred") != null) {
				// Use the renderer in session
				uid = (String) attributes.get("renderer.preferred");
				
				boolean found = false;
				List<RendererConfiguration> players = RendererConfiguration.getConnectedControlPlayers();
				for (RendererConfiguration r : players) {
					if (uid != null && uid.equals(r.getUUID())) {
						found = true;
						renderer = r;
						break;
					}
				}
				
				if (found) {
					renderer.getPlayer().pressPlay(resource.getURL(""), null);

					Map<String, String> names = (Map<String, String>) attributes.get("renderers");
					device = names.get(uid);
					
					// Use names by user
					names = (Map<String, String>) attributes.get("renderers.named");
					if (names != null) {
						for (String key : names.keySet()) {
							String val = names.get(key);
							if (uid.equals(val)) {
								device = key;
								break;
							}
						}
					}

					speechText += " on " + device;
				} else {
					speechText = Utterance.get("pref.device.not.found");
				}
			} else {
				// If renderers found, name them
				speechText = Utterance.get("pref.device.not.set") + ". " + speechText;
				
				// Else, use Alexa
				responseBuilder
					.withShouldEndSession(true)
					.addAudioPlayerPlayDirective(PlayBehavior.REPLACE_ALL, 0L, null, resource.getId(), resource.getURL(""));
			}
			
			responseBuilder
				.withSimpleCard(title, speechText);
		}
		return speechText;
	}

	private String listDevices(Map<String, Object> attributes) {
		String speechText = null;
		List<RendererConfiguration> players = RendererConfiguration.getConnectedControlPlayers();
		if (players.isEmpty()) {
			speechText = Utterance.get("device.not.found");
		} else {
			speechText = "Found following devices: ";
//			speechText += "    Here is a number <w role='amazon:VBD'>read</w> ";
//			speechText += "    as a cardinal number: ";
//			speechText += "    <say-as interpret-as='cardinal'>12345</say-as>. ";
//			speechText += "    <say-as interpret-as='ordinal'>5</say-as>. ";
//			speechText += "    Here is a word spelled out: ";
//			speechText += "    <say-as interpret-as='spell-out'>hello</say-as>. ";

			int i = 1;
			Map<String, String> named = (Map<String, String>) attributes.get("renderers.named");
			for (RendererConfiguration r : players) {
				speechText += "    <say-as interpret-as='ordinal'>" + i + "</say-as>. ";
				speechText += r.getRendererName();
				if (named != null && named.containsKey(r.getUUID())) {
					speechText += " aka " + named.get(r.getUUID());
				}
				i++;
			}
		}
		return speechText;
	}
}