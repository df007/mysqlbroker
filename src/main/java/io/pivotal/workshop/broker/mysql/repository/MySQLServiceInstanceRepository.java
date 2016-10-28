package io.pivotal.workshop.broker.mysql.repository;

import io.pivotal.workshop.broker.mysql.model.ServiceInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by df on 16-10-24.
 */
public interface MySQLServiceInstanceRepository  extends JpaRepository<ServiceInstance,String>{


}
