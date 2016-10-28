package io.pivotal.workshop.broker.mysql.repository;

import io.pivotal.workshop.broker.mysql.model.ServiceInstanceBinding;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by df on 16-10-24.
 */
public interface MySQLServiceInstanceBindingRepository extends JpaRepository<ServiceInstanceBinding, String> {
}
