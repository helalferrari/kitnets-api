package com.helalferrari.kitnetsapi.mapper;

import com.helalferrari.kitnetsapi.dto.kitnet.KitnetRequestDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.KitnetResponseDTO;
import com.helalferrari.kitnetsapi.dto.kitnet.PhotoDTO;
import com.helalferrari.kitnetsapi.dto.auth.OwnerDTO; // Importante manter esse import!
import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.model.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class KitnetMapper {

    public KitnetResponseDTO toResponseDTO(Kitnet kitnet) {
        if (kitnet == null) {
            return null;
        }

        KitnetResponseDTO dto = new KitnetResponseDTO();

        dto.setId(kitnet.getId());
        dto.setNome(kitnet.getNome());
        dto.setValor(kitnet.getValor());
        dto.setVagas(kitnet.getVagas());
        dto.setTaxa(kitnet.getTaxa());
        dto.setDescricao(kitnet.getDescricao());

        dto.setCep(kitnet.getCep());
        dto.setLogradouro(kitnet.getLogradouro());
        dto.setComplement(kitnet.getComplement());
        dto.setNumber(kitnet.getNumber());
        dto.setNeighborhood(kitnet.getNeighborhood());
        dto.setCity(kitnet.getCity());
        dto.setState(kitnet.getState());
        dto.setIbge(kitnet.getIbge());
        dto.setLongitude(kitnet.getLongitude());
        dto.setLatitude(kitnet.getLatitude());
        dto.setNonumber(kitnet.getNonumber());

        if (kitnet.getUser() != null) {
            dto.setLandlord(toOwnerDTO(kitnet.getUser()));
        }

        if (kitnet.getPhotos() != null) {
            dto.setPhotos(kitnet.getPhotos().stream()
                    .map(this::toPhotoDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    private OwnerDTO toOwnerDTO(User user) {
        return new OwnerDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone()
        );
    }

    private PhotoDTO toPhotoDTO(Photo photo) {
        PhotoDTO dto = new PhotoDTO();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());

        String originalUrl = photo.getUrl();
        if (originalUrl != null && originalUrl.contains("/")) {
            int lastSlashIndex = originalUrl.lastIndexOf("/");
            String path = originalUrl.substring(0, lastSlashIndex);
            String filename = originalUrl.substring(lastSlashIndex + 1);
            
            // Garante que o path não termine com slash para evitar double slash se concatenado
            if (path.isEmpty()) {
                dto.setThumbnailUrl("/thumbnails/" + filename);
            } else {
                dto.setThumbnailUrl(path + "/thumbnails/" + filename);
            }
        } else {
            dto.setThumbnailUrl(originalUrl); // Fallback
        }
        
        return dto;
    }

    // --- A CORREÇÃO ESTÁ AQUI EMBAIXO ---
    public Kitnet toEntity(KitnetRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Kitnet kitnet = new Kitnet();
        // Mudamos de getNome() para nome(), de getValor() para valor(), etc.
        kitnet.setNome(dto.nome());
        kitnet.setValor(dto.valor());
        kitnet.setVagas(dto.vagas());
        kitnet.setTaxa(dto.taxa());
        kitnet.setDescricao(dto.descricao());

        kitnet.setCep(dto.cep());
        kitnet.setLogradouro(dto.logradouro());
        kitnet.setComplement(dto.complement());
        kitnet.setNumber(dto.number());
        kitnet.setNeighborhood(dto.neighborhood());
        kitnet.setCity(dto.city());
        kitnet.setState(dto.state());
        kitnet.setIbge(dto.ibge());
        kitnet.setLongitude(dto.longitude());
        kitnet.setLatitude(dto.latitude());
        kitnet.setNonumber(dto.nonumber());

        return kitnet;
    }
}