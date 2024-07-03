package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/virtual-machine")
public class VirtualMachineController {

    private VirtualMachineService virtualMachineService;

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

    @GetMapping("/getAll")
    public List<VirtualMachine> getAllVirtualMachines() {
        return virtualMachineService.getAllVirtualMachines();
    }

    // delete virtual machine
    @DeleteMapping("/delete/{name}")
    public void deleteVirtualMachine(@PathVariable String name) {
        virtualMachineService.deleteVirtualMachine(name);
    }

    // update virtual machine
    @PutMapping("/update")
    public VirtualMachine updateVirtualMachine(@RequestBody VirtualMachine virtualMachine) {
        return virtualMachineService.create(virtualMachine);
    }

    @GetMapping("/list-active-java-processes/{name}")
    public List<String> listActiveJavaProcesses(@PathVariable String name) {
        return virtualMachineService.listActiveJavaProcesses(name);
    }
}
