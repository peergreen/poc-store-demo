package com.peergreen.store.demo;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.Validate;

import com.peergreen.store.controller.IGroupController;
import com.peergreen.store.controller.IPetalController;
import com.peergreen.store.controller.IStoreManagment;
import com.peergreen.store.controller.IUserController;
import com.peergreen.store.db.client.ejb.entity.Capability;
import com.peergreen.store.db.client.ejb.entity.Category;
import com.peergreen.store.db.client.ejb.entity.Petal;
import com.peergreen.store.db.client.ejb.entity.Requirement;
import com.peergreen.store.db.client.ejb.entity.Vendor;
import com.peergreen.store.db.client.ejb.session.api.ISessionRequirement;
import com.peergreen.store.db.client.enumeration.Origin;
import com.peergreen.store.db.client.exception.EntityAlreadyExistsException;
import com.peergreen.store.db.client.exception.NoEntityFoundException;

@Component
@Instantiate
@Provides
public class App {

    @Requires
    private IStoreManagment storeManagement;
    @Requires
    private IPetalController petalController;
    @Requires
    private IGroupController groupController;
    @Requires
    private IUserController userController;

    @Requires
    ISessionRequirement requirementSession;
    
    @Validate
    public void main() throws EntityAlreadyExistsException {
        System.out.println("Running");

        userController.addUser("Administrator", "pwd", "tut@hotmail.com");
        
        groupController.addGroup("Administrator");
        
        storeManagement.addLink("https://store.peergreen.com/community", "Store Community");

        Category category = storeManagement.addCategory("Bundle");

        // create a capability
        // add it to the capabilities list
        Map<String, String> properties = new HashMap<>();
        properties.put("toto", "a");
        Capability cap = petalController.createCapability("tut", "1", "test", properties);
        Set<Capability> capabilities = new HashSet<>();
        capabilities.add(cap);
        
        // create a requirement
        // add it to the requirements list
        Set<Requirement> requirements = new HashSet<>();
        
        Vendor vendor = petalController.createVendor("Peergreen",
                "Peergreen is a software company started by the core team" +
                "who created JOnAS, the Open Application Server used for" +
                "critical production processes");
        
        File petalBinary = new File("C:\\Users\\user2\\.m2\\repository\\com\\peergreen\\" +
        		"store\\controller\\1.0-SNAPSHOT\\controller-1.0-SNAPSHOT.jar");
        
        try {
            petalController.addPetal(vendor, "Store", "0.1.0-beta", "Apps Store for Peergreen Platform",
                    category, requirements, capabilities, Origin.LOCAL, petalBinary);
        } catch (NoEntityFoundException e) {
            // TODO
            e.printStackTrace();
        }
        
        Requirement req = null;
        try {
            req = petalController.createRequirement("test", "test", "(&(capabilityName=tut)(version=1))");
        } catch (EntityAlreadyExistsException e) {
            e.printStackTrace();
        }
        
        Collection<Capability> listResolvedCapabilities = requirementSession.findCapabilities("test", req);
        int i = 1;
        for (Capability c : listResolvedCapabilities) {
            System.out.println(i + "\t" + c.getCapabilityName());
            i++;
        }
        
        Collection<Petal> petalsList = storeManagement.collectPetalsFromLocal();
        System.out.println("There are "+petalsList.size()+" petal(s) in local repository");
    }
}
