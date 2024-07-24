package com.vermeg.ApplicationManager.controllers;

import com.jcraft.jsch.JSchException;
import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.entities.VirtualMachine;
import com.vermeg.ApplicationManager.repositories.CommandRepository;
import com.vermeg.ApplicationManager.services.VirtualMachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/virtual-machine")
@CrossOrigin()


public class VirtualMachineController {

    private VirtualMachineService virtualMachineService;
    private CommandRepository commandRepository;
    @Autowired
    public VirtualMachineController(VirtualMachineService virtualMachineService, CommandRepository commandRepository) {
        this.virtualMachineService = virtualMachineService;
        this.commandRepository = commandRepository;
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

    @PostMapping(value = "/execute/{id}")
    public ResponseEntity<String> executeCommand(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            Command command = commandRepository.findById(id).get();
            virtualMachineService.executeCommand(command);
            return ResponseEntity.ok("Command executed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing command: " + e.getMessage());
        }
    }
      @PostMapping("/thread-dump/{name}/{pid}")
           public ResponseEntity<?> executeThreadDump(@PathVariable String name, @PathVariable String pid) {
               try {
                   String threadDump = virtualMachineService.executeThreadDump(name, pid);
                   return ResponseEntity.ok(threadDump);
               } catch (Exception e) {
                   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error executing thread dump: " + e.getMessage());
               }
           }
       }
     
     
     