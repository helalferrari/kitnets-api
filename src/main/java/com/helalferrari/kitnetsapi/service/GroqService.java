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
                
                Rules:
                1. Match amenities and bathroom types to their Enums strictly.
                2. If a field is not mentioned or not applicable, set it to null. DO NOT use empty strings ("") for Enums or Numbers.
                3. For 'bathroomType', use only 'PRIVATIVO' or 'COMPARTILHADO' or null.
                
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
