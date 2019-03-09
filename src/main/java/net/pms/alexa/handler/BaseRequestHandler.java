package net.pms.alexa.handler;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.audioplayer.PlayBehavior;
import com.amazon.ask.request.Predicates;

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
		return input.getResponseBuilder()
				.addAudioPlayerPlayDirective(PlayBehavior.REPLACE_ALL, 0L, null, "14", "https://043589c9.ngrok.io/get/755/XPWB.mp3_transcoded_to.mp3")
				.withSpeech(speechText)
				.withShouldEndSession(true)
				.withSimpleCard(speechText, speechText)
//				.withReprompt(speechText) // "AMAZON.HelpIntent" "AMAZON.FallbackIntent"
				.build();
	}
}