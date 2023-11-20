package fr.polytech.restcontroller;


import fr.polytech.annotation.IsAdmin;
import fr.polytech.model.Address;
import fr.polytech.service.AddressService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    /**
     * Constructor for AddressController
     *
     * @param addressService AddressService to inject
     */
    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Get all addresses from the database
     *
     * @return List of all addresses
     */
    @GetMapping("/")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Address>> getAllAddresses() {
        try {
            List<Address> addresses = addressService.getAllAddresses();
            logger.info("Got all addresses");
            return new ResponseEntity<>(addresses, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error while getting all addresses", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the address with the specified ID
     *
     * @param id ID of the address to get
     * @return Address with the specified ID
     */
    @GetMapping("/{id}")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> getAddressById(@PathVariable UUID id) {
        try {
            Address address = addressService.getAddressById(id);
            logger.info("Got address with id " + id);
            return new ResponseEntity<>(address, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            logger.error("Error while getting address with id " + id + " " + e.getStatusCode(), e);
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Exception e) {
            logger.error("Error while getting address with id " + id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new address if it doesn't already exist in the database.
     * If a similar address already exists, return the ID of that address.
     *
     * @param address Address to create
     * @return ID of the created address
     */
    @PostMapping("/")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> createAddress(@RequestBody Address address) {
        UUID existingId = addressService.findSimilarAddress(address);
        if (existingId != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(existingId);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(address).getId());
    }


    /**
     * Addresses are never updated, because they can be used by multiple users/companies.
     * If an address is updated, we check if a similar address already exists. If it does, we return the ID of that address.
     * Otherwise, we create a new address and return its ID.
     *
     * @param address Address to update
     * @return ID of the updated address
     */
    @PutMapping("/")
    @Produces(MediaType.APPLICATION_JSON_VALUE)
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @IsAdmin
    public ResponseEntity<UUID> updateAddress(@RequestBody Address address) {
        // Check if a similar address already exists
        UUID existingAddress = addressService.findSimilarAddress(address);

        // If it doesn't, we create a new address
        if (existingAddress == null) {
            UUID addressId = addressService.createAddress(address).getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(addressId);
        }

        // Otherwise, we return the ID of the existing address
        return new ResponseEntity<>(existingAddress, HttpStatus.OK);
    }

    /**
     * Remove an address from the database.
     *
     * @param id ID of the address to remove
     */
    @IsAdmin
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable UUID id) {
        addressService.deleteAddress(id);
    }
}
