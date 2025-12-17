package com.helalferrari.kitnetsapi.mapper;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.LandlordDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.PhotoDTO;
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Landlord;
import com.helalferrari.kitnetsapi.model.Photo;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class KitnetMapper {

    // Método principal: Converte a Entidade Kitnet para o DTO de Resposta
    public KitnetResponseDTO toResponseDTO(Kitnet kitnet) {
        if (kitnet == null) {
            return null;
        }

        KitnetResponseDTO dto = new KitnetResponseDTO();

        // Mapeamento dos campos antigos/básicos (1:1)
        dto.setId(kitnet.getId());
        dto.setNome(kitnet.getNome());
        dto.setValor(kitnet.getValor());
        dto.setVagas(kitnet.getVagas());
        dto.setTaxa(kitnet.getTaxa());
        dto.setDescricao(kitnet.getDescricao());

        // Mapeamento do Landlord (Relacionamento ManyToOne)
        if (kitnet.getLandlord() != null) {
            dto.setLandlord(toLandlordDTO(kitnet.getLandlord()));
        }

        // Mapeamento das Fotos (Relacionamento OneToMany: Lista)
        if (kitnet.getPhotos() != null) {
            dto.setPhotos(kitnet.getPhotos().stream()
                    .map(this::toPhotoDTO) // Mapeia cada Photo individualmente
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    // Método Auxiliar: Converte Landlord (Entidade) para LandlordDTO
    private LandlordDTO toLandlordDTO(Landlord landlord) {
        LandlordDTO dto = new LandlordDTO();
        dto.setId(landlord.getId());
        dto.setName(landlord.getName());
        dto.setEmail(landlord.getEmail());
        // Não mapeamos o campo 'phone' intencionalmente (se for um campo privado)
        return dto;
    }

    // Método Auxiliar: Converte Photo (Entidade) para PhotoDTO
    private PhotoDTO toPhotoDTO(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());
        return dto;
    }

    public Kitnet toEntity(KitnetRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Kitnet kitnet = new Kitnet();
        kitnet.setNome(dto.getNome());
        kitnet.setValor(dto.getValor());
        kitnet.setVagas(dto.getVagas());
        kitnet.setTaxa(dto.getTaxa());
        kitnet.setDescricao(dto.getDescricao());

        // Mapear Landlord
        if (dto.getLandlord() != null) {
            Landlord landlord = new Landlord();
            landlord.setId(dto.getLandlord().getId()); // Se vier ID, pode ser atualização/vinculo
            landlord.setName(dto.getLandlord().getName());
            landlord.setEmail(dto.getLandlord().getEmail());
            // landlord.setPhone(...) se tiver no DTO

            kitnet.setLandlord(landlord);
        }

        // Mapear Fotos
        if (dto.getPhotos() != null) {
            for (PhotoDTO photoDto : dto.getPhotos()) {
                Photo photo = new Photo();
                photo.setUrl(photoDto.getUrl());
                // Importante: Usar o método helper da Entidade para vincular corretamente
                kitnet.addPhoto(photo);
            }
        }

        return kitnet;
    }
}