package net.pms.alexa.handler;

import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.request.Predicates;

import net.pms.alexa.Utterance;

public class LaunchRequestHandler implements RequestHandler {

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(Predicates.requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = Utterance.get(Utterance.START);
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard("Ask UMS", speechText)
                .withReprompt(speechText)
                .build();
    }

}