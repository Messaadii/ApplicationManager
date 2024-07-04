package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/virtualMachine")
@CrossOrigin()
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
    @GetMapping("/list-active-java-processes/{name}")
    public List<String> listActiveJavaProcesses(@PathVariable String name) {
        return virtualMachineService.listActiveJavaProcesses(name);
    }
    @GetMapping("/find-all")
    public List<VirtualMachine> findAll(){
        return virtualMachineService.findAll();
    }

}
