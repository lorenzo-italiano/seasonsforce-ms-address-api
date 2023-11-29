package fr.polytech.repository;

import fr.polytech.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Query("SELECT a.id FROM Address a WHERE a.street = :street AND a.city = :city AND a.zipCode = :zipCode AND a.country = :country AND a.number = :number")
    UUID findSimilarAddress(@Param("street") String street,
                               @Param("city") String city,
                               @Param("zipCode") String zipCode,
                               @Param("number") String number,
                               @Param("country") String country);

}
