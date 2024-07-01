package com.vermeg.ApplicationManager.services;

import com.vermeg.ApplicationManager.entities.VirtualMachine;

public interface VirtualMachineService {
    VirtualMachine create(VirtualMachine virtualMachine);

    VirtualMachine getVirtualMachine(String name);
}
