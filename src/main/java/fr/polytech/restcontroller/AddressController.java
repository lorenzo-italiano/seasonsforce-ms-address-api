package fr.polytech.restcontroller;


import fr.polytech.model.Address;
import fr.polytech.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    /**
     * Constructor for AddressController
     * @param addressService AddressService to inject
     */
    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Get all addresses from the database
     * @return List of all addresses
     */
    @GetMapping("/")
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    /**
     * Just a test endpoint
     * @return Test string
     * TODO: Remove this endpoint
     */
    @GetMapping("/test/1")
    @PreAuthorize("hasRole('client_admin')")
    public String test() {
        return "test for admin";
    }

    /**
     * Get the address with the specified ID
     * @param id ID of the address to get
     * @return Address with the specified ID
     */
    @GetMapping("/{id}")
    public Address getAddressById(@PathVariable UUID id) {
        return addressService.getAddressById(id);
    }

    /**
     * Create a new address if it doesn't already exist in the database.
     * If a similar address already exists, return the ID of that address.
     * @param address Address to create
     * @return ID of the created address
     */
    @PostMapping("/")
    public ResponseEntity<UUID> createAddress(@RequestBody Address address) {
        UUID existingId = addressService.findSimilarAddress(address);
        if (existingId != null) {
//            return ResponseEntity.ok(existingId);
            return ResponseEntity.status(HttpStatus.CREATED).body(existingId);
//            return ResponseEntity.body(existingId);
        }

//        return new addressService.createAddress(address).getId();
//        return ResponseEntity.ok();
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(address).getId());
    }


    /**
     * Addresses are never updated, because they can be used by multiple users/companies.
     * If an address is updated, we check if a similar address already exists. If it does, we return the ID of that address.
     * Otherwise, we create a new address and return its ID.
     * @param address Address to update
     * @return ID of the updated address
     */
    @PutMapping("/")
    public UUID updateAddress(@RequestBody Address address) {
        // Check if a similar address already exists
        UUID existingAddress = addressService.findSimilarAddress(address);

        // If it doesn't, we create a new address
        if (existingAddress == null) {
            return addressService.createAddress(address).getId();
        }

        // Otherwise, we return the ID of the existing address
        return existingAddress;
    }

    /**
     * Remove an address from the database.
     * @param id ID of the address to remove
     * TODO: Should we remove this possibility (even for admins) ?
     */
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
    }
}
