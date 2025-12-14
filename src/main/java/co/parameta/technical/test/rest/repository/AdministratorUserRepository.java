package co.parameta.technical.test.rest.repository;

import co.parameta.technical.test.commons.entity.AdministratorUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link AdministratorUserEntity} persistence.
 * <p>
 * Provides CRUD operations and custom query methods to retrieve administrator
 * users by unique identifiers such as email and code.
 * </p>
 */
@Repository("restAdministrator")
public interface AdministratorUserRepository extends JpaRepository<AdministratorUserEntity, String> {

    /**
     * Finds an administrator user by email address.
     *
     * @param email the email associated with the administrator user
     * @return an {@link Optional} containing the administrator user if found,
     *         or {@link Optional#empty()} if no user exists with the given email
     */
    Optional<AdministratorUserEntity> findByEmail(String email);

    /**
     * Finds an administrator user by unique code.
     *
     * @param code the unique code assigned to the administrator user
     * @return an {@link Optional} containing the administrator user if found,
     *         or {@link Optional#empty()} if no user exists with the given code
     */
    Optional<AdministratorUserEntity> findByCode(String code);

}
