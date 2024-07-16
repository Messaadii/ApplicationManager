package com.vermeg.ApplicationManager.repositories;

import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, String>{
    VirtualMachine getVirtualMachineByName(String name);

    VirtualMachine findByName(String name);
}
