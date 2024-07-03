package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long>{
    VirtualMachine getVirtualMachineByName(String name);

}
