package com.vermeg.ApplicationManager.services;

import com.jcraft.jsch.JSchException;
import com.vermeg.ApplicationManager.entities.Command;
import com.vermeg.ApplicationManager.entities.VirtualMachine;

import java.io.IOException;
import java.util.List;

public interface VirtualMachineService {
    static void executeCommand(String name, String pid) {
    }

    VirtualMachine create(VirtualMachine virtualMachine);
    VirtualMachine getVirtualMachine(String name);
    List<VirtualMachine> getAllVirtualMachines();
    void deleteVirtualMachine(String name);
    List<String> listActiveJavaProcesses(String name );
    List<VirtualMachine> findAll();
    String killProcess(String name, String pid);
    List<Command> getCommandsByVirtualMachineName(String name);
    VirtualMachine findByName(String name);
    void executeCommand(Command command) throws JSchException, IOException;

    String executeThreadDump(String name, String pid) throws IOException, InterruptedException, JSchException;
}
