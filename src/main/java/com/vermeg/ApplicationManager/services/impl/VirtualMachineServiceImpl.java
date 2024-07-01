package com.vermeg.ApplicationManager.services.impl;

import com.vermeg.ApplicationManager.repositories.VirtualMachineRepository;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {

    VirtualMachineRepository virtualMachineRepository;

    @Autowired
    public VirtualMachineServiceImpl(VirtualMachineRepository virtualMachineRepository) {
        this.virtualMachineRepository = virtualMachineRepository;
    }

    @Override
    public VirtualMachine create(VirtualMachine virtualMachine) {
        return virtualMachineRepository.save(virtualMachine);
    }
    @Override
    public VirtualMachine getVirtualMachine( String name) {
        return virtualMachineRepository.getVirtualMachineByName(name);
    }
}
