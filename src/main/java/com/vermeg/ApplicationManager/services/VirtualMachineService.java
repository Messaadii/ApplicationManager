package com.vermeg.ApplicationManager.services;

import com.jcraft.jsch.JSchException;
import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.entities.VirtualMachine;

import java.io.IOException;
import java.util.List;

public interface VirtualMachineService {
    VirtualMachine create(VirtualMachine virtualMachine);
    VirtualMachine getVirtualMachine(String name);
    List<VirtualMachine> getAllVirtualMachines();
    void deleteVirtualMachine(String name);
    List<String> listActiveJavaProcesses(String name );
    List<VirtualMachine> findAll();
    String killProcess(String name, String pid);
    List<Command> getCommandsByVirtualMachineName(String name);


    VirtualMachine findByName(String name);

    void executeCommand(String name);

    void executeCommand(String name, Command command) throws JSchException, IOException, InterruptedException;

    Command addcommand(Command command);
}
