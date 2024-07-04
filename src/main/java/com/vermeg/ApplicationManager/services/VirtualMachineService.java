package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.VirtualMachine;

import java.util.List;

public interface VirtualMachineService {
    VirtualMachine create(VirtualMachine virtualMachine);
    VirtualMachine getVirtualMachine(String name);

    List<VirtualMachine> getAllVirtualMachines();

    void deleteVirtualMachine(String name);

    List<String> listActiveJavaProcesses(String name );

    List<VirtualMachine> findAll();
}
