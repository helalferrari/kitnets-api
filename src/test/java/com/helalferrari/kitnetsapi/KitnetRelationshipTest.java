package com.helalferrari.kitnetsapi;

import com.helalferrari.kitnetsapi.model.Kitnet;
import com.helalferrari.kitnetsapi.model.Landlord;
import com.helalferrari.kitnetsapi.model.Photo;
import com.helalferrari.kitnetsapi.repository.KitnetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class KitnetRelationshipTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private KitnetRepository kitnetRepository;

    @Test
    void shouldLinkKitnetToLandlord() {
        // CENÁRIO: Criando um Landlord (Proprietário)
        Landlord landlord = new Landlord();
        landlord.setName("John Doe");
        landlord.setEmail("john@email.com");
        landlord.setPhone("48 9999-0000");

        // Persistimos o Landlord primeiro
        Landlord savedLandlord = entityManager.persistFlushFind(landlord);

        // Criamos uma Kitnet
        Kitnet kitnet = new Kitnet();
        kitnet.setNome("John's Studio");
        kitnet.setValor(1200.00);
        kitnet.setVagas(1);
        kitnet.setTaxa(50.00);
        kitnet.setDescricao("Near University");

        // AÇÃO: Vinculamos (setLandlord em vez de setProprietario)
        kitnet.setLandlord(savedLandlord);

        Kitnet savedKitnet = kitnetRepository.save(kitnet);

        // VERIFICAÇÃO
        assertThat(savedKitnet.getLandlord()).isNotNull();
        assertThat(savedKitnet.getLandlord().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldAddPhotosToKitnet() {
        // CENÁRIO
        Kitnet kitnet = new Kitnet();
        kitnet.setNome("Studio with Photos");
        kitnet.setValor(1500.00);
        kitnet = entityManager.persistFlushFind(kitnet);

        // AÇÃO: Adicionar Photos
        // https://picsum.photos/id/237/200/300
        Photo photo1 = new Photo("https://fastly.picsum.photos/id/866/500/300.jpg?hmac=gTBX2xIXKy_WSASp2ITBfmK7WFeBZyiuIumiEUmowcw");
        Photo photo2 = new Photo("https://fastly.picsum.photos/id/900/500/300.jpg?hmac=6235Iv2c4OHA7OL5mVLCuvFOPzGUafzWSKp-E_9wKzw");

        // Método addPhoto
        kitnet.addPhoto(photo1);
        kitnet.addPhoto(photo2);

        Kitnet updatedKitnet = kitnetRepository.save(kitnet);

        entityManager.flush();
        entityManager.refresh(updatedKitnet);

        // VERIFICAÇÃO
        assertThat(updatedKitnet.getPhotos()).hasSize(2);
        assertThat(updatedKitnet.getPhotos().get(0).getUrl()).contains("https://fastly.picsum.photos/id/866/500/300.jpg?hmac=gTBX2xIXKy_WSASp2ITBfmK7WFeBZyiuIumiEUmowcw");
    }
}
