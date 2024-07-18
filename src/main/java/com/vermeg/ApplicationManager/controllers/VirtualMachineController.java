package com.vermeg.ApplicationManager.controllers;

import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/find-all")
    public List<VirtualMachine> findAll() {
        return virtualMachineService.findAll();
    }

    @PostMapping("/killprocess/{name}/{pid}")
    public ResponseEntity<Map<String, String>> killProcessVM(@PathVariable("name") String name, @PathVariable("pid") String pid) {
        Map<String, String> response = new HashMap<>();
        try {
            String result = virtualMachineService.killProcess(name, pid);
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to kill process for VM " + name + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/commands/{name}")
    public ResponseEntity<List<Command>> getCommandsByVirtualMachineName(@PathVariable String name) {
        List<Command> commands = virtualMachineService.getCommandsByVirtualMachineName(name);
        if (commands != null) {
            return new ResponseEntity<>(commands, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/Findbyname/{name}")
    public VirtualMachine findByName(@PathVariable String name) {
        return virtualMachineService.findByName(name);
    }

    @PostMapping("/execute/{name}")
    public ResponseEntity<String> executeCommand(@PathVariable String name, @RequestBody Command command) {
        try {
            virtualMachineService.executeCommand(name, command);
            return new ResponseEntity<>("Command executed successfully.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to execute command: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addcommand")
    public Command addcommand(@RequestBody Command command) {
        return virtualMachineService.addcommand(command);
    }

}
