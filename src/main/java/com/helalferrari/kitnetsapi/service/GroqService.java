package com.helalferrari.kitnetsapi.service;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetSearchCriteriaDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GroqService {

    private final ChatModel chatModel;

    public GroqService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public KitnetSearchCriteriaDTO extractSearchCriteria(String userQuery) {
        BeanOutputConverter<KitnetSearchCriteriaDTO> outputConverter = new BeanOutputConverter<>(KitnetSearchCriteriaDTO.class);
        
        String promptText = """
                You are a search assistant for a Kitnet rental platform.
                Extract search criteria from the user's query into a strict JSON format.
                Match the amenities and bathroom types to the enums if possible.
                
                User Query: {query}
                
                {format}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(promptText);
        Prompt prompt = promptTemplate.create(Map.of(
                "query", userQuery,
                "format", outputConverter.getFormat()
        ));

        String response = chatModel.call(prompt).getResult().getOutput().getContent();
        return outputConverter.convert(response);
    }
}
