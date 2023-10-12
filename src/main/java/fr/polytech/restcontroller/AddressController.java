package fr.polytech.restcontroller;


import fr.polytech.model.Address;
import fr.polytech.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/")
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @GetMapping("/test/1")
    @PreAuthorize("hasRole('client_admin')")
    public String test() {
        return "test for admin";
    }

    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable UUID id) {
        return addressService.getAddressById(id);
    }

    /*
    * Créez une nouvelle adresse si elle n'existe pas déjà dans la base de données.
    * Si une adresse similaire existe déjà, renvoyez l'ID de cette adresse.
     */
    @PostMapping("/")
    public UUID createAddress(@RequestBody Address address) {
        UUID existingId = addressService.findSimilarAddress(address);
        if (existingId != null) {
            return existingId;
        }

        return addressService.createAddress(address).getId();
    }

    /*
    * Les adresses ne sont jamais mises à jour, car elles peuvent être utilisées par plusieurs utilisateurs / entreprises.
    * Si une addresse est mise à jour, on vérifie si une adresse similaire existe déjà. Si c'est le cas, on renvoie l'ID de cette adresse.
    * Sinon, on crée une nouvelle adresse et on renvoie son ID.
     */
    @PutMapping("/")
    public UUID updateAddress(@RequestBody Address address) {
        // Vérifiez si l'adresse existe déjà
        UUID existingAddress = addressService.findSimilarAddress(address);

        // Si l'adresse n'existe pas, créez-la
        if (existingAddress == null) {
            return addressService.createAddress(address).getId();
        }

        // Sinon on renvoie l'ID de l'adresse existante
        return existingAddress;
    }

    /*
    * Supprimez l'adresse avec l'ID spécifié.
    * TODO: Doit-on enlever cette possibilité (même pour admins) ?
     */
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
    }
}
