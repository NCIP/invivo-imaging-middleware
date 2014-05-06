package gov.nih.nci.ivi.dsdrepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import gov.nih.nci.ivi.dsd.ContainerInfo;
import gov.nih.nci.ivi.dsd.PackageContainerRequisiteInfo;
import gov.nih.nci.ivi.dsd.PackageContainerURL;
import gov.nih.nci.ivi.dsd.PackageDeployedURL;
import gov.nih.nci.ivi.dsd.PackageRepositoryURL;
import gov.nih.nci.ivi.dsd.URL;
import gov.nih.nci.ivi.dsd._package;

public class PackageManager {

	public static void writeSamplePackageInfo(String fileName, _package packageInfo) {
		URL []urls = new URL[1];

		PackageContainerRequisiteInfo packageContainerRequisiteInfo = new PackageContainerRequisiteInfo();
		ContainerInfo containerInfo = new ContainerInfo();
		containerInfo.setCpuBits(32);
		containerInfo.setCpuCacheSizeInKB(512);
		containerInfo.setCpuClockRateInMHz(2800);
		containerInfo.setCpuCoreCount(2);
		containerInfo.setCpuCount(2);
		containerInfo.setCpuManufacturer("Intel");
		containerInfo.setCpuMaxSSELevel("?");
		containerInfo.setCpuModel("?");
		containerInfo.setDiskFreeSpaceInMB(300);
		containerInfo.setId(0);
		containerInfo.setKernelVersion("2.3.4");
		containerInfo.setMemorySizeInMB(1024);
		containerInfo.setOsBit(32);
		containerInfo.setOsFamily("Windows");
		containerInfo.setOsManufacturer("Microsoft");
		containerInfo.setOsVersion("XP");
		containerInfo.setSecureFlag(false);
		containerInfo.setType("?");
		containerInfo.setVersion("1.2");
		
		packageContainerRequisiteInfo.setContainerInfo(containerInfo);
		packageInfo.setContainerRequisiteInfo(packageContainerRequisiteInfo);

		PackageContainerURL packageContainerURL = new PackageContainerURL();
		urls[0] = new URL();
		urls[0].setId(0);
		urls[0].setValue("http://127.0.0.1:8080/wsrf/services/cagrid/DSDContainerService");
		packageContainerURL.setURL(urls[0]);
		packageInfo.setContainerURL(packageContainerURL);

		PackageDeployedURL packageDeployedURL = new PackageDeployedURL();
		urls[0] = new URL();
		urls[0].setId(0);
		urls[0].setValue("http://127.0.0.1:8080/wsrf/services/cagrid/SampleDataService");
		packageDeployedURL.setURL(urls[0]);
		packageInfo.setDeployedURL(packageDeployedURL);

		packageInfo.setIdentifier("sampledsdpackage.gar");
		packageInfo.setMajorVersion("1");
		packageInfo.setMinorVersion("2");
		packageInfo.setName("Sample DSD Package");

		PackageRepositoryURL packageRepositoryURL = new PackageRepositoryURL();
		urls[0] = new URL();
		urls[0].setId(0);
		urls[0].setValue("http://127.0.0.1:8080/wsrf/services/cagrid/DSDRepositoryDataService");
		packageRepositoryURL.setURL(urls);
		packageInfo.setRepositoryURL(packageRepositoryURL);

		FileOutputStream output = null;
		try {
			output = new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ObjectOutputStream objOut = null;
		try {
			objOut = new ObjectOutputStream(output);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			objOut.writeObject(packageInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static _package readPackageInfo(String fileName) {
		FileInputStream input = null;
		try {
			input = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		ObjectInputStream objIn = null;
		try {
			objIn = new ObjectInputStream(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		_package packageInfo = null;
		try {
			packageInfo = (_package) objIn.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return packageInfo;
	}

	public static void displayPackageInfo(_package packageInfo) {
		if (packageInfo == null) {
			System.out.println("packageInfo is null");
			return;
		}
		if (packageInfo.getContainerRequisiteInfo() != null && packageInfo.getContainerRequisiteInfo().getContainerInfo() != null) {
			System.out.println("CPU bits: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuBits());
			System.out.println("CPU cache size in Kb: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuCacheSizeInKB());
			System.out.println("CPU clock rate in MHz: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuClockRateInMHz());
			System.out.println("CPU core count: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuCoreCount());
			System.out.println("CPU cpu count: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuCount());
			System.out.println("CPU manufacturer: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuManufacturer());
			System.out.println("CPU max SSE level: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuMaxSSELevel());
			System.out.println("CPU model: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getCpuModel());
			System.out.println("Disk free space in MB: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getDiskFreeSpaceInMB());
			System.out.println("Unique Id: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getId());
			System.out.println("Kernel version: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getKernelVersion());
			System.out.println("Memory size in MB: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getMemorySizeInMB());
			System.out.println("OS bit: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getOsBit());
			System.out.println("OS family: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getOsFamily());
			System.out.println("OS manufacturer: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getOsManufacturer());
			System.out.println("OS version: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getOsVersion());
			System.out.println("Type: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getType());
			System.out.println("Unique Id: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getId());
			System.out.println("Version: " + packageInfo.getContainerRequisiteInfo().getContainerInfo().getVersion());
		}
		
		if (packageInfo.getContainerURL() != null && packageInfo.getContainerURL().getURL() != null)
			System.out.println("Container URL: " + packageInfo.getContainerURL().getURL().getValue());

		if (packageInfo.getDeployedURL() != null && packageInfo.getDeployedURL().getURL() != null)
			System.out.println("Deployed URL: " + packageInfo.getDeployedURL().getURL().getValue());

		System.out.println("Identifier: " + packageInfo.getIdentifier());
		System.out.println("MajorVersion: " + packageInfo.getMajorVersion());
		System.out.println("MinorVersion: " + packageInfo.getMinorVersion());
		System.out.println("Name: " + packageInfo.getName());

		if (packageInfo.getRepositoryURL() != null && packageInfo.getRepositoryURL().getURL() != null && packageInfo.getRepositoryURL().getURL()[0] != null)
			System.out.println("Repository URL: " + packageInfo.getRepositoryURL().getURL()[0].getValue());
	}

	public static void main(String[] args) {
		_package packageInfoWritten = new _package();
		writeSamplePackageInfo("/tmp/mySampleService.dat", packageInfoWritten);
		_package packageInfoRead = new _package();
		packageInfoRead = readPackageInfo("/tmp/mySampleService.dat");
		displayPackageInfo(packageInfoRead);
	}

}
