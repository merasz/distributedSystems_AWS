package homework1;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.waiters.WaiterParameters;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import java.util.Collections;
import java.util.List;

/*
* gruppe, markus köberle, michael hauser, nils rambacher
* */

public class TaskOne{

	public static final String keyName = "supertoll";
	public static final String securityGroupName = "launch-wizard-2";
	public static final String securityGroupDescription = "task2_description";
	public static final String amiId = "ami-0817d428a6fb68645";
	public static final String keyPairPath = "/home/michael/Schreibtisch/distri_task2/proSemDistrSysWS2021/Task2/supertoll.pem";
	public static final String csvFile = "input_full.csv";


	public static void main(String[] args) {
		//load aws credentials

		long start, end;
		start = System.currentTimeMillis();

		AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
		//create aws ec2 client
		AmazonEC2 ec2 = AmazonEC2ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_EAST_1)
				.build();

		//create KeyPair
/*		KeyPair keyPair = createKeyPair(ec2);
		String privateKey = keyPair.getKeyMaterial();*/

/*		//create securityGroup
		createSecurityGroup(ec2);*/

		//run the ec2
		RunInstancesResult runInstancesResult = startInstance(ec2, securityGroupName);
		System.out.println("Trying to create instance!\n");


		String instanceId = getInstanceId(ec2, runInstancesResult);
		String instancePublicIP = waitForInstanceToStart(ec2, instanceId);
		end = System.currentTimeMillis();

		System.out.println("Instance ID1: " + instanceId + " DNS: " + instancePublicIP);
		//System.out.println("time elapsed: " + (end - start) / 1000);

		try {
			Thread.sleep(15000);
		} catch (Exception e) {
			System.err.println(e);
		}

		System.out.println("Trying to send ssh command!\n");

		System.out.println("Command: sudo apt-get update\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo apt-get update");

		try {
			Thread.sleep(20000);
		} catch (Exception e) {
			System.err.println(e);
		}


		System.out.println("Command: sudo apt-get install docker-ce docker-ce-cli containerd.io\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo apt-get install --assume-yes docker.io");

		System.out.println("Command: sudo docker pull profdrsquad/javadocker:dockjava\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo docker pull profdrsquad/javadocker:dockjava");
/*
		System.out.println("Command: upload file!\n");
		try {
			SshCommands.whenUploadFileUsingJsch_thenSuccess(instancePublicIP, keyPairPath, "calc_fib.jar");
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}

		System.out.println("Command: upload file!\n");
		try {
			SshCommands.whenUploadFileUsingJsch_thenSuccess(instancePublicIP, keyPairPath, csvFile);
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}

		long time1start, time1end, time2start, time2end;
		time1start = System.currentTimeMillis();
		System.out.println("Compute!!\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo java -jar calc_fib.jar " + csvFile);
		time1end = System.currentTimeMillis();


		System.out.println("Command: download file 1!\n");
		try {
			SshCommands.whenDownloadFileUsingJsch_thenSuccess(instancePublicIP, keyPairPath, "output.csv");
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}

		System.out.println("time for comoutation file1:" + (time1end - time1start) / 1000 + " Sekunden!");
*/

		System.out.println("Command: sudo docker run --name fib profdrsquad/javadocker:dockjava\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo docker run --name runningdocker profdrsquad/javadocker:dockjava");

		System.out.println("Command: sudo docker cp fib:/usr/app/output.csv ./output.csv\n");
		SshCommands.sendCommandViaSsh(instancePublicIP, keyPairPath, "sudo docker cp runningdocker:/usr/app/output.csv ./output.csv");

		System.out.println("Command: download file 1!\n");
		try {
			SshCommands.whenDownloadFileUsingJsch_thenSuccess(instancePublicIP, keyPairPath, "output.csv");
		} catch (JSchException e) {
			e.printStackTrace();
		} catch (SftpException e) {
			e.printStackTrace();
		}





		System.out.println("Instance beenden ...");
		terminateInstance(ec2, instanceId);
		System.out.println("Instance beendet!");

		end = System.currentTimeMillis();
		System.out.println("Total time elapsed: " + (end - start) / 1000);
	}

	public static void createSecurityGroup(AmazonEC2 ec2) {
		CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
		csgr.withGroupName(securityGroupName).withDescription(securityGroupDescription);
		try {
			CreateSecurityGroupResult createSecurityGroupResult = ec2.createSecurityGroup(csgr);
			String ipAddr = "0.0.0.0/0";
			List<String> ipRanges = Collections.singletonList(ipAddr);
			IpPermission ipPermission = new IpPermission();
			ipPermission
					.withIpProtocol("tcp")
					.withFromPort(22)
					.withToPort(22)
					.withIpRanges(ipRanges);
			List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);
			AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(securityGroupName, ipPermissions);
			ec2.authorizeSecurityGroupIngress(ingressRequest);
		} catch (Exception e) {
			System.out.println("Security group has been created ALREADY!");
		}
	}

	public static KeyPair createKeyPair(AmazonEC2 ec2) {
		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
		createKeyPairRequest.withKeyName(keyName);
		//check that key does not exist
		DeleteKeyPairRequest deleteRequest = new DeleteKeyPairRequest();
		deleteRequest.setKeyName(keyName);
		try {
			ec2.deleteKeyPair(deleteRequest);
		} catch (Exception e) {
			//no previous key
		}
		CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);
		return createKeyPairResult.getKeyPair();
	}

	public static String getInstanceId(AmazonEC2 ec2, RunInstancesResult runInstancesResult) {
		String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
		return instanceId;
	}

	public static Integer getInstanceStatus(AmazonEC2 ec2, String instanceId) {
		DescribeInstancesRequest describeInstanceRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances(describeInstanceRequest);
		InstanceState state = describeInstanceResult.getReservations().get(0).getInstances().get(0).getState();
		return state.getCode();
	}

	public static void terminateInstance(AmazonEC2 ec2, String instanceId) {
		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
		ec2.stopInstances(request);
	}

	public static void deleteSecurityGroup(AmazonEC2 ec2, String securityGroupName) {
		DeleteSecurityGroupRequest deleteRequest = new DeleteSecurityGroupRequest()
				.withGroupId(securityGroupName);
		DeleteSecurityGroupResult deleteResponse = ec2.deleteSecurityGroup(deleteRequest);
	}

	public static String waitForInstanceToStart(AmazonEC2 ec2, String instanceId) {
		DescribeInstancesRequest describeRequest = new DescribeInstancesRequest();
		describeRequest.setInstanceIds(Collections.singleton(instanceId));

		ec2.waiters().instanceRunning().run(new WaiterParameters<DescribeInstancesRequest>(describeRequest));
		DescribeInstancesResult result = ec2.describeInstances(describeRequest);
		Instance instance = result.getReservations().get(0).getInstances().get(0);
		return instance.getPublicIpAddress();
	}

	public static RunInstancesResult startInstance(AmazonEC2 ec2, String securityGroupName) {
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
				.withImageId(amiId)
				.withInstanceType(InstanceType.T2Micro)
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName(keyName)
				.withSecurityGroups(securityGroupName);
		return ec2.runInstances(runInstancesRequest);
	}
}

/*File keyFile = new File(keyName + ".pem");
KeyPair keyPair = keyPairResult.getKeyPair();

String privateKey = keyPair.getKeyMaterial();
FileWriter fw;

fw = new FileWriter(keyFile);
fw.write(privateKey);
fw.close();*/