package fr.polytech.service;

import fr.polytech.model.Address;
import fr.polytech.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

@Service
public class AddressService {

    /**
     * AddressRepository to inject
     */
    @Autowired
    private AddressRepository addressRepository;

    /**
     * Delete the address with the specified ID
     *
     * @param id ID of the address to delete
     */
    public void deleteAddress(UUID id) {
        addressRepository.deleteById(id);
    }

    /**
     * Get all addresses from the database
     *
     * @return List of all addresses
     */
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    /**
     * Get the address with the specified ID
     *
     * @param id ID of the address to get
     * @return Address with the specified ID
     * @throws HttpClientErrorException If address is not found
     */
    public Address getAddressById(UUID id) throws HttpClientErrorException {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return address;
    }

    /**
     * Create a new address
     *
     * @param address Address to create
     * @return Created address
     */
    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }

    /**
     * Find an address with the same street, city, zip code, number and country
     *
     * @param address Address to find
     * @return ID of the address if found, null otherwise
     */
    public UUID findSimilarAddress(Address address) {
        return addressRepository.findSimilarAddress(address.getStreet(), address.getCity(), address.getZipCode(), address.getNumber(), address.getCountry());
    }

}
