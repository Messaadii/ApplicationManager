package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/virtualMachine")
public class VirtualMachineController {

    VirtualMachineService virtualMachineService;

    @Autowired
    public VirtualMachineController(VirtualMachineService virtualMachineService) {
        this.virtualMachineService = virtualMachineService;
    }

    @GetMapping("/get/{name}")
    public VirtualMachine getVirtualMachine(@PathVariable String name) {
        return virtualMachineService.getVirtualMachine(name);
    }

    @PostMapping("/save")
    public VirtualMachine save(@RequestBody VirtualMachine virtualMachine) {
        return virtualMachineService.create(virtualMachine);
    }
}
