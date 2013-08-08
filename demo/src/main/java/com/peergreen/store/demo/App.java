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
import com.peergreen.store.db.client.ejb.session.api.ISessionVendor;
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
    private ISessionVendor vendorSession;

    @Requires
    private ISessionRequirement requirementSession;


    @Validate
    public void main() throws EntityAlreadyExistsException, NoEntityFoundException {
        System.out.println("Running");

        //Add 4 users
        userController.addUser("Administrator", "admin", "admin@peergren.com");
        userController.addUser("Toto", "pwdtoto","toto@peergreen.com");
        userController.addUser("Paul", "pwdpaul","paul@peergreen.com");
        userController.addUser("Gui", "pwdgui","gui@peergreen.com");

        //Create admin's group and others two groups 
        groupController.createGroup("Administrator");
        groupController.createGroup("GroupDev1");
        groupController.createGroup("GroupDev2");

        //Add user to groups 
        groupController.addUser("Administrator", "Administrator");
        groupController.addUser("GroupDev1", "Toto");
        groupController.addUser("GroupDev2", "Paul");
        groupController.addUser("GroupDev2", "Gui");
        groupController.addUser("GroupDev2", "Toto");

        System.out.println("The user Toto belongs to 2 groups : " + (userController.collectGroups("Toto").size()==2));
        System.out.println("GroupDev2 has 3 users : " + (groupController.collectUsers("GroupDev2").size()==3));

        //We have 3 petals with his metadata. After reading this, we are : 

        Category category = storeManagement.createCategory("Bundle");
        Category category1 = storeManagement.createCategory("Not Bundle");
        Vendor vendor =  petalController.createVendor("Peergreen",
                "Peergreen is a software company started by the core team" +
                        "who created JOnAS, the Open Application Server used for" +
                "critical production processes");    

        File petalBinary = new File("C:\\Users\\user1\\.m2\\repository\\com\\peergreen\\" +
                "store\\controller\\1.0-SNAPSHOT\\controller-1.0-SNAPSHOT.jar");


        Map<String, String> properties = new HashMap<>();
        properties.put("bundle", "true");
        Capability capJX = petalController.createCapability("Jax-RS", "1.0", "Rest", properties);

        Map<String, String> properties1 = new HashMap<>();
        properties1.put("bundle", "false");
        Capability capJPA = petalController.createCapability("JPA", "2.4", "db", properties1);

        Map<String, String> properties1Bis = new HashMap<>();
        properties1Bis.put("bundle", "true");
        Capability capJPA1 = petalController.createCapability("JPABundle", "2.4", "db", properties1Bis);

        Map<String,String> properties2 = new HashMap<>();
        properties2.put("DB", "H2");
        properties2.put("bundle","true");
        Capability capHB = petalController.createCapability("Hibernate", "4.6", "provider", properties2);

        Map<String,String> properties2Bis = new HashMap<>();
        properties2Bis.put("DB", "H2");
        
        properties2Bis.put("bundle","true");
        Capability capHB1 = petalController.createCapability("Hibernate", "1.6", "provider", properties2Bis);

        Map<String,String> properties3 = new HashMap<>();
        properties3.put("DB", "H2");
        properties3.put("bundle","true");
        Capability capELink = petalController.createCapability("EclipseLink", "2.6", "provider", properties3);


        String filter1 = "(&(capabilityName=Jax-RS)(version=1.0)(bundle=true))";
        Requirement requirement1 = petalController.createRequirement("test1", "test1", filter1);

        String filter2 = "(&(capabilityName=JPA)(version>2.0)(bundle=false))";
        Requirement requirement2 = petalController.createRequirement("test2", "test2", filter2);

        String filter3 = "(&(|(capabilityName=Hibernate)(capabilityName=EclipseLink))(namespace=provider)(version>2.0))";
        Requirement requirement3 = petalController.createRequirement("test3", "test3", filter3);

        String filter4 = "(&(capabilityName=Hibernate)(version>1.0)(bundle=true))";
        Requirement requirement4 = petalController.createRequirement("test4", "test4", filter4);

        String filter5 = "(&(|(capabilityName=Hibernate)(capabilityName=EclipseLink))(namespace=provider)(version>2.0)";
        Requirement requirement5 = petalController.createRequirement("test5", "test5", filter5);

        String filter6 = "(&(capabilityName=Tomcat)(namespace=WebContainer))";;
        Requirement requirement6 = petalController.createRequirement("test6", "test6", filter6);

        Set<Capability> setCap0 = new HashSet<>();
        setCap0.add(capJX);  
        Set<Requirement> setReq0 = new HashSet<>();
        setReq0.add(requirement2);
        setReq0.add(requirement5);

        //Adding petal with capability Jax-RS and 2 requirement: JPA and (Hibernate or EclipseLink)
       petalController.addPetal(vendor.getVendorName(),"RestfulApp", "1.0.0", "Test", category, setReq0, setCap0, Origin.LOCAL, petalBinary);


        Set<Capability> setCap = new HashSet<>();
        setCap.add(capJX);  
        Set<Requirement> setReq = new HashSet<>();
        setReq.add(requirement2);
        setReq.add(requirement3);

        //Adding petal with capability Jax-RS and 2 requirement: JPA and (Hibernate or EclipseLink with bundle=true)
        petalController.addPetal(vendor.getVendorName(),"RestfulApp", "1.0.1", "Test", category, setReq, setCap, Origin.LOCAL, petalBinary);

        Set<Capability> setCap2 = new HashSet<>();
        setCap2.add(capJPA);
        Set<Requirement> setReq2 = new HashSet<>();
        setReq2.add(requirement4);

        //Adding petal given capability JPA  and had requirement for provider Hibernate 
        petalController.addPetal(vendor.getVendorName(), "Api", "1.0.1", "Test", category1, setReq2, setCap2, Origin.LOCAL, petalBinary);

        Set<Capability> setCap3 = new HashSet<>();
        setCap3.add(capHB);
        Set<Requirement> setReq3 = new HashSet<>();
        setReq3.add(requirement1);
        setReq3.add(requirement2);

        //Adding petal given capability Hibernate and had 2 requirement : Jax-RS and JPA
        petalController.addPetal(vendor.getVendorName(), "ProviderHB", "1.0.0", "Hibernate", category, setReq3, setCap3, Origin.LOCAL, petalBinary);

        Set<Capability> setCap4 = new HashSet<>();
        setCap4.add(capELink);
        Set<Requirement> setReq4 = new HashSet<>();
        setReq4.add(requirement2);

        //Adding petal given capability EclipseLink and requirement for JPA 
        petalController.addPetal(vendor.getVendorName(), "ProviderEL", "1.0.0", "EclipseLink", category, setReq4, setCap4, Origin.LOCAL, petalBinary);

        Set<Capability> setCap5 = new HashSet<>();
        setCap5.add(capHB);
        setCap5.add(capJX);
        Set<Requirement> setReq5 = new HashSet<>();
        setReq5.add(requirement2);
        setReq5.add(requirement6);
        //Adding petal given two capability (Hibernate and Jax-RS) and had a requirement for Tomcat and JPA
        petalController.addPetal(vendor.getVendorName(), "Test6", "1.4", "Test", category, setReq5, setCap5, Origin.LOCAL, petalBinary);

        Set<Capability> setCap6 = new HashSet<>(); 
        setCap6.add(capJPA1);
        setCap6.add(capHB1);
        Set<Requirement> setReq6 = new HashSet<>();
        //Adding petal given 2 capability : Hibernate and JPA bundle
        storeManagement.submitPetal(vendor.getVendorName(), "Test7", "1.4", "Test", category, setReq6, setCap6,petalBinary);
        

        System.out.println(" We have " + storeManagement.collectPetalsFromLocal().size() + " petals in the local repository");
        System.out.println(" We have " + storeManagement.collectPetalsFromStaging().size() + " petals in the staging repository");

        Collection<Petal> petals = userController.collectPetals("Administrator");
        System.out.println("The Administrator have access to " + petals.size() + " petals");
        
        System.out.println("After validate the petal in the stagging ");
        storeManagement.validatePetal(vendor.getVendorName(),"Test7", "1.4");
        storeManagement.validatePetal(vendor.getVendorName(),"Test7", "1.4");
        System.out.println(" Then We have " + storeManagement.collectPetalsFromLocal().size() + " petals in the local repository");

        String url = "https://store.peergreen.com";
        String description = "Peergreen central store";
        storeManagement.addLink(url, description);
        
        System.out.println(" We have " + storeManagement.collectPetalsFromRemote().size() + " petals in the remote repository");
        System.out.println("The Administrator have access to " + petals.size() + " petals");


    }
}
